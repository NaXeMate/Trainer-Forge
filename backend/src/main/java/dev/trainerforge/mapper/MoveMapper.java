package dev.trainerforge.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

import dev.trainerforge.dto.response.MoveDto;
import dev.trainerforge.model.entities.Move;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = {EntityReferenceMapper.class})
public interface MoveMapper {

    Move toEntity(MoveDto moveDto);

    MoveDto toDto(Move move);

    void updateEntityFromDto(MoveDto moveDto, @MappingTarget Move move);
}
