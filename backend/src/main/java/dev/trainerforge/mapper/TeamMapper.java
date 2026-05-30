package dev.trainerforge.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

import dev.trainerforge.dto.response.TeamDto;
import dev.trainerforge.model.entities.Team;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = {EntityReferenceMapper.class})
public interface TeamMapper {

    Team toEntity(TeamDto teamDto);

    TeamDto toDto(Team team);

    void updateEntityFromDto(TeamDto teamDto, @MappingTarget Team team);
}
