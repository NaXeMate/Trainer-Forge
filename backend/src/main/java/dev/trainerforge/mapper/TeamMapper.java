package dev.trainerforge.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

import dev.trainerforge.dto.response.TeamDto;
import dev.trainerforge.model.entities.Team;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = {EntityReferenceMapper.class})
public interface TeamMapper {

    @Mapping(source = "trainerUsername", target = "trainer")
    @Mapping(source = "videogame",       target = "videogame")
    Team toEntity(TeamDto teamDto);

    @Mapping(source = "trainer",    target = "trainerUsername")
    @Mapping(source = "videogame",  target = "videogame")
    TeamDto toDto(Team team);

    @Mapping(source = "trainerUsername", target = "trainer")
    @Mapping(source = "videogame",       target = "videogame")
    void updateEntityFromDto(TeamDto teamDto, @MappingTarget Team team);
}
