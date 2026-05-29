package dev.trainerforge.dto.response;

import java.time.LocalDateTime;

public record ErrorDTO(
    int status,
    String error,
    String message,
    String path,
    LocalDateTime timestamp
) {}
