package io.github.zzz8688.fancontrol.dto;

import io.github.zzz8688.fancontrol.domain.FanBoardModeType;
import jakarta.validation.constraints.NotNull;

public record ModeUpdateRequest(
        @NotNull(message = "mode 不能为空，取值 MANUAL / AUTOMATIC")
        FanBoardModeType mode
) {
}
