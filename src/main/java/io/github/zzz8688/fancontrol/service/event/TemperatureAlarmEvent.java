package io.github.zzz8688.fancontrol.service.event;

/**
 * 业务板超温事件。替代课程版手写的 AlarmObserver/notifyObserver：
 * 业务侧只负责发布事件，告警如何处置（落库、后续可扩展通知）由监听器决定。
 */
public record TemperatureAlarmEvent(
        int slot,
        double temperature,
        long timestampMs
) {
}
