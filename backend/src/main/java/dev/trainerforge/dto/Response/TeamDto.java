package dev.trainerforge.dto.response;

import dev.trainerforge.model.enumerated.TeamModality;

public record TeamDto(
    Long id,
    String name,
    TeamModality modality,
    boolean hidden,
    String trainerUsername,
    String videogame
) {}
