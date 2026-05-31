package dev.trainerforge.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

import dev.trainerforge.dto.response.PokedexDto;
import dev.trainerforge.model.entities.Pokedex;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = {EntityReferenceMapper.class})
public interface PokedexMapper {

    Pokedex toEntity(PokedexDto pokedexDto);

    PokedexDto toDto(Pokedex pokedex);

    void updateEntityFromDto(PokedexDto pokedexDto, @MappingTarget Pokedex pokedex);
}
