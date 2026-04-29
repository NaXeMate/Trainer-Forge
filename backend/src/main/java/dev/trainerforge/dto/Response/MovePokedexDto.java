package dev.trainerforge.dto.Response;

import dev.trainerforge.model.enumerated.LearningMethod;

public record MovePokedexDto(
    String pokedexName,
    Long nationalPokedex,
    String moveName,
    String videogameName,
    LearningMethod learningMethod,
    Integer level
) {}
