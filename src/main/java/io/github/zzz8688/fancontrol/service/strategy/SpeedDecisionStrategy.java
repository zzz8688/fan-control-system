package io.github.zzz8688.fancontrol.service.strategy;

import io.github.zzz8688.fancontrol.domain.FanSpeed;

import java.util.List;

/**
 * 风扇档位决策策略。
 * 该逻辑是主控板定时任务里的 high/medium/low 
 * 重构为策略接口。
 */
public interface SpeedDecisionStrategy {

    /**
     * 根据全部业务板的当前温度决定目标档位。
     *
     * @param temperatures 按槽位有序的业务板温度
     * @return 目标风扇档位
     */
    FanSpeed decide(List<Double> temperatures);
}
