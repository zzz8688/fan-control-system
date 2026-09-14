package io.github.zzz8688.fancontrol.exception;

/** 槽位不存在 */
public class BoardNotFoundException extends RuntimeException {

    private final int slot;

    public BoardNotFoundException(int slot) {
        super("槽位 " + slot + " 不存在");
        this.slot = slot;
    }

    public int getSlot() {
        return slot;
    }
}
