package io.github.zzz8688.fancontrol.service;

import io.github.zzz8688.fancontrol.domain.FanSpeed;
import io.github.zzz8688.fancontrol.entity.BusinessBoard;
import io.github.zzz8688.fancontrol.entity.FanBoard;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * 温度演化仿真器，规则与课程版 scheduledTask 中的 simulate 完全一致：
 * 每秒每块业务板自然升温 0~0.5 度，再按所有风扇板的运行档位随机降温
 * （HIGH 单块 0~1.0、MEDIUM 0~0.3、LOW 0~0.1），最终温度钳制在 [10, 100]。
 */
@Component
public class TemperatureSimulator {

    static final double MIN_TEMPERATURE = 10.0;
    static final double MAX_TEMPERATURE = 100.0;

    private final Random random;

    public TemperatureSimulator() {
        this(new Random());
    }

    public TemperatureSimulator(Random random) {
        this.random = random;
    }

    /**
     * @param businessBoards 仿真前的业务板（带旧温度）
     * @param fanBoards      当前全部风扇板（决定降温强度）
     * @return slot -> 新温度（保持入参槽位顺序）
     */
    public Map<Integer, Double> evolve(List<BusinessBoard> businessBoards, List<FanBoard> fanBoards) {
        int low = 0;
        int medium = 0;
        int high = 0;
        for (FanBoard fanBoard : fanBoards) {
            FanSpeed speed = fanBoard.getSpeed();
            if (speed == FanSpeed.HIGH) {
                high++;
            } else if (speed == FanSpeed.MEDIUM) {
                medium++;
            } else {
                low++;
            }
        }

        Map<Integer, Double> result = new LinkedHashMap<>();
        for (BusinessBoard board : businessBoards) {
            double temperature = board.getTemperature() + 0.5 * random.nextDouble();
            if (high > 0) {
                temperature -= 1.0 * high * random.nextDouble();
            }
            if (medium > 0) {
                temperature -= 0.3 * medium * random.nextDouble();
            }
            if (low > 0) {
                temperature -= 0.1 * low * random.nextDouble();
            }
            result.put(board.getSlot(), clamp(temperature));
        }
        return result;
    }

    private double clamp(double temperature) {
        return Math.min(MAX_TEMPERATURE, Math.max(MIN_TEMPERATURE, temperature));
    }
}
