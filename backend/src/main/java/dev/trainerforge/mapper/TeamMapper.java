package dev.trainerforge.mapper;

import java.util.HashSet;
import java.util.Set;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

import dev.trainerforge.dto.response.TeamDto;
import dev.trainerforge.model.entities.Team;
import dev.trainerforge.repository.TeamRepository;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class TeamMapper {

    protected TeamRepository teamRepo;

    public TeamMapper(TeamRepository teamRepo) {
        this.teamRepo = teamRepo;
    }

    public abstract Team toEntity(TeamDto teamDto);

    public abstract TeamDto toDto(Team team);

    public abstract void updateEntityFromDto(TeamDto teamDto, @MappingTarget Team team);

    protected Set<Team> mapTeam(Long[] teamsIds) {
        Set<Team> teams = new HashSet<>();
        if (teamsIds != null) {
            for (Long id : teamsIds) {
                if (id != null) {
                    teamRepo.findById(id).ifPresent(teams::add);
                }
            }
        }
        return teams;
    }

    protected Long[] mapTeamsIds(Set<Team> teams) {
        if (teams == null || teams.isEmpty()) {
            return new Long[0];
        }
        return teams.stream().map(Team::getId).toArray(Long[]::new);
    }
}
