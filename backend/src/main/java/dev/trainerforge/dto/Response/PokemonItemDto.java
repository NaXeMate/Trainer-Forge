package dev.trainerforge.dto.response;

import dev.trainerforge.model.enumerated.ItemType;

public record PokemonItemDto(
    String name,
    String description,
    ItemType type,
    String generation
) {}
