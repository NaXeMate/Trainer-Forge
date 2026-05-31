package dev.trainerforge.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

import dev.trainerforge.dto.response.MovePokedexDto;
import dev.trainerforge.model.entities.MovePokedex;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = {EntityReferenceMapper.class})
public interface MovePokedexMapper {

    MovePokedex toEntity(MovePokedexDto movePokedexDto);

    MovePokedexDto toDto(MovePokedex movePokedex);

    void updateEntityFromDto(MovePokedexDto movePokedexDto, @MappingTarget MovePokedex movePokedex);
}
