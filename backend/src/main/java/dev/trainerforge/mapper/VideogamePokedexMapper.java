package dev.trainerforge.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

import dev.trainerforge.dto.response.VideogamePokedexDto;
import dev.trainerforge.model.entities.VideogamePokedex;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = {EntityReferenceMapper.class})
public interface VideogamePokedexMapper {

    VideogamePokedex toEntity(VideogamePokedexDto videogamePokedexDto);

    VideogamePokedexDto toDto(VideogamePokedex videogamePokedex);

    void updateEntityFromDto(VideogamePokedexDto videogamePokedexDto, @MappingTarget VideogamePokedex videogamePokedex);
}
