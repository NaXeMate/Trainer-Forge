package dev.trainerforge.dto.response;

import dev.trainerforge.model.enumerated.ItemRelationship;

public record PokedexItemDto(
    String pokedex,
    Long nationalPokedex,
    String item,
    ItemRelationship relationship
) {}
