package dev.trainerforge.mapper;

import java.util.HashSet;
import java.util.Set;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

import dev.trainerforge.dto.response.PokedexItemDto;
import dev.trainerforge.model.entities.PokedexItem;
import dev.trainerforge.repository.PokedexItemRepository;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class PokedexItemMapper {

    protected PokedexItemRepository pokedexItemRepo;

    public PokedexItemMapper(PokedexItemRepository pokedexItemRepo) {
        this.pokedexItemRepo = pokedexItemRepo;
    }

    public abstract PokedexItem toEntity(PokedexItemDto pokedexItemDto);

    public abstract PokedexItemDto toDto(PokedexItem pokedexItem);

    public abstract void updateEntityFromDto(PokedexItemDto pokedexItemDto, @MappingTarget PokedexItem pokedexItem);

    protected Set<PokedexItem> mapPokedexItems(Long[] pokedexItemIds) {
        Set<PokedexItem> pokedexItems = new HashSet<>();
        if (pokedexItemIds != null) {
            for (Long id : pokedexItemIds) {
                if (id != null) {
                    pokedexItemRepo.findById(id).ifPresent(pokedexItems::add);
                }
            }
        }
        return pokedexItems;
    }

    protected Long[] mapPokedexItemIds(Set<PokedexItem> pokedexItems) {
        if (pokedexItems == null || pokedexItems.isEmpty()) {
            return new Long[0];
        }
        return pokedexItems.stream().map(PokedexItem::getId).toArray(Long[]::new);
    }
}
