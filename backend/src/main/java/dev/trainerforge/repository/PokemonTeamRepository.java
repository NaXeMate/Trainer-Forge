package dev.trainerforge.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import dev.trainerforge.model.entities.PokemonTeam;

public interface PokemonTeamRepository extends JpaRepository<PokemonTeam, Long> {
    List<PokemonTeam> findByTeamId(Long teamId);
    List<PokemonTeam> findByPokemonId(Long pokemonId);
    List<PokemonTeam> findByPosition(int position);
    boolean existsByTeamIdAndPosition(Long teamId, int position);
    boolean existsByTeamIdAndPokemonId(Long teamId, Long pokemonId);
}
