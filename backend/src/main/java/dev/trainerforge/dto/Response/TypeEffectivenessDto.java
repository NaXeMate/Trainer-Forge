package dev.trainerforge.dto.response;

public record TypeEffectivenessDto(
    String attackingType,
    String defendingType,
    Double multiplier
) {}
