package dev.trainerforge.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import dev.trainerforge.model.entities.Achievement;

public interface AchievementRepository extends JpaRepository<Achievement, Long> {
    Optional<Achievement> findByName(String name);
    List<Achievement> findByHiddenFalse();
    @Query("SELECT COUNT(a) FROM Achievement a WHERE a.isHidden = true")
    long countHiddenAchievements();
}
