package dev.trainerforge.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import dev.trainerforge.model.entities.TrainerAchievement;
import java.time.LocalDateTime;

public interface TrainerAchievementRepository extends JpaRepository<TrainerAchievement, Long> {
    List<TrainerAchievement> findByTrainerId(Long trainerId);
    List<TrainerAchievement> findByAchievementId(Long achievementId);
    List<TrainerAchievement> findByDateObtained(LocalDateTime dateObtained);
    List<TrainerAchievement> findByDateObtainedBetween(LocalDateTime startDate, LocalDateTime endDate);
}
