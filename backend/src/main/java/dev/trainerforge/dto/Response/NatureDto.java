package dev.trainerforge.dto.response;

import dev.trainerforge.model.enumerated.NatureRiseLower;

public record NatureDto(
    String name,
    NatureRiseLower rise,
    NatureRiseLower lower
) {}
