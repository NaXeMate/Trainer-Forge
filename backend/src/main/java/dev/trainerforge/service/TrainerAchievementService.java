package dev.trainerforge.service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import dev.trainerforge.dto.response.AchievementDto.Progress;
import dev.trainerforge.dto.response.TrainerAchievementDto;
import dev.trainerforge.exception.notfound.TrainerAchievementNotFoundException;
import dev.trainerforge.model.entities.Achievement;
import dev.trainerforge.model.entities.Trainer;
import dev.trainerforge.model.entities.TrainerAchievement;
import dev.trainerforge.repository.TrainerAchievementRepository;
import dev.trainerforge.validator.TrainerAchievementValidator;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;

@Transactional(readOnly = true)
@Service
public class TrainerAchievementService {

    private final TrainerAchievementRepository trainerAchievementRepo;
    private final TrainerService trainerService;
    private final AchievementService achievementService;
    private final EntityManager entityManager;

    public TrainerAchievementService(TrainerAchievementRepository trainerAchievementRepo, TrainerService trainerService,
            AchievementService achievementService, EntityManager entityManager) {
        this.trainerAchievementRepo = trainerAchievementRepo;
        this.trainerService = trainerService;
        this.achievementService = achievementService;
        this.entityManager = entityManager;
    }

    /**
     * Retrieves the full catalogue with the trainer's unlock status.
     * Hidden achievements display {@code ???} as their name and requirement until unlocked.
     *
     * @param username the trainer whose progress is requested.
     * @return the catalogue ordered by achievement ID.
     * @throws dev.trainerforge.exception.notfound.TrainerNotFoundException when the trainer does not exist.
     */
    public List<Progress> getProgress(String username) {
        Trainer trainer = trainerService.findByUsername(username);
        Set<Long> unlockedIds = findUnlockedIds(trainer.getId());
        return achievementService.findAll().stream()
                .sorted(Comparator.comparing(achievement -> achievement.getId()))
                .map(achievement -> toProgress(achievement, unlockedIds.contains(achievement.getId())))
                .toList();
    }

    /**
     * Records a batch of achievement unlocks and skips achievements already unlocked by the trainer.
     *
     * @param username the trainer receiving the unlocks.
     * @param achievementIds the achievements to unlock.
     * @return the newly unlocked achievements ordered by ID, or an empty list if there are no new unlocks.
     * @throws dev.trainerforge.exception.notfound.AchievementNotFoundException when any requested ID is missing.
     * @throws dev.trainerforge.exception.notfound.TrainerNotFoundException when the trainer does not exist.
     */
    @Transactional
    public List<Progress> completeAchievements(String username, @NonNull Set<Long> achievementIds) {
        List<Achievement> requested = achievementService.findAllByIds(achievementIds);
        Trainer trainer = lockTrainer(username);
        Set<Long> unlockedIds = findUnlockedIds(trainer.getId());
        LocalDateTime date = LocalDateTime.now().truncatedTo(ChronoUnit.MICROS);
        var newCompletions = new ArrayList<@NonNull TrainerAchievement>();
        for (Achievement achievement : requested) {
            if (!unlockedIds.contains(achievement.getId())) {
                newCompletions.add(new TrainerAchievement(trainer, achievement, date));
            }
        }
        return trainerAchievementRepo.saveAll(newCompletions).stream()
                .map(completion -> toProgress(completion.getAchievement(), true))
                .sorted(Comparator.comparing(progress -> progress.id()))
                .toList();
    }

    private Trainer lockTrainer(String username) {
        Trainer trainer = trainerService.findByUsername(username);
        // Concurrent awards for one trainer must observe each other's writes.
        entityManager.lock(trainer, LockModeType.PESSIMISTIC_WRITE);
        return trainer;
    }

    private Set<Long> findUnlockedIds(Long trainerId) {
        return trainerAchievementRepo.findByTrainerId(trainerId).stream()
                .map(completion -> completion.getAchievement().getId())
                .collect(Collectors.toSet());
    }

    private static Progress toProgress(Achievement achievement, boolean unlocked) {
        if (achievement.isHidden() && !unlocked) {
            return new Progress(achievement.getId(), "???", "???", false);
        }
        return new Progress(achievement.getId(), achievement.getName(), achievement.getDescription(), unlocked);
    }

