package io.github.zzz8688.fancontrol.service;

import io.github.zzz8688.fancontrol.config.FanControlProperties;
import io.github.zzz8688.fancontrol.domain.FanBoardModeType;
import io.github.zzz8688.fancontrol.domain.FanSpeed;
import io.github.zzz8688.fancontrol.entity.AlarmRecord;
import io.github.zzz8688.fancontrol.entity.BusinessBoard;
import io.github.zzz8688.fancontrol.entity.FanBoard;
import io.github.zzz8688.fancontrol.exception.BoardModeConflictException;
import io.github.zzz8688.fancontrol.exception.BoardNotFoundException;
import io.github.zzz8688.fancontrol.mapper.AlarmRecordMapper;
import io.github.zzz8688.fancontrol.mapper.BusinessBoardMapper;
import io.github.zzz8688.fancontrol.mapper.FanBoardMapper;
import io.github.zzz8688.fancontrol.service.event.TemperatureAlarmEvent;
import io.github.zzz8688.fancontrol.service.strategy.SpeedDecisionStrategy;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 主控板，单例模式。
 *
 * 本类是机房风扇控制系统的统一入口：对外承接 REST 手动操作，对内承接每秒 tick 调度，
 * 并通过 MyBatis 持久化全部板卡状态与告警记录。容器内只存在一个实例（@Scope("singleton")），
 * 等价于 GoF 手写双重检查锁单例主控板，但由 Spring 容器托管生命周期，避免手写同步的复杂性。
 */
@Service
@Scope("singleton")
public class BoardService {

    private final FanBoardMapper fanBoardMapper;
    private final BusinessBoardMapper businessBoardMapper;
    private final AlarmRecordMapper alarmRecordMapper;
    private final TemperatureSimulator simulator;
    private final SpeedDecisionStrategy speedDecisionStrategy;
    private final ApplicationEventPublisher eventPublisher;
    private final FanControlProperties properties;

    public BoardService(FanBoardMapper fanBoardMapper,
                        BusinessBoardMapper businessBoardMapper,
                        AlarmRecordMapper alarmRecordMapper,
                        TemperatureSimulator simulator,
                        SpeedDecisionStrategy speedDecisionStrategy,
                        ApplicationEventPublisher eventPublisher,
                        FanControlProperties properties) {
        this.fanBoardMapper = fanBoardMapper;
        this.businessBoardMapper = businessBoardMapper;
        this.alarmRecordMapper = alarmRecordMapper;
        this.simulator = simulator;
        this.speedDecisionStrategy = speedDecisionStrategy;
        this.eventPublisher = eventPublisher;
        this.properties = properties;
    }

    public List<FanBoard> listFanBoards() {
        return fanBoardMapper.selectAll();
    }

    public List<BusinessBoard> listBusinessBoards() {
        return businessBoardMapper.selectAll();
    }

    public List<AlarmRecord> listRecentAlarms(int limit) {
        return alarmRecordMapper.selectRecent(limit);
    }

    /**
     * 手动调速。
     * 模式冲突检查下沉到 FanBoard.adjustSpeed()，service 只负责定位板卡、
     * 调领域方法、持久化。
     */
    public FanBoard manualAdjust(int slot, FanSpeed speed) {
        FanBoard board = requireFanBoard(slot);
        LocalDateTime now = LocalDateTime.now();
        board.adjustSpeed(speed, now);              // 不变量由对象自身守卫
        fanBoardMapper.updateSpeed(slot, speed, now);
        return requireFanBoard(slot);
    }

    public FanBoard changeMode(int slot, FanBoardModeType mode) {
        requireFanBoard(slot);
        LocalDateTime now = LocalDateTime.now();
        fanBoardMapper.updateMode(slot, mode, now);

        // 切到 AUTOMATIC 时立即做一次档位评估，不等下一秒调度 tick
        if (mode == FanBoardModeType.AUTOMATIC) {
            List<Double> temps = businessBoardMapper.selectAll().stream()
                    .map(BusinessBoard::getTemperature)
                    .toList();
            FanSpeed targetSpeed = speedDecisionStrategy.decide(temps);
            fanBoardMapper.updateSpeed(slot, targetSpeed, now);
        }

        return requireFanBoard(slot);
    }

    private FanBoard requireFanBoard(int slot) {
        FanBoard board = fanBoardMapper.selectBySlot(slot);
        if (board == null) {
            throw new BoardNotFoundException(slot);
        }
        return board;
    }

    /**
     * 每秒控制 tick（课程版两个并发 Runnable 的串行化重构）：
     * 1. 读旧温度 -> 2. 仿真演化并落库 -> 3. 策略决策档位，仅更新 AUTOMATIC 风扇板
     * -> 4. 温度“从阈值内跨到阈值外”时发超温事件（边沿触发，不重复告警）。
     */
    public void controlTick() {
        List<BusinessBoard> before = businessBoardMapper.selectAll();
        if (before.isEmpty()) {
            return;
        }
        List<FanBoard> fanBoards = fanBoardMapper.selectAll();
        LocalDateTime now = LocalDateTime.now();

        Map<Integer, Double> nextTemperatures = simulator.evolve(before, fanBoards);
        nextTemperatures.forEach((slot, temperature) ->
                businessBoardMapper.updateTemperature(slot, temperature, now));

        FanSpeed targetSpeed = speedDecisionStrategy.decide(new ArrayList<>(nextTemperatures.values()));
        for (FanBoard fanBoard : fanBoards) {
            fanBoardMapper.updateSpeedIfAutomatic(fanBoard.getSlot(), targetSpeed, now);
        }

        double threshold = properties.getAlarm().getThreshold();
        for (BusinessBoard oldBoard : before) {
            double newTemperature = nextTemperatures.get(oldBoard.getSlot());
            if (oldBoard.getTemperature() <= threshold && newTemperature > threshold) {
                eventPublisher.publishEvent(new TemperatureAlarmEvent(
                        oldBoard.getSlot(), newTemperature, System.currentTimeMillis()));
            }
        }
    }
}
