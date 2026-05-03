package dev.trainerforge.mapper;

import java.util.HashSet;
import java.util.Set;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

import dev.trainerforge.dto.response.AchievementDto;
import dev.trainerforge.model.entities.Achievement;
import dev.trainerforge.repository.AchievementRepository;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class AchievementMapper {

    protected AchievementRepository achievementRepo;

    public AchievementMapper(AchievementRepository achievementRepo) {
        this.achievementRepo = achievementRepo;
    }
    
    public abstract Achievement toEntity(AchievementDto achievementDto);

    public abstract AchievementDto toDto(Achievement achievement);

    public abstract void updateEntityFromDto(AchievementDto achievementDto, @MappingTarget Achievement achievement);

    protected Set<Achievement> mapAchievements(Long[] achievementsIds) {
        Set<Achievement> achievements = new HashSet<>();
        if (achievementsIds != null) {
            for (Long id : achievementsIds) {
                if (id != null) {
                    achievementRepo.findById(id).ifPresent(achievements::add);
                }
            }
        }
        return achievements;
    }

    protected Long[] mapAchievementsIds(Set<Achievement> achievements) {
        if (achievements == null || achievements.isEmpty()) {
            return new Long[0];
        }
        return achievements.stream().map(Achievement::getId).toArray(Long[]::new);
    }
}
