package dev.trainerforge.dto.Input;

public record PokemonTeamInputDto(
    String teamPublicId,
    String pokemonPublicId,
    int position
) {}
