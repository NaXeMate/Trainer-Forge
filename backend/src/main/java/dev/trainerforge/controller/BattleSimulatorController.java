package dev.trainerforge.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import dev.trainerforge.dto.response.TeamDto;
import dev.trainerforge.mapper.TeamMapper;
import dev.trainerforge.service.BattleSimulatorService;
import dev.trainerforge.service.TeamService;

@RestController
@RequestMapping("/api/simulator")
public class BattleSimulatorController {

    private final BattleSimulatorService battleSimulatorService;
    private final TeamService teamService;
    private final TeamMapper teamMapper;

    public BattleSimulatorController(BattleSimulatorService battleSimulatorService, TeamService teamService, TeamMapper teamMapper) {
        this.battleSimulatorService = battleSimulatorService;
        this.teamService = teamService;
        this.teamMapper = teamMapper;
    }

    @GetMapping("/available-teams")
    public ResponseEntity<List<TeamDto>> getAvailableTeams() {
        List<TeamDto> teams = teamService.findAll()
                .stream()
                .map(teamMapper::toDto)
                .toList();
        return ResponseEntity.ok(teams);
    }

    @GetMapping("/battle")
    public ResponseEntity<String> simulateBattle(
            @RequestParam Long teamId1,
            @RequestParam Long teamId2) {
        String result = battleSimulatorService.simulateBattle(teamId1, teamId2);
        return ResponseEntity.ok(result);
    }

}
