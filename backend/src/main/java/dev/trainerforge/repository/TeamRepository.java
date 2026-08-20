package dev.trainerforge.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import dev.trainerforge.model.entities.Team;
import dev.trainerforge.model.enumerated.TeamModality;

public interface TeamRepository extends JpaRepository<Team, Long> {

    @Override
    @EntityGraph(attributePaths = {"trainer", "videogame"})
    List<Team> findAll();

    @Override
    @EntityGraph(attributePaths = {"trainer", "videogame"})
    Optional<Team> findById(Long id);

    @EntityGraph(attributePaths = {"trainer", "videogame"})
    Optional<Team> findByName(String name);

    @EntityGraph(attributePaths = {"trainer", "videogame"})
    List<Team> findByTrainerId(Long trainerId);

    @EntityGraph(attributePaths = {"trainer", "videogame"})
    List<Team> findByVideogameId(Long videogameId);

    @EntityGraph(attributePaths = {"trainer", "videogame"})
    List<Team> findByModality(TeamModality modality);

    @EntityGraph(attributePaths = {"trainer", "videogame"})
    List<Team> findByIsHidden(boolean isHidden);
}
