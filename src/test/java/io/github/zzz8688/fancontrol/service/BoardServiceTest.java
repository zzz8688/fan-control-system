package io.github.zzz8688.fancontrol.service;

import io.github.zzz8688.fancontrol.config.FanControlProperties;
import io.github.zzz8688.fancontrol.domain.FanBoardModeType;
import io.github.zzz8688.fancontrol.domain.FanSpeed;
import io.github.zzz8688.fancontrol.entity.BusinessBoard;
import io.github.zzz8688.fancontrol.entity.FanBoard;
import io.github.zzz8688.fancontrol.exception.BoardModeConflictException;
import io.github.zzz8688.fancontrol.exception.BoardNotFoundException;
import io.github.zzz8688.fancontrol.mapper.AlarmRecordMapper;
import io.github.zzz8688.fancontrol.mapper.BusinessBoardMapper;
import io.github.zzz8688.fancontrol.mapper.FanBoardMapper;
import io.github.zzz8688.fancontrol.service.event.TemperatureAlarmEvent;
import io.github.zzz8688.fancontrol.service.strategy.SpeedDecisionStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BoardServiceTest {

    @Mock FanBoardMapper fanBoardMapper;
    @Mock BusinessBoardMapper businessBoardMapper;
    @Mock AlarmRecordMapper alarmRecordMapper;
    @Mock TemperatureSimulator simulator;
    @Mock SpeedDecisionStrategy speedDecisionStrategy;
    @Mock ApplicationEventPublisher eventPublisher;

    private BoardService boardService;

    @BeforeEach
    void setUp() {
        boardService = new BoardService(fanBoardMapper, businessBoardMapper, alarmRecordMapper,
                simulator, speedDecisionStrategy, eventPublisher, new FanControlProperties());
    }

    private FanBoard fan(int slot, FanSpeed speed, FanBoardModeType mode) {
        return new FanBoard(slot, speed, mode, LocalDateTime.now());
    }

    @Test
    @DisplayName("自动模式下手动调速抛 409 冲突")
    void manualAdjustRejectedInAutomaticMode() {
        when(fanBoardMapper.selectBySlot(90))
                .thenReturn(fan(90, FanSpeed.HIGH, FanBoardModeType.AUTOMATIC));
        assertThrows(BoardModeConflictException.class,
                () -> boardService.manualAdjust(90, FanSpeed.LOW));
        verify(fanBoardMapper, never()).updateSpeed(anyInt(), any(), any());
    }

    @Test
    @DisplayName("槽位不存在抛 404")
    void manualAdjustUnknownSlot() {
        when(fanBoardMapper.selectBySlot(99)).thenReturn(null);
        assertThrows(BoardNotFoundException.class,
                () -> boardService.manualAdjust(99, FanSpeed.HIGH));
    }

    @Test
    @DisplayName("手动模式下调速成功并落库")
    void manualAdjustSucceedsInManualMode() {
        when(fanBoardMapper.selectBySlot(90))
                .thenReturn(fan(90, FanSpeed.LOW, FanBoardModeType.MANUAL));
        boardService.manualAdjust(90, FanSpeed.HIGH);
        verify(fanBoardMapper).updateSpeed(eq(90), eq(FanSpeed.HIGH), any(LocalDateTime.class));
    }

    @Test
    @DisplayName("切换模式时槽位不存在抛 404")
    void changeModeUnknownSlot() {
        when(fanBoardMapper.selectBySlot(99)).thenReturn(null);
        assertThrows(BoardNotFoundException.class,
                () -> boardService.changeMode(99, FanBoardModeType.AUTOMATIC));
    }

    @Test
    @DisplayName("tick：温度从 75 跨到 76 才告警（边沿触发），自动板按策略更新")
    void tickEmitsAlarmOnlyOnThresholdCross() {
        BusinessBoard board = new BusinessBoard(1, 75.0, LocalDateTime.now());
        FanBoard automatic = fan(90, FanSpeed.MEDIUM, FanBoardModeType.AUTOMATIC);
        FanBoard manual = fan(91, FanSpeed.LOW, FanBoardModeType.MANUAL);
        when(businessBoardMapper.selectAll()).thenReturn(List.of(board));
        when(fanBoardMapper.selectAll()).thenReturn(List.of(automatic, manual));
        when(simulator.evolve(any(), any())).thenReturn(Map.of(1, 76.0));
        when(speedDecisionStrategy.decide(any())).thenReturn(FanSpeed.HIGH);

        boardService.controlTick();

        verify(businessBoardMapper).updateTemperature(eq(1), eq(76.0), any(LocalDateTime.class));
        // 是否实际生效由 SQL 的 mode='AUTOMATIC' 条件兜底，这里验证两块板都走了同一更新入口
        verify(fanBoardMapper).updateSpeedIfAutomatic(eq(90), eq(FanSpeed.HIGH), any(LocalDateTime.class));
        verify(fanBoardMapper).updateSpeedIfAutomatic(eq(91), eq(FanSpeed.HIGH), any(LocalDateTime.class));

        ArgumentCaptor<TemperatureAlarmEvent> captor =
                ArgumentCaptor.forClass(TemperatureAlarmEvent.class);
        verify(eventPublisher, times(1)).publishEvent(captor.capture());
        TemperatureAlarmEvent event = captor.getValue();
        assertEquals(1, event.slot());
        assertEquals(76.0, event.temperature());
    }

    @Test
    @DisplayName("tick：温度已在阈值上方（76->77）不重复告警")
    void tickNoAlarmWhenAlreadyAboveThreshold() {
        BusinessBoard board = new BusinessBoard(1, 76.0, LocalDateTime.now());
        when(businessBoardMapper.selectAll()).thenReturn(List.of(board));
        when(fanBoardMapper.selectAll()).thenReturn(List.of(fan(90, FanSpeed.HIGH, FanBoardModeType.AUTOMATIC)));
        when(simulator.evolve(any(), any())).thenReturn(Map.of(1, 77.0));
        when(speedDecisionStrategy.decide(any())).thenReturn(FanSpeed.HIGH);

        boardService.controlTick();

        verify(eventPublisher, never()).publishEvent(any(TemperatureAlarmEvent.class));
    }

    @Test
    @DisplayName("tick：无业务板时空转返回")
    void tickEmptyBoards() {
        when(businessBoardMapper.selectAll()).thenReturn(List.of());
        boardService.controlTick();
        verify(simulator, never()).evolve(any(), any());
    }
}
