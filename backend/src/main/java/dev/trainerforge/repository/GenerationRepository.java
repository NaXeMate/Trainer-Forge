package dev.trainerforge.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import dev.trainerforge.model.entities.Generation;

public interface GenerationRepository extends JpaRepository<Generation, Long> {
    Optional<Generation> findByName(String name);
}
