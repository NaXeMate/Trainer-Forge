package dev.trainerforge.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

import dev.trainerforge.dto.response.NatureDto;
import dev.trainerforge.model.entities.Nature;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = {EntityReferenceMapper.class})
public interface NatureMapper {

    Nature toEntity(NatureDto natureDto);

    NatureDto toDto(Nature nature);

    void updateEntityFromDto(NatureDto natureDto, @MappingTarget Nature nature);
}
