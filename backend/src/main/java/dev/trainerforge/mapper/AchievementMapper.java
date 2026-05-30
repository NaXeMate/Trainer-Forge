package dev.trainerforge.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

import dev.trainerforge.dto.response.AchievementDto;
import dev.trainerforge.model.entities.Achievement;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = {EntityReferenceMapper.class})
public interface AchievementMapper {

    Achievement toEntity(AchievementDto achievementDto);

    AchievementDto toDto(Achievement achievement);

    void updateEntityFromDto(AchievementDto achievementDto, @MappingTarget Achievement achievement);
}
