package dev.trainerforge.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

import dev.trainerforge.dto.response.AbilityDto;
import dev.trainerforge.model.entities.Ability;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = {EntityReferenceMapper.class})
public interface AbilityMapper {
    
    Ability toEntity(AbilityDto abilityDto);

    AbilityDto toDto(Ability ability);

    void updateEntityFromDto(AbilityDto abilityDto, @MappingTarget Ability ability);
}
