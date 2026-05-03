package dev.trainerforge.mapper;

import java.util.HashSet;
import java.util.Set;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

import dev.trainerforge.dto.response.MovePokedexDto;
import dev.trainerforge.model.entities.MovePokedex;
import dev.trainerforge.repository.MovePokedexRepository;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class MovePokedexMapper {

    protected MovePokedexRepository pokedexRepo;

    public MovePokedexMapper(MovePokedexRepository pokedexRepo) {
        this.pokedexRepo = pokedexRepo;
    }

    public abstract MovePokedex toEntity(MovePokedexDto movePokedexDto);

    public abstract MovePokedexDto toDto(MovePokedex movePokedex);

    public abstract void updateEntityFromDto(MovePokedexDto movePokedexDto, @MappingTarget MovePokedex movePokedex);

    protected Set<MovePokedex> mapMovePokedex(Long[] movePokedexesIds) {
        Set<MovePokedex> movePokedexes = new HashSet<>();
        if (movePokedexesIds != null) {
            for (Long id : movePokedexesIds) {
                if (id != null) {
                    pokedexRepo.findById(id).ifPresent(movePokedexes::add);
                }
            }
        }
        return movePokedexes;
    }

    protected Long[] mapMovePokedexesIds(Set<MovePokedex> movePokedexes) {
        if (movePokedexes == null || movePokedexes.isEmpty()) {
            return new Long[0];
        }
        return movePokedexes.stream().map(MovePokedex::getId).toArray(Long[]::new);
    }

}
