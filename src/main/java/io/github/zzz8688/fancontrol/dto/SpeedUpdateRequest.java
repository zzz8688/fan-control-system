package io.github.zzz8688.fancontrol.dto;

import io.github.zzz8688.fancontrol.domain.FanSpeed;
import jakarta.validation.constraints.NotNull;

public record SpeedUpdateRequest(
        @NotNull(message = "speed 不能为空，取值 LOW / MEDIUM / HIGH")
        FanSpeed speed
) {
}
