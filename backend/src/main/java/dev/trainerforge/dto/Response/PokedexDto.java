package dev.trainerforge.dto.response;

import dev.trainerforge.model.enumerated.PokemonClass;

public record PokedexDto(
    Long nationalPokedex,
    String name,
    String imageUrl,
    int generation,
    String region,
    PokemonClass pokemonClass,
    String type1,
    String type2,
    String ability1,
    String ability2,
    String hiddenAbility,
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
