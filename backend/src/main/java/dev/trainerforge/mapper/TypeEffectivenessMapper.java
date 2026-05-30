package dev.trainerforge.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

import dev.trainerforge.dto.response.TypeEffectivenessDto;
import dev.trainerforge.model.entities.TypeEffectiveness;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = {EntityReferenceMapper.class})
public interface TypeEffectivenessMapper {

    TypeEffectiveness toEntity(TypeEffectivenessDto typeEffectivenessDto);

    TypeEffectivenessDto toDto(TypeEffectiveness typeEffectiveness);

    void updateEntityFromDto(TypeEffectivenessDto typeEffectivenessDto, @MappingTarget TypeEffectiveness typeEffectiveness);
}
