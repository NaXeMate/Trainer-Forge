package dev.trainerforge.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import dev.trainerforge.model.entities.Nature;
import dev.trainerforge.model.enumerated.NatureRiseLower;

public interface NatureRepository extends JpaRepository<Nature, Long> {
    Optional<Nature> findByName(String name);
    List<Nature> findByRise(NatureRiseLower rise);
    List<Nature> findByLower(NatureRiseLower lower);
}
