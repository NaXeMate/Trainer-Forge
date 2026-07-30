package dev.trainerforge.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import dev.trainerforge.dto.response.PokemonTeamDto;
import dev.trainerforge.mapper.PokemonTeamMapper;
import dev.trainerforge.service.PokemonTeamService;

@RestController
@RequestMapping("/api/pokemon-teams")
public class PokemonTeamController {

    private final PokemonTeamService pokemonTeamService;
    private final PokemonTeamMapper pokemonTeamMapper;

    public PokemonTeamController(PokemonTeamService pokemonTeamService, PokemonTeamMapper pokemonTeamMapper) {
        this.pokemonTeamService = pokemonTeamService;
        this.pokemonTeamMapper = pokemonTeamMapper;
    }

    @GetMapping
    public ResponseEntity<List<PokemonTeamDto>> getAllPokemonTeams() {
        List<PokemonTeamDto> associations = pokemonTeamService.findAll().stream()
            .map(pokemonTeamMapper::toDto)
            .toList();
        return ResponseEntity.ok(associations);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PokemonTeamDto> getPokemonTeamById(@PathVariable Long id) {
        PokemonTeamDto association = pokemonTeamMapper.toDto(pokemonTeamService.findById(id));
        return ResponseEntity.ok(association);
    }

    @GetMapping("/team/{teamId}")
    public ResponseEntity<List<PokemonTeamDto>> getPokemonTeamsByTeam(@PathVariable Long teamId) {
        List<PokemonTeamDto> associations = pokemonTeamService.findByTeamId(teamId).stream()
            .map(pokemonTeamMapper::toDto)
            .toList();
        return ResponseEntity.ok(associations);
    }

    @PostMapping
    public ResponseEntity<PokemonTeamDto> createPokemonTeam(@RequestBody PokemonTeamDto dto) {
        PokemonTeamDto created = pokemonTeamMapper.toDto(pokemonTeamService.createPokemonTeam(dto));
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PatchMapping("/{id}/position")
    public ResponseEntity<PokemonTeamDto> updatePosition(
            @PathVariable Long id,
            @RequestParam int value) {
        PokemonTeamDto updated = pokemonTeamMapper.toDto(pokemonTeamService.updatePosition(id, value));
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePokemonTeam(@PathVariable Long id) {
        pokemonTeamService.deletePokemonTeam(id);
        return ResponseEntity.noContent().build();
    }
}
