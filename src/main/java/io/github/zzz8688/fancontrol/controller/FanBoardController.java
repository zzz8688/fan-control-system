package io.github.zzz8688.fancontrol.controller;

import io.github.zzz8688.fancontrol.dto.ModeUpdateRequest;
import io.github.zzz8688.fancontrol.dto.SpeedUpdateRequest;
import io.github.zzz8688.fancontrol.entity.FanBoard;
import io.github.zzz8688.fancontrol.service.BoardService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/fan-boards")
public class FanBoardController {

    private final BoardService boardService;

    public FanBoardController(BoardService boardService) {
        this.boardService = boardService;
    }

    @GetMapping
    public List<FanBoard> list() {
        return boardService.listFanBoards();
    }

    /** 手动调速：自动模式下返回 409，槽位不存在返回 404 */
    @PostMapping("/{slot}/speed")
    public ResponseEntity<FanBoard> updateSpeed(@PathVariable int slot,
                                                @Valid @RequestBody SpeedUpdateRequest request) {
        return ResponseEntity.ok(boardService.manualAdjust(slot, request.speed()));
    }

    @PostMapping("/{slot}/mode")
    public ResponseEntity<FanBoard> updateMode(@PathVariable int slot,
                                               @Valid @RequestBody ModeUpdateRequest request) {
        return ResponseEntity.ok(boardService.changeMode(slot, request.mode()));
    }
}
