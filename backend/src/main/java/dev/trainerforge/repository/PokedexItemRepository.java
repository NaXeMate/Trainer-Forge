package dev.trainerforge.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import dev.trainerforge.model.entities.PokedexItem;

public interface PokedexItemRepository extends JpaRepository<PokedexItem, Long> {}
