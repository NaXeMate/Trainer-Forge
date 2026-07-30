package dev.trainerforge.service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import dev.trainerforge.dto.response.PokemonTeamDto;
import dev.trainerforge.exception.InvalidFilterValueException;
import dev.trainerforge.exception.notfound.PokemonNotFoundException;
import dev.trainerforge.exception.notfound.PokemonTeamNotFoundException;
import dev.trainerforge.exception.notfound.TeamNotFoundException;
import dev.trainerforge.model.entities.PokemonTeam;
import dev.trainerforge.model.entities.Team;
import dev.trainerforge.repository.PokemonTeamRepository;

@Transactional(readOnly = true)
@Service
public class PokemonTeamService {

    private final PokemonTeamRepository pokemonTeamRepo;
    private final TeamService teamService;
    private final PokemonService pokemonService;

    private static final int MAX_POSITION = 6;
    private static final int MIN_POSITION = 1;

    private void validatePositionRange(int position) {
        if (position < MIN_POSITION || position > MAX_POSITION) {
            throw new InvalidFilterValueException("Position must be between " + MIN_POSITION + " and " + MAX_POSITION + ".");
        }
    }

    public PokemonTeamService(PokemonTeamRepository pokemonTeamRepo, TeamService teamService, PokemonService pokemonService) {
        this.pokemonTeamRepo = pokemonTeamRepo;
        this.teamService = teamService;
        this.pokemonService = pokemonService;
    }

    /**
     * Creates a team slot association after validating identifiers, ownership constraints, and slot availability.
     *
     * @param dto payload containing team identifier, pokemon identifier, and target slot position.
     * @return the persisted pokemon-team association.
     * @throws InvalidFilterValueException when identifiers are non-numeric, the slot is invalid, or uniqueness constraints are violated.
     * @throws TeamNotFoundException when the referenced team does not exist or is not owned by the current trainer.
     * @throws PokemonNotFoundException when the referenced pokemon does not exist.
     * @throws AuthenticationCredentialsNotFoundException when no authenticated trainer is available.
     */
    @Transactional
    public PokemonTeam createPokemonTeam(PokemonTeamDto dto) {
        final Long teamId;
        final Long pokemonId;
        try {
            teamId = Long.valueOf(dto.teamId());
            pokemonId = Long.valueOf(dto.pokemonId());
        } catch (NumberFormatException ex) {
            throw new InvalidFilterValueException("teamId and pokemonId must be numeric.");
        }

        Team team = teamService.findOwnedById(teamId);
        if (!pokemonService.existsById(pokemonId)) {
            throw new PokemonNotFoundException(pokemonId);
        }

        validatePositionRange(dto.position());

        if (pokemonTeamRepo.existsByTeamIdAndPosition(teamId, dto.position())) {
            throw new InvalidFilterValueException(
                "Position " + dto.position() + " is already occupied in team with id: " + teamId + ".");
        }

        if (pokemonTeamRepo.existsByTeamIdAndPokemonId(teamId, pokemonId)) {
            throw new InvalidFilterValueException(
                "Pokemon with id " + pokemonId + " is already in team with id: " + teamId + ".");
        }

        PokemonTeam newAssociation = new PokemonTeam();
        newAssociation.setTeam(team);
        newAssociation.setPokemon(pokemonService.findById(pokemonId));
        newAssociation.setPosition(dto.position());

        return pokemonTeamRepo.save(newAssociation);
    }

    /**
     * Moves an existing association to a different slot inside an owned team.
     * Requesting its current position is treated as an idempotent operation and does not persist a change.
     *
     * @param id identifier of the pokemon-team association to update.
     * @param newPosition target slot position within the team.
     * @return the updated association with the new position.
     * @throws PokemonTeamNotFoundException when no association exists for the provided identifier.
     * @throws TeamNotFoundException when the association belongs to a team not owned by the current trainer.
     * @throws InvalidFilterValueException when the position is out of range or already occupied in the team.
     * @throws AuthenticationCredentialsNotFoundException when no authenticated trainer is available.
     */
    @Transactional
    public PokemonTeam updatePosition(Long id, int newPosition) {
        PokemonTeam association = findStoredById(id);
        teamService.findOwnedById(association.getTeam().getId());

        validatePositionRange(newPosition);

        if (association.getPosition() == newPosition) {
            return association;
        }

        if (pokemonTeamRepo.existsByTeamIdAndPosition(association.getTeam().getId(), newPosition)) {
            throw new InvalidFilterValueException(
                "Position " + newPosition + " is already occupied in this team.");
        }

        association.setPosition(newPosition);
        return pokemonTeamRepo.save(association);
    }

