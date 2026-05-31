package dev.trainerforge.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

import dev.trainerforge.dto.response.TrainerAchievementDto;
import dev.trainerforge.model.entities.TrainerAchievement;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = {EntityReferenceMapper.class})
public interface TrainerAchievementMapper {

    TrainerAchievement toEntity(TrainerAchievementDto trainerAchievementDto);

    TrainerAchievementDto toDto(TrainerAchievement trainerAchievement);

    void updateEntityFromDto(TrainerAchievementDto trainerAchievementDto, @MappingTarget TrainerAchievement trainerAchievement);
}
