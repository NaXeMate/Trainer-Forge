package dev.trainerforge.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

import dev.trainerforge.dto.response.GamePossesionDto;
import dev.trainerforge.model.entities.GamePossesion;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = {EntityReferenceMapper.class})
public interface GamePossesionMapper {

    @Mapping(source = "trainerUsername", target = "trainer")
    GamePossesion toEntity(GamePossesionDto gamePossesionDto);

    @Mapping(source = "trainer.username", target = "trainerUsername")
    GamePossesionDto toDto(GamePossesion gamePossesion);

    @Mapping(source = "trainerUsername", target = "trainer")
    void updateEntityFromDto(GamePossesionDto gamePossesionDto, @MappingTarget GamePossesion gamePossesion);
}
