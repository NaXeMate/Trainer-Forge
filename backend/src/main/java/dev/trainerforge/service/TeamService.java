package dev.trainerforge.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import dev.trainerforge.dto.response.PokemonTeamDto.Member;
import dev.trainerforge.dto.response.TeamDto;
import dev.trainerforge.dto.response.TeamDto.Complete;
import dev.trainerforge.dto.response.TeamDto.Lineup;
import dev.trainerforge.exception.InvalidFilterValueException;
import dev.trainerforge.exception.ResourceNotFoundException;
import dev.trainerforge.exception.notfound.TeamNotFoundException;
import dev.trainerforge.mapper.TeamMapper;
import dev.trainerforge.model.entities.Pokemon;
import dev.trainerforge.model.entities.PokemonTeam;
import dev.trainerforge.model.entities.Team;
import dev.trainerforge.model.enumerated.TeamModality;
import dev.trainerforge.repository.TeamRepository;
import dev.trainerforge.validator.TeamValidator;
import dev.trainerforge.validator.ValidationLimits;

@Transactional(readOnly = true)
@Service
public class TeamService {

    private final TeamRepository teamRepo;
    private final VideogameService videogameService;
    private final TrainerService trainerService;
    private final PokemonService pokemonService;

    private final TeamMapper teamMapper;

    public TeamService(TeamRepository teamRepo, VideogameService videogameService, TrainerService trainerService,
            TeamMapper teamMapper, PokemonService pokemonService) {
        this.teamRepo = teamRepo;
        this.videogameService = videogameService;
        this.trainerService = trainerService;
        this.teamMapper = teamMapper;
        this.pokemonService = pokemonService;
    }

    @Transactional
    public Complete saveCompleteTeam(Long id, Lineup lineup) {
        String username = getCurrentUsername();
        Team team = id == null ? new Team()
                : teamRepo.lockOwnedTeam(id, username).orElseThrow(() -> new TeamNotFoundException(id));

        if (lineup == null || lineup.name() == null || lineup.name().isBlank() || lineup.name().strip().length() > 100) {
            throw new InvalidFilterValueException("Enter a team name of up to 100 characters.");
        }
        if (lineup.gameId() == null || lineup.gameId() <= 0) {
            throw new InvalidFilterValueException("Choose a game before saving the team.");
        }
        var game = videogameService.findById(lineup.gameId());
        List<Pokemon> replacements = prepareLineup(lineup);
        Map<Integer, PokemonTeam> previous = team.getPokemonTeams().stream()
                .collect(Collectors.toMap(slot -> slot.getPosition(), slot -> slot));

        if (id == null) team.setTrainer(trainerService.findByUsername(username));
        team.setName(lineup.name().strip());
        team.setVideogame(game);
        team.setModality(lineup.modality());
        if (id == null || lineup.publicTeam() != null) team.setHidden(!Boolean.TRUE.equals(lineup.publicTeam()));
        if (id == null) team = teamRepo.save(team);

        List<Long> removedPokemonIds = new ArrayList<>();
        for (int index = 0; index < replacements.size(); index++) {
            int position = index + 1;
            Pokemon replacement = replacements.get(index);
            PokemonTeam slot = previous.get(position);
            if (slot == null) {
                if (replacement != null) {
                    team.getPokemonTeams().add(new PokemonTeam(team, pokemonService.saveTeamMember(replacement), position));
                }
                continue;
            }
            Pokemon original = slot.getPokemon();
            if (replacement == null) {
                team.getPokemonTeams().remove(slot);
            } else {
                if (pokemonService.describeTeamMember(original).equals(pokemonService.describeTeamMember(replacement))) continue;
                if (original.getSpecies().getId().equals(replacement.getSpecies().getId())) {
                    replacement.setNickname(original.getNickname());
                    replacement.setLocationFound(original.getLocationFound());
                    replacement.setGender(original.getGender());
                    replacement.setShiny(original.isShiny());
                }
                slot.setPokemon(pokemonService.saveTeamMember(replacement));
            }
            removedPokemonIds.add(original.getId());
        }

        teamRepo.flush();
        pokemonService.removeUnreferencedTeamMembers(removedPokemonIds);
        return describeCompleteTeam(team);
    }

