package io.github.zzz8688.fancontrol.mapper;

import io.github.zzz8688.fancontrol.domain.FanBoardModeType;
import io.github.zzz8688.fancontrol.domain.FanSpeed;
import io.github.zzz8688.fancontrol.entity.AlarmRecord;
import io.github.zzz8688.fancontrol.entity.BusinessBoard;
import io.github.zzz8688.fancontrol.entity.FanBoard;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@MybatisTest
@Sql(scripts = "/schema.sql")
class BoardMapperTest {

    @Autowired FanBoardMapper fanBoardMapper;
    @Autowired BusinessBoardMapper businessBoardMapper;
    @Autowired AlarmRecordMapper alarmRecordMapper;

    private LocalDateTime now() {
        return LocalDateTime.now();
    }

    @Test
    void fanBoardCrudAndConditionalUpdate() {
        fanBoardMapper.insert(new FanBoard(90, FanSpeed.LOW, FanBoardModeType.AUTOMATIC, now()));
        fanBoardMapper.insert(new FanBoard(91, FanSpeed.LOW, FanBoardModeType.MANUAL, now()));

        assertEquals(2, fanBoardMapper.count());
        assertEquals(FanSpeed.LOW, fanBoardMapper.selectBySlot(90).getSpeed());
        assertEquals(FanBoardModeType.MANUAL, fanBoardMapper.selectBySlot(91).getMode());

        // 条件更新：只对 AUTOMATIC 板生效，MANUAL 板不被自动调速覆盖
        int updatedAuto = fanBoardMapper.updateSpeedIfAutomatic(90, FanSpeed.HIGH, now());
        int updatedManual = fanBoardMapper.updateSpeedIfAutomatic(91, FanSpeed.HIGH, now());
        assertEquals(1, updatedAuto);
        assertEquals(0, updatedManual);
        assertEquals(FanSpeed.HIGH, fanBoardMapper.selectBySlot(90).getSpeed());
        assertEquals(FanSpeed.LOW, fanBoardMapper.selectBySlot(91).getSpeed());

        // 手动调速与切模式不受限
        fanBoardMapper.updateSpeed(91, FanSpeed.MEDIUM, now());
        fanBoardMapper.updateMode(91, FanBoardModeType.AUTOMATIC, now());
        assertEquals(FanSpeed.MEDIUM, fanBoardMapper.selectBySlot(91).getSpeed());
        assertEquals(FanBoardModeType.AUTOMATIC, fanBoardMapper.selectBySlot(91).getMode());
    }

    @Test
    void businessBoardUpdateTemperature() {
        businessBoardMapper.insert(new BusinessBoard(1, 20.0, now()));
        businessBoardMapper.updateTemperature(1, 42.5, now());
        assertEquals(42.5, businessBoardMapper.selectAll().get(0).getTemperature());
    }

    @Test
    void alarmRecordInsertAndRecentOrdering() {
        alarmRecordMapper.insert(new AlarmRecord(1, 76.1, now().minusSeconds(10)));
        alarmRecordMapper.insert(new AlarmRecord(2, 88.0, now()));

        List<AlarmRecord> recent = alarmRecordMapper.selectRecent(10);
        assertEquals(2, recent.size());
        // 按时间倒序，最新告警在前
        assertEquals(2, recent.get(0).getSlot());
        assertNotNull(recent.get(0).getId());
    }
}
