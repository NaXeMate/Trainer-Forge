package dev.trainerforge.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import dev.trainerforge.model.entities.Videogame;

public interface VideogameRepository extends JpaRepository<Videogame, Long> {
    Videogame findByName(String name);
    List<Videogame> findByGenerationId(Long generationId);
    List<Videogame> findByRegionId(Long regionId);
}
