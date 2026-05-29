package dev.trainerforge.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import dev.trainerforge.model.entities.GamePossesion;

public interface GamePossesionRepository extends JpaRepository<GamePossesion, Long> {
    List<GamePossesion> findByTrainerId(Long trainerId);
    List<GamePossesion> findByVideogameId(Long videogameId);
    boolean existsByTrainerIdAndVideogameId(Long trainerId, Long videogameId);
}
