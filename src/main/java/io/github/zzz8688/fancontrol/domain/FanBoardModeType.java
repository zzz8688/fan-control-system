package io.github.zzz8688.fancontrol.domain;

/**
 * 风扇板工作模式：MANUAL 仅接受手动调速；AUTOMATIC 由每秒控制 tick 自动调速，
 * 自动模式下手动调速请求返回 409 冲突。
 */
public enum FanBoardModeType {
    MANUAL,
    AUTOMATIC
}
