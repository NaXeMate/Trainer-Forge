package dev.trainerforge.dto.response;

import dev.trainerforge.model.enumerated.MoveClass;

public record MoveDto(
    String name,
    String type,
    MoveClass moveClass,
    int power,
    int accuracy,
    boolean contact,
    int priority,
    String target,
    String secondaryEffect,
    int pp,
    String generation
) {}
