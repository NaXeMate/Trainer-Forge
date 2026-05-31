package dev.trainerforge.dto.response;

public record AuthResponseDto(
    String token,
    String username
) {}
