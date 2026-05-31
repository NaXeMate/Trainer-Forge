package dev.trainerforge.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import dev.trainerforge.exception.notfound.AchievementNotFoundException;
import dev.trainerforge.model.entities.Achievement;
import dev.trainerforge.repository.AchievementRepository;

@Transactional(readOnly = true)
@Service
public class AchievementService {

    private final AchievementRepository achievementRepo;

    public AchievementService(AchievementRepository achievementRepo) {
        this.achievementRepo = achievementRepo;
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
        
        if (result.isEmpty()) {
            throw new AchievementNotFoundException("No visible achievements found.");
        }
        
        return result;
    }

    public long countHiddenAchievements() {
        return achievementRepo.countHiddenAchievements();
    }

    public boolean existsById(Long id) {
        return achievementRepo.existsById(id);
    }
}
