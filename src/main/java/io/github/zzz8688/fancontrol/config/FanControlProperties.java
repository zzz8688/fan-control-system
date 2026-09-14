package io.github.zzz8688.fancontrol.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 业务参数：对应课程版控制台程序中的硬编码常量（调度周期 1s、告警阈值 75 摄氏度）。
 */
@ConfigurationProperties(prefix = "fancontrol")
public class FanControlProperties {

    private Tick tick = new Tick();
    private Alarm alarm = new Alarm();

    public Tick getTick() { return tick; }
    public void setTick(Tick tick) { this.tick = tick; }
    public Alarm getAlarm() { return alarm; }
    public void setAlarm(Alarm alarm) { this.alarm = alarm; }

    public static class Tick {
        /** 是否启用每秒仿真/调速调度，测试上下文可关闭 */
        private boolean enabled = true;
        private long intervalMs = 1000;

        public boolean isEnabled() { return enabled; }
        public void setEnabled(boolean enabled) { this.enabled = enabled; }
        public long getIntervalMs() { return intervalMs; }
        public void setIntervalMs(long intervalMs) { this.intervalMs = intervalMs; }
    }

    public static class Alarm {
        /** 业务板超温告警阈值（摄氏度），原版逻辑为 temperature > 75 */
        private double threshold = 75.0;

        public double getThreshold() { return threshold; }
        public void setThreshold(double threshold) { this.threshold = threshold; }
    }
}
