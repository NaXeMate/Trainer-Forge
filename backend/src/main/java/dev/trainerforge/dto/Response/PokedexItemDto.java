package dev.trainerforge.dto.Response;

import dev.trainerforge.model.enumerated.ItemRelationship;

public record PokedexItemDto(
    String pokedexName,
    Long nationalPokedex,
    String itemName,
    ItemRelationship relationship
) {}
