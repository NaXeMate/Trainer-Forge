package dev.trainerforge.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

import dev.trainerforge.dto.response.PokemonDto;
import dev.trainerforge.model.entities.Pokemon;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = {EntityReferenceMapper.class})
public interface PokemonMapper {

    Pokemon toEntity(PokemonDto pokemonDto);

    PokemonDto toDto(Pokemon pokemon);

    @Mapping(target = "id", ignore = true)
    void updateEntityFromDto(PokemonDto pokemonDto, @MappingTarget Pokemon pokemon);
}
