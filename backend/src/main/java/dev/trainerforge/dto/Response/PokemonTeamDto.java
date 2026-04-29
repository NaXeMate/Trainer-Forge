package dev.trainerforge.dto.Response;

public record PokemonTeamDto(
    String teamPublicId,
    String pokemonPublicId,
    int position
) {}
