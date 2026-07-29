package dev.trainerforge.dto.response;

import dev.trainerforge.model.enumerated.Gender;

public record PokemonDto(
    Long id,
    Long species,
    String nickname,
    String locationFound,
    int level,
    boolean shiny,
    Gender gender,
    String ability,
    String move1,
    String move2,
    String move3,
    String move4,
    String equippedItem,
    String nature,
    int hpEv,
    int attackEv,
    int defenseEv,
    int specialAttackEv,
    int specialDefenseEv,
    int speedEv
) {}
