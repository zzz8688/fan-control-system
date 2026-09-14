package io.github.zzz8688.fancontrol.controller;

import io.github.zzz8688.fancontrol.entity.AlarmRecord;
import io.github.zzz8688.fancontrol.entity.BusinessBoard;
import io.github.zzz8688.fancontrol.service.BoardService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class BusinessBoardController {

    static final int MAX_ALARM_LIMIT = 200;
    private final BoardService boardService;

    public BusinessBoardController(BoardService boardService) {
        this.boardService = boardService;
    }

    @GetMapping("/business-boards")
    public List<BusinessBoard> listTemperatures() {
        return boardService.listBusinessBoards();
    }

    @GetMapping("/alarms")
    public List<AlarmRecord> recentAlarms(
            @RequestParam(name = "limit", defaultValue = "50") int limit) {
        int safeLimit = Math.min(Math.max(limit, 1), MAX_ALARM_LIMIT);
        return boardService.listRecentAlarms(safeLimit);
    }
}
