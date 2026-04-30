package dev.trainerforge.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import dev.trainerforge.model.entities.GamePossesion;

public interface GamePossesionRepository extends JpaRepository<GamePossesion, Long> {}
