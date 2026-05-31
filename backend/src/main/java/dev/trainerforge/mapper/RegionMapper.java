package dev.trainerforge.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

import dev.trainerforge.dto.response.RegionDto;
import dev.trainerforge.model.entities.Region;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = {EntityReferenceMapper.class})
public interface RegionMapper {

    Region toEntity(RegionDto regionDto);

    RegionDto toDto(Region region);

    void updateEntityFromDto(RegionDto regionDto, @MappingTarget Region region);
}
