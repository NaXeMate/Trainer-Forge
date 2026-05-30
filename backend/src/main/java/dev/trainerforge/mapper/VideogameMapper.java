package dev.trainerforge.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

import dev.trainerforge.dto.response.VideogameDto;
import dev.trainerforge.model.entities.Videogame;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = {EntityReferenceMapper.class})
public interface VideogameMapper {

    Videogame toEntity(VideogameDto videogameDto);

    VideogameDto toDto(Videogame videogame);

    void updateEntityFromDto(VideogameDto videogameDto, @MappingTarget Videogame videogame);
}
