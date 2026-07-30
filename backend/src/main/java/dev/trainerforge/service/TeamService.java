package dev.trainerforge.service;

import java.util.List;

import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import dev.trainerforge.dto.response.TeamDto;
import dev.trainerforge.exception.InvalidFilterValueException;
import dev.trainerforge.exception.notfound.TeamNotFoundException;
import dev.trainerforge.mapper.TeamMapper;
import dev.trainerforge.model.entities.Team;
import dev.trainerforge.model.enumerated.TeamModality;
import dev.trainerforge.repository.TeamRepository;

@Transactional(readOnly = true)
@Service
public class TeamService {

    private final TeamRepository teamRepo;
    private final VideogameService videogameService;
    private final TrainerService trainerService;

    private final TeamMapper teamMapper;

    public TeamService(TeamRepository teamRepo, VideogameService videogameService, TrainerService trainerService, TeamMapper teamMapper) {
        this.teamRepo = teamRepo;
        this.videogameService = videogameService;
        this.trainerService = trainerService;
        this.teamMapper = teamMapper;
    }

    /**
     * Creates a team owned by the currently authenticated trainer and persists it.
     * The trainer supplied in the payload cannot override the authenticated identity.
     *
     * @param dto payload containing team metadata and the videogame to bind.
     * @return the persisted team with generated identifiers.
     * @throws InvalidFilterValueException when the payload attempts to assign the team to another trainer.
     * @throws AuthenticationCredentialsNotFoundException when no authenticated trainer is available.
     */
    @Transactional
    public Team createTeam(TeamDto dto) {
        String currentUsername = getCurrentUsername();
        validateTrainerAssignment(dto, currentUsername);

        Team newTeam = new Team();
        teamMapper.updateEntityFromDto(dto, newTeam);
        newTeam.setTrainer(trainerService.findByUsername(currentUsername));

        return teamRepo.save(newTeam);
    }

    /**
     * Updates an existing team owned by the currently authenticated trainer.
     * The team identifier and owner remain unchanged regardless of the values supplied in the payload.
     *
     * @param id identifier of the team to update.
     * @param dto payload containing the fields to overwrite.
     * @return the updated and persisted team entity.
     * @throws TeamNotFoundException when the team does not exist or is not owned by the current trainer.
     * @throws InvalidFilterValueException when the payload attempts to assign the team to another trainer.
     * @throws AuthenticationCredentialsNotFoundException when no authenticated trainer is available.
     */
    @Transactional
    public Team updateTeam(Long id, TeamDto dto) {
        Team team = this.findOwnedById(id);
        String currentUsername = getCurrentUsername();

        validateTrainerAssignment(dto, currentUsername);
        teamMapper.updateEntityFromDto(dto, team);

        return teamRepo.save(team);
    }

    /**
     * Deletes a team owned by the currently authenticated trainer.
     *
     * @param id identifier of the team to delete.
     * @throws TeamNotFoundException when the team does not exist or is not owned by the current trainer.
     * @throws AuthenticationCredentialsNotFoundException when no authenticated trainer is available.
     */
    @Transactional
    public void deleteTeam(Long id) {
        Team team = this.findOwnedById(id);
        teamRepo.delete(team);
        System.out.println("Deleted Team with id: " + id);
    }

    /**
     * Retrieves every team accessible to the current trainer.
     * Public teams are always included, while hidden teams are included only for their owner.
     *
     * @return the accessible teams, or an empty list when none are available.
     * @throws AuthenticationCredentialsNotFoundException when no authenticated trainer is available.
     */
    public List<Team> findAll() {
        return filterAccessibleTeams(teamRepo.findAll());
    }

    /**
     * Retrieves a team when it is public or owned by the currently authenticated trainer.
     * Hidden teams belonging to another trainer are reported as not found to avoid disclosing them.
     *
     * @param id identifier of the requested team.
     * @return the accessible team.
     * @throws TeamNotFoundException when the team does not exist or is hidden from the current trainer.
     * @throws AuthenticationCredentialsNotFoundException when no authenticated trainer is available.
     */
    public Team findById(Long id) {
        Team team = teamRepo.findById(id)
                .orElseThrow(() -> new TeamNotFoundException(id));

        if (!isAccessibleTo(team, getCurrentUsername())) {
            throw new TeamNotFoundException(id);
        }

        return team;
    }

    /**
     * Retrieves a team only when it belongs to the currently authenticated trainer.
     * This is the authorization boundary shared by every operation that mutates a team
     * or one of its pokemon slots, including operations delegated by {@link PokemonTeamService}.
     *
     * @param id identifier of the team whose ownership must be enforced.
     * @return the owned team.
     * @throws TeamNotFoundException when the team does not exist or belongs to another trainer.
     * @throws AuthenticationCredentialsNotFoundException when no authenticated trainer is available.
     */
    public Team findOwnedById(Long id) {
        Team team = this.findById(id);
        validateOwnership(team, getCurrentUsername());
        return team;
    }

