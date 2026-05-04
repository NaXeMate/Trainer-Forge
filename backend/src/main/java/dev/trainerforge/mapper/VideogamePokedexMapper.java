package dev.trainerforge.mapper;

import java.util.HashSet;
import java.util.Set;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

import dev.trainerforge.dto.response.VideogamePokedexDto;
import dev.trainerforge.model.entities.VideogamePokedex;
import dev.trainerforge.repository.VideogamePokedexRepository;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class VideogamePokedexMapper {

    protected VideogamePokedexRepository videogamePokedexRepo;

    public VideogamePokedexMapper(VideogamePokedexRepository videogamePokedexRepo) {
        this.videogamePokedexRepo = videogamePokedexRepo;
    }

    public abstract VideogamePokedex toEntity(VideogamePokedexDto videogamePokedexDto);

    public abstract VideogamePokedexDto toDto(VideogamePokedex videogamePokedex);

    public abstract void updateEntityFromDto(VideogamePokedexDto videogamePokedexDto, @MappingTarget VideogamePokedex videogamePokedex);

    protected Set<VideogamePokedex> mapVideogamePokedexe(Long[] videogamePokedexesIds) {
        Set<VideogamePokedex> videogamePokedexes = new HashSet<>();
        if (videogamePokedexesIds != null) {
            for (Long id : videogamePokedexesIds) {
                if (id != null) {
                    videogamePokedexRepo.findById(id).ifPresent(videogamePokedexes::add);
                }
            }
        }
        return videogamePokedexes;
    }

    protected Long[] mapVideogamePokedexesIds(Set<VideogamePokedex> videogamePokedexes) {
        if (videogamePokedexes == null || videogamePokedexes.isEmpty()) {
            return new Long[0];
        }
        return videogamePokedexes.stream().map(VideogamePokedex::getId).toArray(Long[]::new);
    }
}
