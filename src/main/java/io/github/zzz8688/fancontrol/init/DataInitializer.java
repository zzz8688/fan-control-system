package io.github.zzz8688.fancontrol.init;

import io.github.zzz8688.fancontrol.domain.FanBoardModeType;
import io.github.zzz8688.fancontrol.domain.FanSpeed;
import io.github.zzz8688.fancontrol.entity.BusinessBoard;
import io.github.zzz8688.fancontrol.entity.FanBoard;
import io.github.zzz8688.fancontrol.mapper.AlarmRecordMapper;
import io.github.zzz8688.fancontrol.mapper.BusinessBoardMapper;
import io.github.zzz8688.fancontrol.mapper.FanBoardMapper;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * 初始化板卡：
 * 风扇扩展板 90、91，初始 LOW/MANUAL；业务板 1-14，初始温度 20 摄氏度。
 * 每次应用启动清空旧数据、重新播种，保证状态可预测。
 */
@Component
public class DataInitializer implements ApplicationRunner {

    static final int FAN_SLOT_START = 90;
    static final int FAN_SLOT_END = 91;
    static final int BUSINESS_SLOT_START = 1;
    static final int BUSINESS_SLOT_END = 14;
    static final double INITIAL_TEMPERATURE = 20.0;

    private final FanBoardMapper fanBoardMapper;
    private final BusinessBoardMapper businessBoardMapper;
    private final AlarmRecordMapper alarmRecordMapper;

    public DataInitializer(FanBoardMapper fanBoardMapper,
                           BusinessBoardMapper businessBoardMapper,
                           AlarmRecordMapper alarmRecordMapper) {
        this.fanBoardMapper = fanBoardMapper;
        this.businessBoardMapper = businessBoardMapper;
        this.alarmRecordMapper = alarmRecordMapper;
    }

    @Override
    public void run(ApplicationArguments args) {
        LocalDateTime now = LocalDateTime.now();
        // 清空板卡状态与告警记录，每次启动回到纯净初始态
        fanBoardMapper.deleteAll();
        businessBoardMapper.deleteAll();
        alarmRecordMapper.deleteAll();

        for (int slot = FAN_SLOT_START; slot <= FAN_SLOT_END; slot++) {
            fanBoardMapper.insert(new FanBoard(slot, FanSpeed.LOW, FanBoardModeType.MANUAL, now));
        }
        for (int slot = BUSINESS_SLOT_START; slot <= BUSINESS_SLOT_END; slot++) {
            businessBoardMapper.insert(new BusinessBoard(slot, INITIAL_TEMPERATURE, now));
        }
    }
}
