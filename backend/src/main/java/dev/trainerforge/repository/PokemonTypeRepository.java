package dev.trainerforge.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import dev.trainerforge.model.entities.PokemonType;

public interface PokemonTypeRepository extends JpaRepository<PokemonType, Long> {
    Optional<PokemonType> findByName(String name);
    List<PokemonType> findByGenerationId(Long generationId);
}