    /**
     * Removes a pokemon slot association from a team owned by the current trainer.
     *
     * @param id identifier of the association to remove.
     * @throws PokemonTeamNotFoundException when no association exists for the provided identifier.
     * @throws TeamNotFoundException when the association belongs to a team not owned by the current trainer.
     * @throws AuthenticationCredentialsNotFoundException when no authenticated trainer is available.
     */
    @Transactional
    public void deletePokemonTeam(Long id) {
        PokemonTeam association = findStoredById(id);
        teamService.findOwnedById(association.getTeam().getId());
        pokemonTeamRepo.delete(association);
        System.out.println("Deleted PokemonTeam association with id: " + id + ".");
    }

    /**
     * Retrieves every slot association accessible to the current trainer.
     * Associations from public teams and owned hidden teams are returned ordered by team and position.
     *
     * @return accessible associations in deterministic order, or an empty list when none are available.
     * @throws AuthenticationCredentialsNotFoundException when no authenticated trainer is available.
     */
    public List<PokemonTeam> findAll() {
        return filterAccessible(pokemonTeamRepo.findAllByOrderByTeamIdAscPositionAsc());
    }

    /**
     * Retrieves a slot association only when its team is visible to the current trainer.
     *
     * @param id identifier of the requested association.
     * @return the accessible association.
     * @throws PokemonTeamNotFoundException when the association does not exist.
     * @throws TeamNotFoundException when its team is hidden from the current trainer.
     * @throws AuthenticationCredentialsNotFoundException when no authenticated trainer is available.
     */
    public PokemonTeam findById(Long id) {
        PokemonTeam association = findStoredById(id);
        teamService.findById(association.getTeam().getId());
        return association;
    }

    /**
     * Retrieves all slot associations for a team after validating the team identifier.
     *
     * @param teamId identifier of the team whose slots are requested.
     * @return every pokemon-team association linked to the given team, ordered by position.
     * @throws TeamNotFoundException when the team does not exist or is hidden from the current trainer.
     * @throws PokemonTeamNotFoundException when the team exists but has no slot associations.
     * @throws AuthenticationCredentialsNotFoundException when no authenticated trainer is available.
     */
    public List<PokemonTeam> findByTeamId(Long teamId) {
        teamService.findById(teamId);

        List<PokemonTeam> result = pokemonTeamRepo.findByTeamIdOrderByPositionAsc(teamId);

        if (result.isEmpty()) {
            throw new PokemonTeamNotFoundException("No Pokemon-Team associations found for team with id: " + teamId + ".");
        }
        
        return result;
    }
    
    /**
     * Retrieves all team associations where a specific pokemon appears.
     *
     * @param pokemonId identifier of the pokemon used to filter associations.
     * @return accessible pokemon-team associations that reference the pokemon.
     * @throws PokemonNotFoundException when the pokemon does not exist.
     * @throws PokemonTeamNotFoundException when the pokemon has no associations visible to the current trainer.
     * @throws AuthenticationCredentialsNotFoundException when no authenticated trainer is available.
     */
    public List<PokemonTeam> findByPokemonId(Long pokemonId) {
        if (!pokemonService.existsById(pokemonId)) {
            throw new PokemonNotFoundException(pokemonId);
        }
        
        List<PokemonTeam> result = filterAccessible(pokemonTeamRepo.findByPokemonId(pokemonId));

        if (result.isEmpty()) {
            throw new PokemonTeamNotFoundException("No team-pokemon association found with pokemon id: " + pokemonId + ".");
        }
        
        return result;
    }
    
    /**
     * Retrieves associations that occupy a specific slot position.
     *
     * @param position slot index to filter across teams.
     * @return accessible associations stored in the requested position.
     * @throws InvalidFilterValueException when the position is outside the supported team slot range.
     * @throws PokemonTeamNotFoundException when no visible association exists at the provided position.
     * @throws AuthenticationCredentialsNotFoundException when no authenticated trainer is available.
     */
    public List<PokemonTeam> findByPosition(int position) {
        validatePositionRange(position);
        
        List<PokemonTeam> result = filterAccessible(pokemonTeamRepo.findByPosition(position));

        if (result.isEmpty()) {
            throw new PokemonTeamNotFoundException("No team-pokemon association found with position: " + position + ".");
        }

        return result;
    }

    public boolean existsById(Long id) {
        return pokemonTeamRepo.existsById(id);
    }

    private PokemonTeam findStoredById(Long id) {
        return pokemonTeamRepo.findById(id)
            .orElseThrow(() -> new PokemonTeamNotFoundException(id));
    }

    /**
     * Removes associations whose teams are not visible to the current trainer while preserving input order.
     *
     * @param associations associations already retrieved from persistence.
     * @return associations belonging to public teams or owned hidden teams.
     */
    private List<PokemonTeam> filterAccessible(List<PokemonTeam> associations) {
        Set<Long> accessibleTeamIds = teamService.findAll().stream()
            .map(team -> team.getId())
            .collect(Collectors.toSet());

        return associations.stream()
            .filter(association -> accessibleTeamIds.contains(association.getTeam().getId()))
            .toList();
    }

}
