package io.github.zzz8688.fancontrol.service;

import io.github.zzz8688.fancontrol.entity.BusinessBoard;
import io.github.zzz8688.fancontrol.entity.FanBoard;
import io.github.zzz8688.fancontrol.domain.FanBoardModeType;
import io.github.zzz8688.fancontrol.domain.FanSpeed;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TemperatureSimulatorTest {

    private List<BusinessBoard> boards(double temperature) {
        return List.of(new BusinessBoard(1, temperature, null));
    }

    @Test
    @DisplayName("自然升温有界：无有效降温时新温度落在 [旧值, 旧值+0.5]")
    void naturalHeatUpBounded() {
        // LOW 档单块板最多降温 0.1，使用固定种子只验证总体钳制区间
        TemperatureSimulator simulator = new TemperatureSimulator(new java.util.Random(0));
        List<FanBoard> fans = List.of(
                new FanBoard(90, FanSpeed.LOW, FanBoardModeType.AUTOMATIC, null));
        Map<Integer, Double> result = simulator.evolve(boards(20.0), fans);
        double next = result.get(1);
        assertTrue(next >= 10.0 && next <= 100.0);
        assertTrue(next <= 20.5);
    }

    @Test
    @DisplayName("温度钳制不低于 10 摄氏度")
    void lowerClamp() {
        TemperatureSimulator simulator = new TemperatureSimulator(new java.util.Random(1));
        Map<Integer, Double> result = simulator.evolve(
                boards(10.0),
                List.of(new FanBoard(90, FanSpeed.HIGH, FanBoardModeType.AUTOMATIC, null),
                        new FanBoard(91, FanSpeed.HIGH, FanBoardModeType.AUTOMATIC, null)));
        assertEquals(10.0, result.get(1));
    }

    @Test
    @DisplayName("温度钳制不超过 100 摄氏度")
    void upperClamp() {
        TemperatureSimulator simulator = new TemperatureSimulator(new java.util.Random(0));
        Map<Integer, Double> result = simulator.evolve(
                boards(100.0),
                List.of(new FanBoard(90, FanSpeed.LOW, FanBoardModeType.MANUAL, null)));
        assertEquals(100.0, result.get(1));
    }
}
