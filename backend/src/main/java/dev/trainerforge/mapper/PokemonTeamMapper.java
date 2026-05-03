package dev.trainerforge.mapper;

import java.util.HashSet;
import java.util.Set;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

import dev.trainerforge.dto.response.PokemonTeamDto;
import dev.trainerforge.model.entities.PokemonTeam;
import dev.trainerforge.repository.PokemonTeamRepository;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class PokemonTeamMapper {

    protected PokemonTeamRepository pokemonTeamRepo;

    public PokemonTeamMapper(PokemonTeamRepository pokemonTeamRepo) {
        this.pokemonTeamRepo = pokemonTeamRepo;
    }

    public abstract PokemonTeam toEntity(PokemonTeamDto pokemonTeamDto);

    public abstract PokemonTeamDto toDto(PokemonTeam pokemonTeam);

    public abstract void updateEntityFromDto(PokemonTeamDto pokemonTeamDto, @MappingTarget PokemonTeam pokemonTeam);

    protected Set<PokemonTeam> mapPokemonTeam(Long[] pokemonTeamIds) {
        Set<PokemonTeam> pokemonTeams = new HashSet<>();
        if (pokemonTeamIds != null) {
            for (Long id : pokemonTeamIds) {
                if (id != null) {
                    pokemonTeamRepo.findById(id).ifPresent(pokemonTeams::add);
                }
            }
        }
        return pokemonTeams;
    }

    protected Long[] mapPokemonTeamIds(Set<PokemonTeam> pokemonTeams) {
        if (pokemonTeams == null || pokemonTeams.isEmpty()) {
            return new Long[0];
        }
        return pokemonTeams.stream().map(PokemonTeam::getId).toArray(Long[]::new);
    }
}
