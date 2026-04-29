package dev.trainerforge.dto.Response;

public record TypeEffectivenessDto(
    String attackingTypeName,
    String defendingTypeName,
    Double multiplier
) {}
