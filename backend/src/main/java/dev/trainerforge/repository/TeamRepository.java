package dev.trainerforge.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import dev.trainerforge.model.entities.Team;
import dev.trainerforge.model.enumerated.TeamModality;
import jakarta.persistence.LockModeType;

public interface TeamRepository extends JpaRepository<Team, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT team FROM Team team WHERE team.id = :id AND team.trainer.username = :username")
    Optional<Team> lockOwnedTeam(@Param("id") Long id, @Param("username") String username);

    @Override
    @EntityGraph(attributePaths = {"trainer", "videogame"})
    List<Team> findAll();

    @Override
    @EntityGraph(attributePaths = {"trainer", "videogame"})
    Optional<Team> findById(Long id);

    @EntityGraph(attributePaths = {"trainer", "videogame"})
    Optional<Team> findByName(String name);

    @EntityGraph(attributePaths = {"trainer", "videogame"})
    @Query("""
            SELECT team FROM Team team
            WHERE team.isHidden = false OR team.trainer.username = :username
            """)
    List<Team> findAllAccessibleTo(@Param("username") String username);

    @EntityGraph(attributePaths = {"trainer", "videogame"})
    @Query("""
            SELECT team FROM Team team
            WHERE team.id = :id
              AND (team.isHidden = false OR team.trainer.username = :username)
            """)
    Optional<Team> findAccessibleById(@Param("id") Long id, @Param("username") String username);

    @EntityGraph(attributePaths = {
            "trainer", "videogame", "pokemonTeams.pokemon.species", "pokemonTeams.pokemon.ability",
            "pokemonTeams.pokemon.move1", "pokemonTeams.pokemon.move2", "pokemonTeams.pokemon.move3",
            "pokemonTeams.pokemon.move4", "pokemonTeams.pokemon.equippedItem", "pokemonTeams.pokemon.nature"
    })
    @Query("""
            SELECT team FROM Team team
            WHERE team.id = :id
              AND (team.isHidden = false OR team.trainer.username = :username)
            """)
    Optional<Team> findAccessibleCompleteById(@Param("id") Long id, @Param("username") String username);

    @EntityGraph(attributePaths = {"trainer", "videogame"})
    @Query("""
            SELECT team FROM Team team
            WHERE team.trainer.id = :trainerId
              AND (team.isHidden = false OR team.trainer.username = :username)
            """)
    List<Team> findAccessibleByTrainerId(
            @Param("trainerId") Long trainerId,
            @Param("username") String username
    );

    @EntityGraph(attributePaths = {"trainer", "videogame"})
    @Query("""
            SELECT team FROM Team team
            WHERE team.videogame.id = :videogameId
              AND (team.isHidden = false OR team.trainer.username = :username)
            """)
    List<Team> findAccessibleByVideogameId(
            @Param("videogameId") Long videogameId,
            @Param("username") String username
    );

    @EntityGraph(attributePaths = {"trainer", "videogame"})
    @Query("""
            SELECT team FROM Team team
            WHERE team.modality = :modality
              AND (team.isHidden = false OR team.trainer.username = :username)
            """)
    List<Team> findAccessibleByModality(
            @Param("modality") TeamModality modality,
            @Param("username") String username
    );

    @EntityGraph(attributePaths = {"trainer", "videogame"})
    @Query("""
            SELECT team FROM Team team
            WHERE team.isHidden = :isHidden
              AND (team.isHidden = false OR team.trainer.username = :username)
            """)
    List<Team> findAccessibleByIsHidden(
            @Param("isHidden") boolean isHidden,
            @Param("username") String username
    );
}
