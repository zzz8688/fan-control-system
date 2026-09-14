package io.github.zzz8688.fancontrol.domain;

/**
 * 风扇档位。原版按温度区间给出建议档位：
 * 温度 <= 35 为 LOW，(35, 60] 为 MEDIUM，> 60 为 HIGH。
 */
public enum FanSpeed {
    LOW,
    MEDIUM,
    HIGH
}
