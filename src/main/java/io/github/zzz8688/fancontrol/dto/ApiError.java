package io.github.zzz8688.fancontrol.dto;

import java.time.LocalDateTime;

/** 统一错误响应体 */
public record ApiError(
        int status,
        String error,
        String message,
        LocalDateTime timestamp
) {
    public static ApiError of(int status, String error, String message) {
        return new ApiError(status, error, message, LocalDateTime.now());
    }
}
