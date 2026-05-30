package dev.trainerforge.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

import dev.trainerforge.dto.response.GamePossesionDto;
import dev.trainerforge.model.entities.GamePossesion;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = {EntityReferenceMapper.class})
public interface GamePossesionMapper {

    GamePossesion toEntity(GamePossesionDto gamePossesionDto);

    GamePossesionDto toDto(GamePossesion gamePossesion);

    void updateEntityFromDto(GamePossesionDto gamePossesionDto, @MappingTarget GamePossesion gamePossesion);
}
