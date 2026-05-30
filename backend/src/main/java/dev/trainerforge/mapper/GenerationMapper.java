package dev.trainerforge.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

import dev.trainerforge.dto.response.GenerationDto;
import dev.trainerforge.model.entities.Generation;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = {EntityReferenceMapper.class})
public interface GenerationMapper {

    Generation toEntity(GenerationDto generationDto);

    GenerationDto toDto(Generation generation);

    void updateEntityFromDto(GenerationDto generationDto, @MappingTarget Generation generation);
}
