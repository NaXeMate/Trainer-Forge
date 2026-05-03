package dev.trainerforge.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import dev.trainerforge.model.entities.Generation;

public interface GenerationRepository extends JpaRepository<Generation, Long> {
    Generation findByName(String name);
}
