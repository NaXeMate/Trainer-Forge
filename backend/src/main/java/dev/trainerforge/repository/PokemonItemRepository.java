package dev.trainerforge.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import dev.trainerforge.model.entities.PokemonItem;
import dev.trainerforge.model.enumerated.ItemType;

public interface PokemonItemRepository extends JpaRepository<PokemonItem, Long> {
    PokemonItem findByName(String name);
    List<PokemonItem> findByType(ItemType type);
    List<PokemonItem> findByGenerationId(Long generationId);
}
