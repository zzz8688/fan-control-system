package io.github.zzz8688.fancontrol.controller;

import io.github.zzz8688.fancontrol.domain.FanBoardModeType;
import io.github.zzz8688.fancontrol.domain.FanSpeed;
import io.github.zzz8688.fancontrol.entity.FanBoard;
import io.github.zzz8688.fancontrol.exception.BoardModeConflictException;
import io.github.zzz8688.fancontrol.exception.BoardNotFoundException;
import io.github.zzz8688.fancontrol.service.BoardService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(FanBoardController.class)
class FanBoardControllerTest {

    @Autowired MockMvc mockMvc;
    @MockBean BoardService boardService;

    private FanBoard board(int slot, FanSpeed speed, FanBoardModeType mode) {
        return new FanBoard(slot, speed, mode, LocalDateTime.now());
    }

    @Test
    void manualSpeedUpdateOnAutomaticBoardReturns409() throws Exception {
        when(boardService.manualAdjust(anyInt(), org.mockito.ArgumentMatchers.any()))
                .thenThrow(new BoardModeConflictException(90, FanBoardModeType.AUTOMATIC));
        mockMvc.perform(post("/api/fan-boards/90/speed")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"speed\":\"LOW\"}"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.error").value("MODE_CONFLICT"));
    }

    @Test
    void unknownSlotReturns404() throws Exception {
        when(boardService.manualAdjust(anyInt(), org.mockito.ArgumentMatchers.any()))
                .thenThrow(new BoardNotFoundException(99));
        mockMvc.perform(post("/api/fan-boards/99/speed")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"speed\":\"LOW\"}"))
                .andExpect(status().isNotFound());
    }

    @Test
    void invalidEnumReturns400() throws Exception {
        mockMvc.perform(post("/api/fan-boards/90/speed")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"speed\":\"TURBO\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void missingFieldReturns400() throws Exception {
        mockMvc.perform(post("/api/fan-boards/90/speed")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void validSpeedUpdateReturns200() throws Exception {
        when(boardService.manualAdjust(90, FanSpeed.HIGH))
                .thenReturn(board(90, FanSpeed.HIGH, FanBoardModeType.MANUAL));
        mockMvc.perform(post("/api/fan-boards/90/speed")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"speed\":\"HIGH\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.slot").value(90))
                .andExpect(jsonPath("$.speed").value("HIGH"))
                .andExpect(jsonPath("$.mode").value("MANUAL"));
    }

    @Test
    void listFanBoardsReturns200() throws Exception {
        mockMvc.perform(get("/api/fan-boards"))
                .andExpect(status().isOk());
    }
}
