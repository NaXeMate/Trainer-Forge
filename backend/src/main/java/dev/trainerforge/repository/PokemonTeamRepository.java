package dev.trainerforge.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import dev.trainerforge.model.entities.PokemonTeam;

public interface PokemonTeamRepository extends JpaRepository<PokemonTeam, Long> {
    List<PokemonTeam> findByTeam(Long teamId);
    List<PokemonTeam> findByPokemon(Long pokemonId);
    List<PokemonTeam> findByPosition(int position);
}
