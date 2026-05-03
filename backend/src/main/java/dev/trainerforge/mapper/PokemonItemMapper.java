package dev.trainerforge.mapper;

import java.util.HashSet;
import java.util.Set;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

import dev.trainerforge.dto.response.PokemonItemDto;
import dev.trainerforge.model.entities.PokemonItem;
import dev.trainerforge.repository.PokemonItemRepository;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class PokemonItemMapper {

    protected PokemonItemRepository pokemonItemRepo;

    public PokemonItemMapper(PokemonItemRepository pokemonItemRepo) {
        this.pokemonItemRepo = pokemonItemRepo;
    }

    public abstract PokemonItem toEntity(PokemonItemDto pokemonItemDto);

    public abstract PokemonItemDto toDto(PokemonItem pokemonItem);

    public abstract void updateEntityFromDto(PokemonItemDto pokemonItemDto, @MappingTarget PokemonItem pokemonItem);

    protected Set<PokemonItem> mapPokemonItem(Long[] pokemonItemsIds) {
        Set<PokemonItem> pokemonItems = new HashSet<>();
        if (pokemonItemsIds != null) {
            for (Long id : pokemonItemsIds) {
                if (id != null) {
                    pokemonItemRepo.findById(id).ifPresent(pokemonItems::add);
                }
            }
        }
        return pokemonItems;
    }

    protected Long[] mapPokemonItemsIds(Set<PokemonItem> pokemonItems) {
        if (pokemonItems == null || pokemonItems.isEmpty()) {
            return new Long[0];
        }
        return pokemonItems.stream().map(PokemonItem::getId).toArray(Long[]::new);
    }
}
