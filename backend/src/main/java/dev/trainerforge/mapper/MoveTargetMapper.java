package dev.trainerforge.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

import dev.trainerforge.dto.response.MoveTargetDto;
import dev.trainerforge.model.entities.MoveTarget;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = {EntityReferenceMapper.class})
public interface MoveTargetMapper {

    MoveTarget toEntity(MoveTargetDto moveTargetDto);

    MoveTargetDto toDto(MoveTarget moveTarget);

    void updateEntityFromDto(MoveTargetDto moveTargetDto, @MappingTarget MoveTarget moveTarget);
}
