package dev.trainerforge.mapper;

import java.util.HashSet;
import java.util.Set;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

import dev.trainerforge.dto.response.PokemonDto;
import dev.trainerforge.model.entities.Pokemon;
import dev.trainerforge.repository.PokemonRepository;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class PokemonMapper {

    protected PokemonRepository pokemonRepo;

    public PokemonMapper(PokemonRepository pokemonRepo) {
        this.pokemonRepo = pokemonRepo;
    }

    public abstract Pokemon toEntity(PokemonDto pokemonDto);

    public abstract PokemonDto toDto(Pokemon pokemon);

    public abstract void updateEntityFromDto(PokemonDto pokemonDto, @MappingTarget Pokemon pokemon);

    protected Set<Pokemon> mapPokemons(Long[] pokemonsIds) {
        Set<Pokemon> pokemons = new HashSet<>();
        if (pokemonsIds != null) {
            for (Long id : pokemonsIds) {
                if (id != null) {
                    pokemonRepo.findById(id).ifPresent(pokemons::add);
                }
            }
        }
        return pokemons;
    }

    protected Long[] mapPokemonsIds(Set<Pokemon> pokemons) {
        if (pokemons == null || pokemons.isEmpty()) {
            return new Long[0];
        }
        return pokemons.stream().map(Pokemon::getId).toArray(Long[]::new);
    }
}
