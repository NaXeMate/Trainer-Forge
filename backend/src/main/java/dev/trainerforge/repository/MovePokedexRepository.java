package dev.trainerforge.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import dev.trainerforge.model.entities.MovePokedex;
import dev.trainerforge.model.enumerated.LearningMethod;

public interface MovePokedexRepository extends JpaRepository<MovePokedex, Long> {
    List<MovePokedex> findByMoveId(Long moveId);
    List<MovePokedex> findByPokedexId(Long pokedexId);
    List<MovePokedex> findByLearningMethod(LearningMethod learningMethod);
}
