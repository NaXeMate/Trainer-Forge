package dev.trainerforge.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import dev.trainerforge.model.entities.Ability;

public interface AbilityRepository extends JpaRepository<Ability, Long> {
    Optional<Ability> findByName(String name);
    List<Ability> findByGenerationId(Long generationId);
}
