package io.github.zzz8688.fancontrol.service.strategy;

import io.github.zzz8688.fancontrol.domain.FanSpeed;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 与课程版完全同口径的聚合策略：
 * 单板建议档位：温度 <= 35 LOW，(35, 60] MEDIUM，> 60 HIGH；
 * 全局档位：任一业务板建议 HIGH 则 HIGH；否则任一 MEDIUM 则 MEDIUM；否则 LOW。
 */
@Component
public class AggregateSpeedStrategy implements SpeedDecisionStrategy {

    static final double MEDIUM_THRESHOLD = 35.0;
    static final double HIGH_THRESHOLD = 60.0;

    @Override
    public FanSpeed decide(List<Double> temperatures) {
        if (temperatures == null || temperatures.isEmpty()) {
            return FanSpeed.LOW;
        }
        boolean anyMedium = false;
        for (double temperature : temperatures) {
            if (temperature > HIGH_THRESHOLD) {
                return FanSpeed.HIGH;
            }
            if (temperature > MEDIUM_THRESHOLD) {
                anyMedium = true;
            }
        }
        return anyMedium ? FanSpeed.MEDIUM : FanSpeed.LOW;
    }
}
