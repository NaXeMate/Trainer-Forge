package dev.trainerforge.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

import dev.trainerforge.dto.response.MoveSecondaryEffectDto;
import dev.trainerforge.model.entities.MoveSecondaryEffect;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = {EntityReferenceMapper.class})
public interface MoveSecondaryEffectMapper {

    MoveSecondaryEffect toEntity(MoveSecondaryEffectDto secondaryEffectDto);

    MoveSecondaryEffectDto toDto(MoveSecondaryEffect secondaryEffect);

    void updateEntityFromDto(MoveSecondaryEffectDto secondaryEffectDto, @MappingTarget MoveSecondaryEffect secondaryEffect);
}
