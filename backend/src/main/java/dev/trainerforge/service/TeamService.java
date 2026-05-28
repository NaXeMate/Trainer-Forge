package dev.trainerforge.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import dev.trainerforge.dto.response.TeamDto;
import dev.trainerforge.exception.InvalidFilterValueException;
import dev.trainerforge.exception.TeamNotFoundException;
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

    @Transactional
    public Team createTeam(TeamDto dto) {
        Team newTeam = new Team();

        teamMapper.updateEntityFromDto(dto, newTeam);

        return teamRepo.save(newTeam);
    }

    @Transactional
    public Team updateTeam(Long id, TeamDto dto) {
        Team team = this.findById(id);
        
        teamMapper.updateEntityFromDto(dto, team);
        
        return teamRepo.save(team);
    }

    @Transactional
    public void deleteTeam(Long id) {
        Team team = this.findById(id);
        teamRepo.delete(team);
        System.out.println("Deleted Team with id: " + id);
    }

    public List<Team> findAll() {
        return teamRepo.findAll();
    }

    public Team findById(Long id) {
        return teamRepo.findById(id)
        .orElseThrow(() -> new TeamNotFoundException(id));
    }

    public List<Team> findByTrainerId(Long trainerId) {
        if (trainerId == null) {
            throw new InvalidFilterValueException("The trainer ID cannot be null.");
        }

        if (!trainerService.existsById(trainerId)) {
            throw new InvalidFilterValueException("No trainer found with id: " + trainerId + ".");
        }
        
        List<Team> result = teamRepo.findByTrainerId(trainerId);
        
        if (result.isEmpty()) {
            throw new TeamNotFoundException("No teams found for trainer with id: " + trainerId + ".");
        }
        
        return result;
    }

    public List<Team> findByVideogameId(Long videogameId) {
        if (videogameId == null) {
            throw new InvalidFilterValueException("The videogame ID cannot be null.");
        }
        
        if (!videogameService.existsById(videogameId)) {
            throw new InvalidFilterValueException("No videogame found with id: " + videogameId + ".");
        }
        
        List<Team> result = teamRepo.findByVideogameId(videogameId);
        
        if (result.isEmpty()) {
            throw new TeamNotFoundException("No teams found for videogame with id: " + videogameId + ".");
        }
        
        return result;
    }

    public List<Team> findByModality(TeamModality modality) {
        if (modality == null) {
            throw new InvalidFilterValueException("The modality cannot be null.");
        }

        List<Team> result = teamRepo.findByModality(modality);
        
        if (result.isEmpty()) {
            throw new TeamNotFoundException("No teams found for modality: " + modality + ".");
        }
        return result;
    }

    public List<Team> findByIsHidden(boolean isHidden) {
        List<Team> result = teamRepo.findByIsHidden(isHidden);
        
        if (result.isEmpty()) {
            String visibility = isHidden ? "hidden" : "public";
            throw new TeamNotFoundException("No " + visibility + " teams were found.");
        }
    
        return result;
    }

    public boolean existsById(Long id) {
        return teamRepo.existsById(id);
    }
}
