package dev.trainerforge.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import dev.trainerforge.model.entities.Nature;

public interface NatureRepository extends JpaRepository<Nature, Long> {

    Nature findByName(String name);
    List<Nature> findByRise(String rise);
    List<Nature> findByLower(String lower);
}
