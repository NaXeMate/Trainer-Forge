package dev.trainerforge.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import dev.trainerforge.model.entities.GamePossession;

public interface GamePossessionRepository extends JpaRepository<GamePossession, Long> {
    List<GamePossession> findByTrainerId(Long trainerId);
    List<GamePossession> findByVideogameId(Long videogameId);
}
