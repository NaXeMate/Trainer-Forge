package dev.trainerforge.dto.Input;

import dev.trainerforge.model.enumerated.TeamModality;

public record TeamInputDto(
    String name,
    TeamModality modality,
    boolean hidden,
    String videogameName,
    String trainerUsername
) {}
