package io.github.zzz8688.fancontrol.entity;

import java.time.LocalDateTime;

/** 业务板：槽位 1-14，带实时温度 */
public class BusinessBoard {

    private Integer slot;
    private Double temperature;
    private LocalDateTime updatedAt;

    public BusinessBoard() {
    }

    public BusinessBoard(Integer slot, Double temperature, LocalDateTime updatedAt) {
        this.slot = slot;
        this.temperature = temperature;
        this.updatedAt = updatedAt;
    }

    public Integer getSlot() { return slot; }
    public void setSlot(Integer slot) { this.slot = slot; }
    public Double getTemperature() { return temperature; }
    public void setTemperature(Double temperature) { this.temperature = temperature; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
