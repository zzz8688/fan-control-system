package io.github.zzz8688.fancontrol;

import io.github.zzz8688.fancontrol.entity.BusinessBoard;
import io.github.zzz8688.fancontrol.entity.FanBoard;
import io.github.zzz8688.fancontrol.mapper.BusinessBoardMapper;
import io.github.zzz8688.fancontrol.mapper.FanBoardMapper;
import io.github.zzz8688.fancontrol.service.BoardService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

/**
 * 全上下文装配验证：关闭每秒调度后，验证初始拓扑播种（2 块风扇板 + 14 块业务板）
 * 与一次手动控制 tick 的完整链路（仿真 -> 调速 -> 事件 -> 告警落库）。
 */
@SpringBootTest(properties = "fancontrol.tick.enabled=false")
class FanControlApplicationTests {

    @Autowired FanBoardMapper fanBoardMapper;
    @Autowired BusinessBoardMapper businessBoardMapper;
    @Autowired BoardService boardService;

    @Test
    void contextLoadsWithSeededTopology() {
        List<FanBoard> fans = fanBoardMapper.selectAll();
        List<BusinessBoard> boards = businessBoardMapper.selectAll();
        assertEquals(2, fans.size());
        assertEquals(14, boards.size());
        assertEquals(List.of(90, 91), fans.stream().map(FanBoard::getSlot).toList());
    }

    @Test
    void controlTickRunsEndToEnd() {
        boardService.controlTick();
        List<BusinessBoard> boards = businessBoardMapper.selectAll();
        assertFalse(boards.isEmpty());
        // 每块业务板温度都在物理边界内
        boards.forEach(b ->
                org.junit.jupiter.api.Assertions.assertTrue(
                        b.getTemperature() >= 10.0 && b.getTemperature() <= 100.0));
    }
}
