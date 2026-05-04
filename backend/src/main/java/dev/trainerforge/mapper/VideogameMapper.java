package dev.trainerforge.mapper;

import java.util.HashSet;
import java.util.Set;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

import dev.trainerforge.dto.response.VideogameDto;
import dev.trainerforge.model.entities.Videogame;
import dev.trainerforge.repository.VideogameRepository;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class VideogameMapper {

    protected VideogameRepository videogameRepo;

    public VideogameMapper(VideogameRepository videogameRepo) {
        this.videogameRepo = videogameRepo;
    }

    public abstract Videogame toEntity(VideogameDto videogameDto);

    public abstract VideogameDto toDto(Videogame videogame);

    public abstract void updateEntityFromDto(VideogameDto videogameDto, @MappingTarget Videogame videogame);

    protected Set<Videogame> mapVideogame(Long[] videogamesIds) {
        Set<Videogame> videogames = new HashSet<>();
        if (videogamesIds != null) {
            for (Long id : videogamesIds) {
                if (id != null) {
                    videogameRepo.findById(id).ifPresent(videogames::add);
                }
            }
        }
        return videogames;
    }

    protected Long[] mapVideogamesIds(Set<Videogame> videogames) {
        if (videogames == null || videogames.isEmpty()) {
            return new Long[0];
        }
        return videogames.stream().map(Videogame::getId).toArray(Long[]::new);
    }
}
