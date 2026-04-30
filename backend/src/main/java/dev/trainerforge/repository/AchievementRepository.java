package dev.trainerforge.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import dev.trainerforge.model.entities.Achievement;

public interface AchievementRepository extends JpaRepository<Achievement, Long> {

    Achievement findByName(String name);

    List<Achievement> findByHiddenFalse();

    @Query("SELECT COUNT(a) FROM Achievement a WHERE a.hidden = true")
    long countHiddenAchievements();
}
