package dev.trainerforge.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

import dev.trainerforge.dto.response.GamePossessionDto;
import dev.trainerforge.model.entities.GamePossession;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = {EntityReferenceMapper.class})
public interface GamePossessionMapper {

    @Mapping(source = "trainerUsername", target = "trainer")
    GamePossession toEntity(GamePossessionDto gamePossessionDto);

    @Mapping(source = "trainer.username", target = "trainerUsername")
    GamePossessionDto toDto(GamePossession gamePossession);

    @Mapping(source = "trainerUsername", target = "trainer")
    void updateEntityFromDto(GamePossessionDto gamePossessionDto, @MappingTarget GamePossession gamePossession);
}
