package dev.trainerforge.dto.Response;

import dev.trainerforge.model.enumerated.PokemonClass;

public record PokedexDto(
    Long nationalPokedex,
    String name,
    String imageUrl,
    String generationName,
    String regionName,
    PokemonClass pokemonClass,
    String type1Name,
    String type2Name,
    String ability1Name,
    String ability2Name,
    String hiddenAbilityName,
    String description,
    String category,
    Double weight,
    Double height,
    int hpBase,
    int attackBase,
    int defenseBase,
    int specialAttackBase,
    int specialDefenseBase,
    int speedBase
) {}
