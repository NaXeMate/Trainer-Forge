package dev.trainerforge.mapper;

import java.util.HashSet;
import java.util.Set;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

import dev.trainerforge.dto.response.TrainerAchievementDto;
import dev.trainerforge.model.entities.TrainerAchievement;
import dev.trainerforge.repository.TrainerAchievementRepository;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class TrainerAchievementMapper {

    protected TrainerAchievementRepository trainerAchievementRepo;
    
    public TrainerAchievementMapper(TrainerAchievementRepository trainerAchievementRepo) {
        this.trainerAchievementRepo = trainerAchievementRepo;
    }
    
    public abstract TrainerAchievement toEntity(TrainerAchievementDto trainerAchievementDto);
    
    public abstract TrainerAchievementDto toDto(TrainerAchievement trainerAchievement);

    public abstract void updateEntityFromDto(TrainerAchievementDto trainerAchievementDto, @MappingTarget TrainerAchievement trainerAchievement);

    protected Set<TrainerAchievement> mapTrainerAchievement(Long[] trainerAchievementIds) {
        Set<TrainerAchievement> trainerAchievements = new HashSet<>();
        if (trainerAchievementIds != null) {
            for (Long id : trainerAchievementIds) {
                if (id != null) {
                    trainerAchievementRepo.findById(id).ifPresent(trainerAchievements::add);
                }
            }
        }
        return trainerAchievements;
    }

    protected Long[] mapTrainerAchievementIds(Set<TrainerAchievement> trainerAchievements) {
        if (trainerAchievements == null || trainerAchievements.isEmpty()) {
            return new Long[0];
        }
        return trainerAchievements.stream().map(TrainerAchievement::getId).toArray(Long[]::new);
    }
}