    /**
     * Unlocks an achievement for a trainer and timestamps the unlock event.
     *
     * If no date is provided in the payload, the current server timestamp is used.
     *
     * @param dto payload containing trainer reference, achievement identifier, and optional unlock date.
     * @return the persisted trainer-achievement association.
     * @throws InvalidFilterValueException when the achievement identifier is not numeric or already unlocked for the trainer.
     * @throws AchievementNotFoundException when the referenced achievement does not exist.
     */
    @Transactional
    public TrainerAchievement unlockAchievement(TrainerAchievementDto dto) {
        Trainer trainer = lockTrainer(dto.trainerUsername());

        Long achievementId = TrainerAchievementValidator.parseAchievementId(dto.achievement());
        Achievement achievement = achievementService.findById(achievementId);

        TrainerAchievementValidator.validateNotAlreadyUnlocked(
            trainerAchievementRepo.existsByTrainerIdAndAchievementId(trainer.getId(), achievementId),
            trainer.getId(),
            achievementId);

        LocalDateTime date = dto.dateObtained() != null ? dto.dateObtained() : LocalDateTime.now();
        return trainerAchievementRepo.save(new TrainerAchievement(trainer, achievement, date));
    }

    public List<TrainerAchievement> findAll() {
        return trainerAchievementRepo.findAll();
    }

    public TrainerAchievement findById(Long id) {
        return trainerAchievementRepo.findById(id)
        .orElseThrow(() -> new TrainerAchievementNotFoundException(id));
    }

    /**
     * Retrieves all achievements unlocked by a trainer.
     *
     * @param trainerId identifier of the trainer used to filter unlock records.
     * @return all trainer-achievement records associated with the trainer.
     * @throws TrainerNotFoundException when the trainer does not exist.
     * @throws TrainerAchievementNotFoundException when the trainer exists but has no unlocked achievements.
     */
    public List<TrainerAchievement> findByTrainerId(Long trainerId) {
        TrainerAchievementValidator.validateTrainerExists(trainerService.existsById(trainerId), trainerId);

        List<TrainerAchievement> result = trainerAchievementRepo.findByTrainerId(trainerId);
        TrainerAchievementValidator.validateByTrainerResult(result, trainerId);

        return result;
    }

    /**
     * Retrieves all trainers who unlocked a specific achievement.
     *
     * @param achievementId identifier of the achievement used as filter criteria.
     * @return all unlock records linked to the provided achievement.
     * @throws AchievementNotFoundException when the achievement does not exist.
     * @throws TrainerAchievementNotFoundException when the achievement exists but has no unlock records.
     */
    public List<TrainerAchievement> findByAchievementId(Long achievementId) {
        TrainerAchievementValidator.validateAchievementExists(
            achievementService.existsById(achievementId), achievementId);

        List<TrainerAchievement> result = trainerAchievementRepo.findByAchievementId(achievementId);
        TrainerAchievementValidator.validateByAchievementResult(result, achievementId);

        return result;
    }

    /**
     * Retrieves unlock records for an exact timestamp.
     *
     * @param dateObtained timestamp used to filter unlock records.
     * @return all records unlocked at the provided timestamp.
     * @throws InvalidFilterValueException when the timestamp is null.
     * @throws TrainerAchievementNotFoundException when no unlock record matches the provided timestamp.
     */
    public List<TrainerAchievement> findByDateObtained(LocalDateTime dateObtained) {
        TrainerAchievementValidator.validateDate(dateObtained);

        List<TrainerAchievement> result = trainerAchievementRepo.findByDateObtained(dateObtained);
        TrainerAchievementValidator.validateByDateResult(result, dateObtained);

        return result;
    }

    /**
     * Retrieves unlock records whose timestamps fall within an inclusive interval.
     *
     * @param startDate lower bound of the unlock timestamp interval.
     * @param endDate upper bound of the unlock timestamp interval.
     * @return all records unlocked between the requested timestamps.
     * @throws InvalidFilterValueException when either bound is null or the interval order is invalid.
     * @throws TrainerAchievementNotFoundException when no unlock record exists in the requested interval.
     */
    public List<TrainerAchievement> findByDateObtainedBetween(LocalDateTime startDate, LocalDateTime endDate) {
        TrainerAchievementValidator.validateDateBetween(startDate, endDate);

        List<TrainerAchievement> result = trainerAchievementRepo.findByDateObtainedBetween(startDate, endDate);
        TrainerAchievementValidator.validateByDateRangeResult(result, startDate, endDate);

        return result;
    }

    public boolean existsById(Long id) {
        return trainerAchievementRepo.existsById(id);
    }

}
