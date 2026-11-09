package dev.trainerforge.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import dev.trainerforge.dto.response.MoveDto;
import dev.trainerforge.dto.response.PokemonItemDto;
import dev.trainerforge.dto.response.TeamDto;
import dev.trainerforge.mapper.MoveMapper;
import dev.trainerforge.mapper.PokemonItemMapper;
import dev.trainerforge.mapper.TeamMapper;
import dev.trainerforge.service.BattleSimulatorService;
import dev.trainerforge.service.MoveService;
import dev.trainerforge.service.PokemonItemService;
import dev.trainerforge.service.TeamService;

@RestController
@RequestMapping("/api/simulator")
public class BattleSimulatorController {

    private final BattleSimulatorService battleSimulatorService;
    private final TeamService teamService;
    private final TeamMapper teamMapper;
    private final MoveService moveService;
    private final MoveMapper moveMapper;
    private final PokemonItemService pokemonItemService;
    private final PokemonItemMapper pokemonItemMapper;

    public BattleSimulatorController(BattleSimulatorService battleSimulatorService, TeamService teamService,
            TeamMapper teamMapper, MoveService moveService, MoveMapper moveMapper,
            PokemonItemService pokemonItemService, PokemonItemMapper pokemonItemMapper) {
        this.battleSimulatorService = battleSimulatorService;
        this.teamService = teamService;
        this.teamMapper = teamMapper;
        this.moveService = moveService;
        this.moveMapper = moveMapper;
        this.pokemonItemService = pokemonItemService;
        this.pokemonItemMapper = pokemonItemMapper;
    }

    @GetMapping("/moves")
    public ResponseEntity<List<MoveDto>> getMoves() {
        List<MoveDto> moves = moveService.findAll()
                .stream()
                .map(moveMapper::toDto)
                .toList();
        return ResponseEntity.ok(moves);
    }

    @GetMapping("/items")
    public ResponseEntity<List<PokemonItemDto>> getEquippableItems() {
        List<PokemonItemDto> items = pokemonItemService.findAllEquippableItems()
                .stream()
                .map(pokemonItemMapper::toDto)
                .toList();
        return ResponseEntity.ok(items);
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
