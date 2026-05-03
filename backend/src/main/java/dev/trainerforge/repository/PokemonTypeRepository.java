package dev.trainerforge.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import dev.trainerforge.model.entities.PokemonType;

public interface PokemonTypeRepository extends JpaRepository<PokemonType, Long> {
    PokemonType findByName(String name);
    List<PokemonType> findByGeneration(Long generationId);
}
