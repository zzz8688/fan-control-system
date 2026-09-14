package io.github.zzz8688.fancontrol.service.strategy;

import io.github.zzz8688.fancontrol.domain.FanSpeed;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AggregateSpeedStrategyTest {

    private final AggregateSpeedStrategy strategy = new AggregateSpeedStrategy();

    @Test
    @DisplayName("空列表默认 LOW")
    void emptyListReturnsLow() {
        assertEquals(FanSpeed.LOW, strategy.decide(List.of()));
    }

    @Test
    @DisplayName("温度 <= 35 全部 LOW")
    void allLow() {
        assertEquals(FanSpeed.LOW, strategy.decide(List.of(20.0, 35.0, 10.0)));
    }

    @Test
    @DisplayName("35 < 温度 <= 60 任一存在 -> MEDIUM（边界 35 归 LOW，60 归 MEDIUM）")
    void mediumBand() {
        assertEquals(FanSpeed.MEDIUM, strategy.decide(List.of(35.0, 35.01)));
        assertEquals(FanSpeed.MEDIUM, strategy.decide(List.of(20.0, 60.0)));
    }

    @Test
    @DisplayName("任一业务板温度 > 60 -> HIGH")
    void anyHigh() {
        assertEquals(FanSpeed.HIGH, strategy.decide(List.of(20.0, 60.01, 35.0)));
        assertEquals(FanSpeed.HIGH, strategy.decide(List.of(100.0)));
    }
}
