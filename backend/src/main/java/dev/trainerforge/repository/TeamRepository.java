package dev.trainerforge.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import dev.trainerforge.model.entities.Team;
import dev.trainerforge.model.enumerated.TeamModality;

public interface TeamRepository extends JpaRepository<Team, Long> {
    Team findByName(String name);
    List<Team> findByTrainer(Long trainerId);
    List<Team> findByGame(Long videogameId);
    List<Team> findByModality(TeamModality modality);
    List<Team> findByIsHidden(boolean isHidden);
}
