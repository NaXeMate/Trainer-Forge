package dev.trainerforge.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dev.trainerforge.dto.response.TeamDto;
import dev.trainerforge.mapper.TeamMapper;
import dev.trainerforge.model.entities.Team;
import dev.trainerforge.model.enumerated.TeamModality;
import dev.trainerforge.service.TeamService;

@RestController
@RequestMapping("/api/teams")
public class TeamController {

    private final TeamService teamService;
    private final TeamMapper teamMapper;

    public TeamController(TeamService teamService, TeamMapper teamMapper) {
        this.teamService = teamService;
        this.teamMapper = teamMapper;
    }

    @GetMapping
    public ResponseEntity<List<TeamDto>> getAllTeams() {
        List<TeamDto> teams = teamService.findAll()
                .stream()
                .map(teamMapper::toDto)
                .toList();
        return ResponseEntity.ok(teams);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TeamDto> getTeamById(@PathVariable Long id) {
        TeamDto team = teamMapper.toDto(teamService.findById(id));
        return ResponseEntity.ok(team);
    }

    @GetMapping("/trainer/{trainerId}")
    public ResponseEntity<List<TeamDto>> getTeamsByTrainer(@PathVariable Long trainerId) {
        List<TeamDto> teams = teamService.findByTrainerId(trainerId)
                .stream()
                .map(teamMapper::toDto)
                .toList();
        return ResponseEntity.ok(teams);
    }

    @GetMapping("/videogame/{videogameId}")
    public ResponseEntity<List<TeamDto>> getTeamsByVideogame(@PathVariable Long videogameId) {
        List<TeamDto> teams = teamService.findByVideogameId(videogameId)
                .stream()
                .map(teamMapper::toDto)
                .toList();
        return ResponseEntity.ok(teams);
    }

    @GetMapping("/modality/{modality}")
    public ResponseEntity<List<TeamDto>> getTeamsByModality(@PathVariable TeamModality modality) {
        List<TeamDto> teams = teamService.findByModality(modality)
                .stream()
                .map(teamMapper::toDto)
                .toList();
        return ResponseEntity.ok(teams);
    }

    @GetMapping("/hidden/{isHidden}")
    public ResponseEntity<List<TeamDto>> getTeamsByVisibility(@PathVariable boolean isHidden) {
        List<TeamDto> teams = teamService.findByIsHidden(isHidden)
                .stream()
                .map(teamMapper::toDto)
                .toList();
        return ResponseEntity.ok(teams);
    }

    @PostMapping
    public ResponseEntity<TeamDto> createTeam(@RequestBody TeamDto dto) {
        Team created = teamService.createTeam(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(teamMapper.toDto(created));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TeamDto> updateTeam(@PathVariable Long id, @RequestBody TeamDto dto) {
        Team updated = teamService.updateTeam(id, dto);
        return ResponseEntity.ok(teamMapper.toDto(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTeam(@PathVariable Long id) {
        teamService.deleteTeam(id);
        return ResponseEntity.noContent().build();
    }
}
