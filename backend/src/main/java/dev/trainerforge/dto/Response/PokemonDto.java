package dev.trainerforge.dto.Response;

import dev.trainerforge.model.enumerated.Gender;

public record PokemonDto(
    String publicId,
    String speciesName,
    Long speciesNationalPokedex,
    String nickname,
    String locationFound,
    int level,
    boolean shiny,
    Gender gender,
    String abilityName,
    String move1Name,
    String move2Name,
    String move3Name,
    String move4Name,
    String equippedItemName,
    String natureName,
    int hpEv,
    int attackEv,
    int defenseEv,
    int specialAttackEv,
    int specialDefenseEv,
    int speedEv
) {}
