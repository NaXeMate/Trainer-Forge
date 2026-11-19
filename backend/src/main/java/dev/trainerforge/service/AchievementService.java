package dev.trainerforge.service;

import java.util.List;
import java.util.Set;

import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import dev.trainerforge.exception.notfound.AchievementNotFoundException;
import dev.trainerforge.model.entities.Achievement;
import dev.trainerforge.repository.AchievementRepository;
import dev.trainerforge.validator.AchievementValidator;

@Transactional(readOnly = true)
@Service
public class AchievementService {

    private final AchievementRepository achievementRepo;

    public AchievementService(AchievementRepository achievementRepo) {
        this.achievementRepo = achievementRepo;
    }

    /**
     * Retrieves a batch of achievements and requires every requested ID to exist.
     *
     * @param achievementIds the IDs to look up.
     * @return the matching achievements, or an empty list if no IDs were supplied.
     * @throws AchievementNotFoundException if any requested ID is missing.
     */
    public List<Achievement> findAllByIds(@NonNull Set<Long> achievementIds) {
        List<Achievement> requested = achievementRepo.findAllById(achievementIds);
        if (requested.size() != achievementIds.size()) {
            throw new AchievementNotFoundException("One or more achievements were not found.");
        }
        return requested;
    }

    public List<Achievement> findAll() {
        return achievementRepo.findAll();
    }

    public Achievement findById(Long id) {
        return achievementRepo.findById(id)
        .orElseThrow(() -> new AchievementNotFoundException(id));
    }

    public Achievement findByName(String name) {
        return achievementRepo.findByName(name)
        .orElseThrow(() -> new AchievementNotFoundException("Achievement not found with name: " + name + "."));
    }

    /**
     * Retrieves all achievements that are visible to end users.
     *
     * @return all achievements flagged as non-hidden.
     * @throws AchievementNotFoundException when no visible achievements are available.
     */
    public List<Achievement> findVisibleAchievements() {
        List<Achievement> result = achievementRepo.findByHiddenFalse();
        AchievementValidator.validateVisibleResult(result);

        return result;
    }

    public long countHiddenAchievements() {
        return achievementRepo.countHiddenAchievements();
    }

    public boolean existsById(Long id) {
        return achievementRepo.existsById(id);
    }
}
