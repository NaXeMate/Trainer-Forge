package dev.trainerforge.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import dev.trainerforge.model.entities.PokemonTeam;

public interface PokemonTeamRepository extends JpaRepository<PokemonTeam, Long> {
    @Query("""
            SELECT association FROM PokemonTeam association
            JOIN association.team team
            WHERE team.isHidden = false OR team.trainer.username = :username
            ORDER BY team.id ASC, association.position ASC
            """)
    List<PokemonTeam> findAllAccessibleToOrderByTeamIdAscPositionAsc(
            @Param("username") String username
    );

    List<PokemonTeam> findByTeamIdOrderByPositionAsc(Long teamId);

    @Query("""
            SELECT association FROM PokemonTeam association
            JOIN association.team team
            WHERE association.pokemon.id = :pokemonId
              AND (team.isHidden = false OR team.trainer.username = :username)
            """)
    List<PokemonTeam> findAccessibleByPokemonId(
            @Param("pokemonId") Long pokemonId,
            @Param("username") String username
    );

    @Query("""
            SELECT association FROM PokemonTeam association
            JOIN association.team team
            WHERE association.position = :position
              AND (team.isHidden = false OR team.trainer.username = :username)
            """)
    List<PokemonTeam> findAccessibleByPosition(
            @Param("position") int position,
            @Param("username") String username
    );

    boolean existsByTeamIdAndPosition(Long teamId, int position);
    boolean existsByTeamIdAndPokemonId(Long teamId, Long pokemonId);
}
