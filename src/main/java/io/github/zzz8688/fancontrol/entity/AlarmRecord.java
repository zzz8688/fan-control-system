package io.github.zzz8688.fancontrol.entity;

import java.time.LocalDateTime;

/** 超温告警记录：温度跨越告警阈值（边沿触发）时落库一条 */
public class AlarmRecord {

    private Long id;
    private Integer slot;
    private Double temperature;
    private LocalDateTime alarmTime;

    public AlarmRecord() {
    }

    public AlarmRecord(Integer slot, Double temperature, LocalDateTime alarmTime) {
        this.slot = slot;
        this.temperature = temperature;
        this.alarmTime = alarmTime;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Integer getSlot() { return slot; }
    public void setSlot(Integer slot) { this.slot = slot; }
    public Double getTemperature() { return temperature; }
    public void setTemperature(Double temperature) { this.temperature = temperature; }
    public LocalDateTime getAlarmTime() { return alarmTime; }
    public void setAlarmTime(LocalDateTime alarmTime) { this.alarmTime = alarmTime; }
}
