package dev.trainerforge.dto.Response;

import dev.trainerforge.model.enumerated.MoveClass;

public record MoveDto(
    String name,
    String typeName,
    MoveClass moveClass,
    int power,
    int accuracy,
    boolean contact,
    int priority,
    String target,
    String secondaryEffect,
    int pp,
    String generationName
) {}
