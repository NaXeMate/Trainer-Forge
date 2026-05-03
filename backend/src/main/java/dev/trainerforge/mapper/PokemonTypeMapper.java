package dev.trainerforge.mapper;

import java.util.HashSet;
import java.util.Set;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

import dev.trainerforge.dto.response.PokemonTypeDto;
import dev.trainerforge.model.entities.PokemonType;
import dev.trainerforge.repository.PokemonTypeRepository;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class PokemonTypeMapper {

    protected PokemonTypeRepository pokemonTypeRepo;

    public PokemonTypeMapper(PokemonTypeRepository pokemonTypeRepo) {
        this.pokemonTypeRepo = pokemonTypeRepo;
    }

    public abstract PokemonType toEntity(PokemonTypeDto pokemonTypeDto);
    
    public abstract PokemonTypeDto toDto(PokemonType pokemonType);

    public abstract void updateEntityFromDto(PokemonTypeDto pokemonTypeDto, @MappingTarget PokemonType pokemonType);

    protected Set<PokemonType> mapPokemonType(Long[] pokemonTypeIds) {
        Set<PokemonType> pokemonTypes = new HashSet<>();
        if (pokemonTypeIds != null) {
            for (Long id : pokemonTypeIds) {
                if (id != null) {
                    pokemonTypeRepo.findById(id).ifPresent(pokemonTypes::add);
                }
            }
        }
        return pokemonTypes;
    }

    protected Long[] mapPokemonTypeIds(Set<PokemonType> pokemonTypes) {
        if (pokemonTypes == null || pokemonTypes.isEmpty()) {
            return new Long[0];
        }
        return pokemonTypes.stream().map(PokemonType::getId).toArray(Long[]::new);
    }
}
