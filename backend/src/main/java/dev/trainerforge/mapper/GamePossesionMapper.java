package dev.trainerforge.mapper;

import java.util.HashSet;
import java.util.Set;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

import dev.trainerforge.dto.response.GamePossesionDto;
import dev.trainerforge.model.entities.GamePossesion;
import dev.trainerforge.repository.GamePossesionRepository;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class GamePossesionMapper {

    protected GamePossesionRepository gamePossesionRepo;

    public GamePossesionMapper(GamePossesionRepository gamePossesionRepo) {
        this.gamePossesionRepo = gamePossesionRepo;
    }

    public abstract GamePossesion toEntity(GamePossesionDto gamePossesionDto);

    public abstract GamePossesionDto toDto(GamePossesion gamePossesion);

    public abstract void updateEntityFromDto(GamePossesionDto gamePossesionDto, @MappingTarget GamePossesion gamePossesion);

    protected Set<GamePossesion> mapGamePossesions(Long[] gamePossesionsIds) {
        Set<GamePossesion> gamePossesions = new HashSet<>();
        if (gamePossesionsIds != null) {
            for (Long id : gamePossesionsIds) {
                if (id != null) {
                    gamePossesionRepo.findById(id).ifPresent(gamePossesions::add);
                }
            }
        }
        return gamePossesions;
    }

    protected Long[] mapGamePossesionsIds(Set<GamePossesion> gamePossesions) {
        if (gamePossesions == null || gamePossesions.isEmpty()) {
            return new Long[0];
        }
        return gamePossesions.stream().map(GamePossesion::getId).toArray(Long[]::new);
    }
}
