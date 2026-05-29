package dev.trainerforge.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import dev.trainerforge.model.entities.Team;
import dev.trainerforge.model.enumerated.TeamModality;

public interface TeamRepository extends JpaRepository<Team, Long> {
    Optional<Team> findByName(String name);
    List<Team> findByTrainerId(Long trainerId);
    List<Team> findByVideogameId(Long videogameId);
    List<Team> findByModality(TeamModality modality);
    List<Team> findByIsHidden(boolean isHidden);
}
