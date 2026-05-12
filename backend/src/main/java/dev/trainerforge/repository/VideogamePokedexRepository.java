package dev.trainerforge.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import dev.trainerforge.model.entities.VideogamePokedex;

public interface VideogamePokedexRepository extends JpaRepository<VideogamePokedex, Long> {
    List<VideogamePokedex> findByPokedexId(Long pokedexId);
    List<VideogamePokedex> findByVideogameId(Long videogameId);
}
