package dev.trainerforge.dto.response;

import dev.trainerforge.model.enumerated.LearningMethod;

public record MovePokedexDto(
    String pokedex,
    Long nationalPokedex,
    String move,
    String videogame,
    LearningMethod learningMethod,
    Integer level
) {}
