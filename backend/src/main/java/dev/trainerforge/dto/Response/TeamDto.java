package dev.trainerforge.dto.Response;

import dev.trainerforge.model.enumerated.TeamModality;

public record TeamDto(
    String publicId,
    String name,
    TeamModality modality,
    boolean hidden,
    String trainerUsername,
    String videogameName
) {}
