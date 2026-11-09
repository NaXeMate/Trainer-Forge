package dev.trainerforge.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import dev.trainerforge.model.entities.PokemonItem;
import dev.trainerforge.model.enumerated.ItemType;

public interface PokemonItemRepository extends JpaRepository<PokemonItem, Long> {
    Optional<PokemonItem> findByName(String name);

    @EntityGraph(attributePaths = {"generation"})
    List<PokemonItem> findByType(ItemType type);

    @EntityGraph(attributePaths = {"generation"})
    List<PokemonItem> findByTypeIn(List<ItemType> types);

    List<PokemonItem> findByGenerationId(Long generationId);
}
