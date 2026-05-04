package dev.trainerforge.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import dev.trainerforge.model.entities.VideogamePokedex;

public interface VideogamePokedexRepository extends JpaRepository<VideogamePokedex, Long> {
    List<VideogamePokedex> findByPokedex(Long pokedexId);
    List<VideogamePokedex> findByVideogame(Long videogameId);
}