    List<Pokemon> prepareLineup(Lineup lineup) {
        int capacity = ValidationLimits.IntegerRange.TEAM_SIZE.max();
        if (lineup == null || lineup.modality() == null || lineup.members() == null || lineup.members().size() != capacity) {
            throw new InvalidFilterValueException("Choose a valid modality and supply exactly " + capacity + " slots.");
        }
        List<Pokemon> slots = new ArrayList<>(capacity);
        for (int index = 0; index < capacity; index++) {
            var member = lineup.members().get(index);
            try {
                slots.add(member == null ? null : pokemonService.prepareTeamMember(member));
            } catch (InvalidFilterValueException | ResourceNotFoundException error) {
                throw new InvalidFilterValueException("Slot " + (index + 1) + ": " + error.getMessage());
            }
        }
        validateMonotype(lineup.modality(), slots);
        return slots;
    }

    static void validateMonotype(TeamModality modality, List<Pokemon> roster) {
        if (modality != TeamModality.MONOTYPE) return;
        Set<String> common = null;
        for (Pokemon pokemon : roster) {
            if (pokemon == null) continue;
            var species = pokemon.getSpecies();
            Set<String> types = new HashSet<>();
            if (species.getType1() != null) types.add(species.getType1().getName());
            if (species.getType2() != null) types.add(species.getType2().getName());
            if (common == null) common = types;
            else common.retainAll(types);
            if (common.isEmpty()) throw new InvalidFilterValueException("All Pokémon in a Monotype team must share a type.");
        }
    }

    public Complete getCompleteTeam(Long id) {
        Team team = teamRepo.findAccessibleCompleteById(id, getCurrentUsername())
                .orElseThrow(() -> new TeamNotFoundException(id));
        return describeCompleteTeam(team);
    }

    private Complete describeCompleteTeam(Team team) {
        Map<Integer, Member> roster = team.getPokemonTeams().stream()
                .collect(Collectors.toMap(slot -> slot.getPosition(), slot -> pokemonService.describeTeamMember(slot.getPokemon())));
        var members = IntStream.rangeClosed(1, ValidationLimits.IntegerRange.TEAM_SIZE.max())
                .mapToObj(roster::get).toList();
        return new Complete(team.getId(), team.getTrainer().getUsername(),
                new Lineup(team.getName(), team.getModality(), team.getVideogame().getId(), !team.isHidden(), members));
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
        TeamValidator.validateTrainerAssignment(dto, currentUsername);

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

        TeamValidator.validateTrainerAssignment(dto, currentUsername);
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
        return teamRepo.findAllAccessibleTo(getCurrentUsername());
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
        return teamRepo.findAccessibleById(id, getCurrentUsername())
                .orElseThrow(() -> new TeamNotFoundException(id));
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
        TeamValidator.validateOwnership(isOwnedBy(team, getCurrentUsername()), team.getId());
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
        TeamValidator.validateTrainerId(trainerId);
        TeamValidator.validateTrainerExists(trainerService.existsById(trainerId), trainerId);
        
        List<Team> result = teamRepo.findAccessibleByTrainerId(trainerId, getCurrentUsername());
        TeamValidator.validateByTrainerResult(result, trainerId);

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
        TeamValidator.validateVideogameId(videogameId);
        TeamValidator.validateVideogameExists(videogameService.existsById(videogameId), videogameId);
        
        List<Team> result = teamRepo.findAccessibleByVideogameId(videogameId, getCurrentUsername());
        TeamValidator.validateByVideogameResult(result, videogameId);

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
        TeamValidator.validateModality(modality);

        List<Team> result = teamRepo.findAccessibleByModality(modality, getCurrentUsername());
        TeamValidator.validateByModalityResult(result, modality);
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
        List<Team> result = teamRepo.findAccessibleByIsHidden(isHidden, getCurrentUsername());
        TeamValidator.validateByVisibilityResult(result, isHidden);

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

    private boolean isOwnedBy(Team team, String username) {
        return team.getTrainer().getUsername().equals(username);
    }

    /**
     * Resolves the username stored in the active Spring Security context.
     *
     * @return the authenticated trainer username.
     * @throws AuthenticationCredentialsNotFoundException when the context has no usable authentication.
     */
    String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return TeamValidator.getAuthenticatedUsername(authentication);
    }
}
