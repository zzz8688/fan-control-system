package io.github.zzz8688.fancontrol.service.event;

import io.github.zzz8688.fancontrol.entity.AlarmRecord;
import io.github.zzz8688.fancontrol.mapper.AlarmRecordMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * 超温事件监听器：等价于课程版的 AlarmConcreteObserver，
 * 但告警动作从控制台打印升级为持久化告警记录。
 * 异步执行，避免落库抖动影响每秒控制主循环。
 */
@Component
public class TemperatureAlarmListener {

    private static final Logger log = LoggerFactory.getLogger(TemperatureAlarmListener.class);

    private final AlarmRecordMapper alarmRecordMapper;

    public TemperatureAlarmListener(AlarmRecordMapper alarmRecordMapper) {
        this.alarmRecordMapper = alarmRecordMapper;
    }

    @Async
    @EventListener
    public void onTemperatureAlarm(TemperatureAlarmEvent event) {
        AlarmRecord record = new AlarmRecord(
                event.slot(),
                event.temperature(),
                LocalDateTime.now()
        );
        alarmRecordMapper.insert(record);
        log.warn("业务板 {} 温度 {} 超过警戒值，已生成告警记录 id={}",
                event.slot(), String.format("%.2f", event.temperature()), record.getId());
    }
}
