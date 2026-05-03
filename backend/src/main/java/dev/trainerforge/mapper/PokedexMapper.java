package dev.trainerforge.mapper;

import java.util.HashSet;
import java.util.Set;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

import dev.trainerforge.dto.response.PokedexDto;
import dev.trainerforge.model.entities.Pokedex;
import dev.trainerforge.repository.PokedexRepository;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class PokedexMapper {

    protected PokedexRepository pokedexRepo;

    public PokedexMapper(PokedexRepository pokedexRepo) {
        this.pokedexRepo = pokedexRepo;
    }

    public abstract Pokedex toEntity(PokedexDto pokedexDto);

    public abstract PokedexDto toDto(Pokedex pokedex);

    public abstract void updateEntityFromDto(PokedexDto pokedexDto, @MappingTarget Pokedex pokedex);

    protected Set<Pokedex> mapPokedex(Long[] pokedexesIds) {
        Set<Pokedex> pokedexes = new HashSet<>();
        if (pokedexesIds != null) {
            for (Long id : pokedexesIds) {
                if (id != null) {
                    pokedexRepo.findById(id).ifPresent(pokedexes::add);
                }
            }
        }
        return pokedexes;
    }

    protected Long[] mapPokedexesIds(Set<Pokedex> pokedexes) {
        if (pokedexes == null || pokedexes.isEmpty()) {
            return new Long[0];
        }
        return pokedexes.stream().map(Pokedex::getId).toArray(Long[]::new);
    }
}
