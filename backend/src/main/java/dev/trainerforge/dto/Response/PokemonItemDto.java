package dev.trainerforge.dto.Response;

import dev.trainerforge.model.enumerated.ItemType;

public record PokemonItemDto(
    String name,
    String description,
    ItemType type,
    String generationName
) {}
