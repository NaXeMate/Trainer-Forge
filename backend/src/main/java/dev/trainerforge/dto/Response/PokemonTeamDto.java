package dev.trainerforge.dto.response;

public record PokemonTeamDto(
    String teamId,
    String pokemonId,
    int position
) {}
