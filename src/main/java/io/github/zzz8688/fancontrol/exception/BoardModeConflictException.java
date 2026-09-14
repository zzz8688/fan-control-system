package io.github.zzz8688.fancontrol.exception;

import io.github.zzz8688.fancontrol.domain.FanBoardModeType;

/**
 * 自动模式下收到手动调速请求。对应课程版控制台的提示语
 * “当前选择风扇板为自动工作模式，不能手工调速”，在 HTTP 语义上映射为 409。
 */
public class BoardModeConflictException extends RuntimeException {

    private final int slot;
    private final FanBoardModeType currentMode;

    public BoardModeConflictException(int slot, FanBoardModeType currentMode) {
        super("风扇板 " + slot + " 当前为 " + currentMode + " 模式，不能手工调速");
        this.slot = slot;
        this.currentMode = currentMode;
    }

    public int getSlot() {
        return slot;
    }

    public FanBoardModeType getCurrentMode() {
        return currentMode;
    }
}
