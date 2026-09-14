package io.github.zzz8688.fancontrol.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 每秒控制 tick 的调度入口（默认开启，测试上下文通过 fancontrol.tick.enabled=false 关闭）。
 * 显式绑定到具名调度器 fanTaskScheduler，不使用 Spring 默认单线程调度器。
 */
@Component
@ConditionalOnProperty(prefix = "fancontrol.tick", name = "enabled",
        havingValue = "true", matchIfMissing = true)
public class TickScheduler {

    private static final Logger log = LoggerFactory.getLogger(TickScheduler.class);

    private final BoardService boardService;

    public TickScheduler(BoardService boardService) {
        this.boardService = boardService;
    }

    @Scheduled(fixedRateString = "${fancontrol.tick.interval-ms:1000}",
            scheduler = "fanTaskScheduler")
    public void tick() {
        try {
            boardService.controlTick();
        } catch (Exception e) {
            // 单个 tick 失败不能杀死后续调度
            log.error("风扇控制 tick 执行失败", e);
        }
    }
}
