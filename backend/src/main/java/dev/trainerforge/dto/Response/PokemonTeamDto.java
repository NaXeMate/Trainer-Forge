package dev.trainerforge.dto.response;

import java.util.List;

public record PokemonTeamDto(
    Long id,
    String teamId,
    String pokemonId,
    int position
) {
    /** Contents of a slot before a Pokémon or a team association has been saved. */
    public record Member(Long pokedexId, List<String> moves, String ability, String item,
            Stats stats, Integer level, String nature) {}

    public record Stats(Integer hp, Integer attack, Integer defense, Integer specialAttack,
            Integer specialDefense, Integer speed) {}
}
