package io.github.zzz8688.fancontrol.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

/**
 * 调度线程池：替代课程版里每个主控板各自 newScheduledThreadPool(2) 的做法。
 * 控制台版的仿真与调速是两个并发 Runnable，对同一批板卡状态存在读写竞态；
 * 重构后合并为单 tick 串行执行，池容量保留 2 供后续告警等独立周期任务扩展。
 */
@Configuration
@EnableAsync
@EnableConfigurationProperties(FanControlProperties.class)
public class SchedulingConfig {

    @Bean(destroyMethod = "shutdown")
    public ThreadPoolTaskScheduler fanTaskScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(2);
        scheduler.setThreadNamePrefix("fan-tick-");
        scheduler.setWaitForTasksToCompleteOnShutdown(true);
        scheduler.setAwaitTerminationSeconds(10);
        scheduler.setRemoveOnCancelPolicy(true);
        return scheduler;
    }
}
