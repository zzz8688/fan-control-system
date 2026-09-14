package io.github.zzz8688.fancontrol.entity;

import io.github.zzz8688.fancontrol.domain.FanBoardModeType;
import io.github.zzz8688.fancontrol.domain.FanSpeed;
import io.github.zzz8688.fancontrol.exception.BoardModeConflictException;

import java.time.LocalDateTime;

/**
 * 风扇扩展控制板（槽位 90、91 两块）。
 *
 * 充血模型：不变量由对象自身守卫。手动调速是板卡的行为，不是 service 层的 if-else。
 */
public class FanBoard {

    private Integer slot;
    private FanSpeed speed;
    private FanBoardModeType mode;
    private LocalDateTime updatedAt;

    public FanBoard() {
    }

    public FanBoard(Integer slot, FanSpeed speed, FanBoardModeType mode, LocalDateTime updatedAt) {
        this.slot = slot;
        this.speed = speed;
        this.mode = mode;
        this.updatedAt = updatedAt;
    }

    /**
     * 手动调速。
     *
     * 仅手动模式允许调整。若当前为自动模式，调用方不能绕过本方法直接改 speed，
     * 否则"自动模式拒绝手动操作"这条业务不变量就失去了唯一守卫点。
     *
     * @param target  目标档位
     * @param now     本次操作时间戳
     * @throws BoardModeConflictException 自动模式下调用
     */
    public void adjustSpeed(FanSpeed target, LocalDateTime now) {
        if (this.mode == FanBoardModeType.AUTOMATIC) {
            throw new BoardModeConflictException(this.slot, this.mode);
        }
        this.speed = target;
        this.updatedAt = now;
    }

    public Integer getSlot() { return slot; }
    public void setSlot(Integer slot) { this.slot = slot; }
    public FanSpeed getSpeed() { return speed; }
    public void setSpeed(FanSpeed speed) { this.speed = speed; }
    public FanBoardModeType getMode() { return mode; }
    public void setMode(FanBoardModeType mode) { this.mode = mode; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
