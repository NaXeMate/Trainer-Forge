package dev.trainerforge.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import dev.trainerforge.dto.response.PokemonTeamDto;
import dev.trainerforge.exception.InvalidFilterValueException;
import dev.trainerforge.exception.notfound.PokemonNotFoundException;
import dev.trainerforge.exception.notfound.PokemonTeamNotFoundException;
import dev.trainerforge.exception.notfound.TeamNotFoundException;
import dev.trainerforge.model.entities.PokemonTeam;
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

        if (!teamService.existsById(teamId)) {
            throw new TeamNotFoundException(teamId);
        }
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
        newAssociation.setTeam(teamService.findById(teamId));
        newAssociation.setPokemon(pokemonService.findById(pokemonId));
        newAssociation.setPosition(dto.position());

        return pokemonTeamRepo.save(newAssociation);
    }

    @Transactional
    public PokemonTeam updatePosition(Long id, int newPosition) {
        PokemonTeam association = this.findById(id);

        validatePositionRange(newPosition);

        if (pokemonTeamRepo.existsByTeamIdAndPosition(association.getTeam().getId(), newPosition)) {
            throw new InvalidFilterValueException(
                "Position " + newPosition + " is already occupied in this team.");
        }

        association.setPosition(newPosition);
        return pokemonTeamRepo.save(association);
    }

    @Transactional
    public void deletePokemonTeam(Long id) {
        PokemonTeam association = this.findById(id);
        pokemonTeamRepo.delete(association);
        System.out.println("Deleted PokemonTeam association with id: " + id + ".");
    }

    public List<PokemonTeam> findAll() {
        return pokemonTeamRepo.findAll();
    }

    public PokemonTeam findById(Long id) {
        return pokemonTeamRepo.findById(id)
            .orElseThrow(() -> new PokemonTeamNotFoundException(id));
    }

    public List<PokemonTeam> findByTeamId(Long teamId) {
        if (!teamService.existsById(teamId)) {
            throw new TeamNotFoundException(teamId);
        }

        List<PokemonTeam> result = pokemonTeamRepo.findByTeamId(teamId);

        if (result.isEmpty()) {
            throw new PokemonTeamNotFoundException("No Pokemon-Team associations found for team with id: " + teamId + ".");
        }
        
        return result;
    }
    
    public List<PokemonTeam> findByPokemonId(Long pokemonId) {
        if (!pokemonService.existsById(pokemonId)) {
            throw new PokemonNotFoundException(pokemonId);
        }
        
        List<PokemonTeam> result = pokemonTeamRepo.findByPokemonId(pokemonId);

        if (result.isEmpty()) {
            throw new PokemonTeamNotFoundException("No team-pokemon association found with pokemon id: " + pokemonId + ".");
        }
        
        return result;
    }
    
    public List<PokemonTeam> findByPosition(int position) {
        validatePositionRange(position);
        
        List<PokemonTeam> result = pokemonTeamRepo.findByPosition(position);

        if (result.isEmpty()) {
            throw new PokemonTeamNotFoundException("No team-pokemon association found with position: " + position + ".");
        }

        return result;
    }

    public boolean existsById(Long id) {
        return pokemonTeamRepo.existsById(id);
    }

}
