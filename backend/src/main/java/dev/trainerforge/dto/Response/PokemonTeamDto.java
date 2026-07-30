package dev.trainerforge.dto.response;

public record PokemonTeamDto(
    Long id,
    String teamId,
    String pokemonId,
    int position
) {}
