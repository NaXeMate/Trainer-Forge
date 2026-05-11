package dev.trainerforge.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import dev.trainerforge.model.entities.Ability;

public interface AbilityRepository extends JpaRepository<Ability, Long> {

    Ability findByName(String name);
    List<Ability> findByGenerationId(Long generationId);
}