    /**
     * Retrieves a trainer's teams that are accessible to the current user.
     *
     * @param trainerId identifier of the trainer whose teams are requested.
     * @return public teams plus hidden teams when the current user owns them.
     * @throws InvalidFilterValueException when the trainer identifier is null or does not resolve to an existing trainer.
     * @throws TeamNotFoundException when the trainer has no teams accessible to the current user.
     * @throws AuthenticationCredentialsNotFoundException when no authenticated trainer is available.
     */
    public List<Team> findByTrainerId(Long trainerId) {
        if (trainerId == null) {
            throw new InvalidFilterValueException("The trainer ID cannot be null.");
        }

        if (!trainerService.existsById(trainerId)) {
            throw new InvalidFilterValueException("No trainer found with id: " + trainerId + ".");
        }
        
        List<Team> result = filterAccessibleTeams(teamRepo.findByTrainerId(trainerId));
        
        if (result.isEmpty()) {
            throw new TeamNotFoundException("No teams found for trainer with id: " + trainerId + ".");
        }
        
        return result;
    }

    /**
     * Retrieves accessible teams available in a videogame after validating its existence.
     *
     * @param videogameId identifier of the videogame used to filter teams.
     * @return public teams plus hidden teams owned by the current user.
     * @throws InvalidFilterValueException when the videogame identifier is null or does not resolve to an existing videogame.
     * @throws TeamNotFoundException when the videogame has no teams accessible to the current user.
     * @throws AuthenticationCredentialsNotFoundException when no authenticated trainer is available.
     */
    public List<Team> findByVideogameId(Long videogameId) {
        if (videogameId == null) {
            throw new InvalidFilterValueException("The videogame ID cannot be null.");
        }
        
        if (!videogameService.existsById(videogameId)) {
            throw new InvalidFilterValueException("No videogame found with id: " + videogameId + ".");
        }
        
        List<Team> result = filterAccessibleTeams(teamRepo.findByVideogameId(videogameId));
        
        if (result.isEmpty()) {
            throw new TeamNotFoundException("No teams found for videogame with id: " + videogameId + ".");
        }
        
        return result;
    }

    /**
     * Retrieves accessible teams grouped by competition modality.
     *
     * @param modality team modality used as filter criteria.
     * @return public teams plus hidden teams owned by the current user.
     * @throws InvalidFilterValueException when the modality filter is null.
     * @throws TeamNotFoundException when no accessible teams match the provided modality.
     * @throws AuthenticationCredentialsNotFoundException when no authenticated trainer is available.
     */
    public List<Team> findByModality(TeamModality modality) {
        if (modality == null) {
            throw new InvalidFilterValueException("The modality cannot be null.");
        }

        List<Team> result = filterAccessibleTeams(teamRepo.findByModality(modality));
        
        if (result.isEmpty()) {
            throw new TeamNotFoundException("No teams found for modality: " + modality + ".");
        }
        return result;
    }

    /**
     * Retrieves accessible teams by visibility status.
     *
     * @param isHidden visibility flag where true returns hidden teams owned by the current user and false returns public teams.
     * @return the accessible teams matching the requested visibility.
     * @throws TeamNotFoundException when no accessible teams match the requested visibility.
     * @throws AuthenticationCredentialsNotFoundException when no authenticated trainer is available.
     */
    public List<Team> findByIsHidden(boolean isHidden) {
        List<Team> result = filterAccessibleTeams(teamRepo.findByIsHidden(isHidden));
        
        if (result.isEmpty()) {
            String visibility = isHidden ? "hidden" : "public";
            throw new TeamNotFoundException("No " + visibility + " teams were found.");
        }
    
        return result;
    }

    /**
     * Checks whether a team identifier exists in persistence without applying visibility rules.
     * This method is intended for internal relationship validation and must not be used to authorize resource access.
     *
     * @param id identifier to check.
     * @return {@code true} when the team exists, otherwise {@code false}.
     */
    public boolean existsById(Long id) {
        return teamRepo.existsById(id);
    }

    private List<Team> filterAccessibleTeams(List<Team> teams) {
        String currentUsername = getCurrentUsername();

        return teams.stream()
                .filter(team -> isAccessibleTo(team, currentUsername))
                .toList();
    }

    private boolean isAccessibleTo(Team team, String username) {
        return !team.isHidden() || isOwnedBy(team, username);
    }

    private boolean isOwnedBy(Team team, String username) {
        return team.getTrainer().getUsername().equals(username);
    }

    /**
     * Enforces ownership for mutating operations.
     * A non-owned team is reported as not found so its ownership is not disclosed through the API.
     *
     * @param team team whose ownership must be checked.
     * @param username authenticated trainer username.
     */
    private void validateOwnership(Team team, String username) {
        if (!isOwnedBy(team, username)) {
            throw new TeamNotFoundException(team.getId());
        }
    }

    /**
     * Prevents the shared response DTO from assigning a team to a trainer other than the authenticated user.
     *
     * @param dto payload whose trainer assignment must be checked.
     * @param username authenticated trainer username.
     * @throws InvalidFilterValueException when the payload names another trainer.
     */
    private void validateTrainerAssignment(TeamDto dto, String username) {
        if (dto.trainerUsername() != null && !dto.trainerUsername().equals(username)) {
            throw new InvalidFilterValueException("A team cannot be assigned to another trainer.");
        }
    }

    /**
     * Resolves the username stored in the active Spring Security context.
     *
     * @return the authenticated trainer username.
     * @throws AuthenticationCredentialsNotFoundException when the context has no usable authentication.
     */
    private String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()
                || authentication.getName() == null || authentication.getName().isBlank()) {
            throw new AuthenticationCredentialsNotFoundException("Authentication is required.");
        }

        return authentication.getName();
    }
}
