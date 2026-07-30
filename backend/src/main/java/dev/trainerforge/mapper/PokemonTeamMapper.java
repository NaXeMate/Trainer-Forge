package dev.trainerforge.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

import dev.trainerforge.dto.response.PokemonTeamDto;
import dev.trainerforge.model.entities.PokemonTeam;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = {EntityReferenceMapper.class})
public interface PokemonTeamMapper {

    @Mapping(target = "id",      ignore = true)
    @Mapping(target = "team",    ignore = true)
    @Mapping(target = "pokemon", ignore = true)
    PokemonTeam toEntity(PokemonTeamDto pokemonTeamDto);

    @Mapping(source = "team.id",    target = "teamId")
    @Mapping(source = "pokemon.id", target = "pokemonId")
    PokemonTeamDto toDto(PokemonTeam pokemonTeam);

    @Mapping(target = "id",      ignore = true)
    @Mapping(target = "team",    ignore = true)
    @Mapping(target = "pokemon", ignore = true)
    void updateEntityFromDto(PokemonTeamDto pokemonTeamDto, @MappingTarget PokemonTeam pokemonTeam);
}
