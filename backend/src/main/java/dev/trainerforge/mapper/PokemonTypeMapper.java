package dev.trainerforge.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

import dev.trainerforge.dto.response.PokemonTypeDto;
import dev.trainerforge.model.entities.PokemonType;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = {EntityReferenceMapper.class})
public interface PokemonTypeMapper {

    PokemonType toEntity(PokemonTypeDto pokemonTypeDto);

    PokemonTypeDto toDto(PokemonType pokemonType);

    void updateEntityFromDto(PokemonTypeDto pokemonTypeDto, @MappingTarget PokemonType pokemonType);
}
