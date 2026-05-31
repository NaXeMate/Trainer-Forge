package dev.trainerforge.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

import dev.trainerforge.dto.response.PokemonTeamDto;
import dev.trainerforge.model.entities.PokemonTeam;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = {EntityReferenceMapper.class})
public interface PokemonTeamMapper {

    PokemonTeam toEntity(PokemonTeamDto pokemonTeamDto);

    PokemonTeamDto toDto(PokemonTeam pokemonTeam);

    void updateEntityFromDto(PokemonTeamDto pokemonTeamDto, @MappingTarget PokemonTeam pokemonTeam);
}
