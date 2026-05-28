package dev.trainerforge.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import dev.trainerforge.exception.AchievementNotFoundException;
import dev.trainerforge.exception.InvalidFilterValueException;
import dev.trainerforge.exception.TrainerAchievementNotFoundException;
import dev.trainerforge.exception.TrainerNotFoundException;
import dev.trainerforge.model.entities.TrainerAchievement;
import dev.trainerforge.repository.TrainerAchievementRepository;

@Transactional
@Service
public class TrainerAchievementService {

    private final TrainerAchievementRepository trainerAchievementRepo;
    private final TrainerService trainerService;
    private final AchievementService achievementService;

    public TrainerAchievementService(TrainerAchievementRepository trainerAchievementRepo, TrainerService trainerService, AchievementService achievementService) {
        this.trainerAchievementRepo = trainerAchievementRepo;
        this.trainerService = trainerService;
        this.achievementService = achievementService;
    }

    public List<TrainerAchievement> findAll() {
        return trainerAchievementRepo.findAll();
    }

    public TrainerAchievement findById(Long id) {
        return trainerAchievementRepo.findById(id)
        .orElseThrow(() -> new TrainerAchievementNotFoundException(id));
    }

    public List<TrainerAchievement> findByTrainerId(Long trainerId) {
        if (!trainerService.existsById(trainerId)) {
            throw new TrainerNotFoundException(trainerId);
        }

        List<TrainerAchievement> result = trainerAchievementRepo.findByTrainerId(trainerId);

        if (result.isEmpty()) {
            throw new TrainerAchievementNotFoundException("No achievements found for trainer with id: " + trainerId + ".");
        }

        return result;
    }

    public List<TrainerAchievement> findByAchievementId(Long achievementId) {
        if (!achievementService.existsById(achievementId)) {
            throw new AchievementNotFoundException(achievementId);
        }

        List<TrainerAchievement> result = trainerAchievementRepo.findByAchievementId(achievementId);

        if (result.isEmpty()) {
            throw new TrainerAchievementNotFoundException("No trainers found with achievement id: " + achievementId + ".");
        }

        return result;
    }

    public List<TrainerAchievement> findByDateObtained(LocalDateTime dateObtained) {
        if (dateObtained == null) {
            throw new InvalidFilterValueException("Date obtained cannot be null.");
        }

        List<TrainerAchievement> result = trainerAchievementRepo.findByDateObtained(dateObtained);

        if (result.isEmpty()) {
            throw new TrainerAchievementNotFoundException("No achievements found obtained on: " + dateObtained + ".");
        }

        return result;
    }

    public List<TrainerAchievement> findByDateObtainedBetween(LocalDateTime startDate, LocalDateTime endDate) {
        if (startDate == null || endDate == null) {
            throw new InvalidFilterValueException("Start date and end date cannot be null.");
        }
        if (startDate.isAfter(endDate)) {
            throw new InvalidFilterValueException("Start date cannot be after end date.");
        }

        List<TrainerAchievement> result = trainerAchievementRepo.findByDateObtainedBetween(startDate, endDate);

        if (result.isEmpty()) {
            throw new TrainerAchievementNotFoundException("No achievements found obtained between: " + startDate + " and " + endDate + ".");
        }

        return result;
    }

    public boolean existsById(Long id) {
        return trainerAchievementRepo.existsById(id);
    }

}
