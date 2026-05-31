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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import dev.trainerforge.dto.response.PokemonDto;
import dev.trainerforge.mapper.PokemonMapper;
import dev.trainerforge.model.enumerated.Gender;
import dev.trainerforge.service.PokemonService;

@RestController
@RequestMapping("/api/pokemon")
public class PokemonController {
    private final PokemonService pokemonService;
    private final PokemonMapper pokemonMapper;

    public PokemonController(PokemonService pokemonService, PokemonMapper pokemonMapper) {
        this.pokemonService = pokemonService;
        this.pokemonMapper = pokemonMapper;
    }

    // CRUD

    @GetMapping
    public ResponseEntity<List<PokemonDto>> getAllPokemon() {
        List<PokemonDto> pokemon = pokemonService.findAll()
                .stream().map(pokemonMapper::toDto).toList();
        return ResponseEntity.ok(pokemon);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PokemonDto> getPokemonById(@PathVariable Long id) {
        PokemonDto pokemon = pokemonMapper.toDto(pokemonService.findById(id));
        return ResponseEntity.ok(pokemon);
    }

    @PostMapping
    public ResponseEntity<PokemonDto> createPokemon(@RequestBody PokemonDto dto) {
        PokemonDto createdPokemon = pokemonMapper.toDto(pokemonService.createPokemon(dto));
        return ResponseEntity.status(HttpStatus.CREATED).body(createdPokemon);
    }

    @PutMapping("/{id}") // Used from the EV Configuration full editor and any field update
    public ResponseEntity<PokemonDto> updatePokemon(@PathVariable Long id, @RequestBody PokemonDto dto) {
        PokemonDto updatedPokemon = pokemonMapper.toDto(pokemonService.updatePokemon(id, dto));
        return ResponseEntity.ok(updatedPokemon);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePokemon(@PathVariable Long id) {
        pokemonService.deletePokemon(id);
        return ResponseEntity.noContent().build();
    }

    // QUERIES ("Personal" data)

    @GetMapping("/species/{speciesId}")
    public ResponseEntity<List<PokemonDto>> getPokemonBySpecies(@PathVariable Long speciesId) {
        List<PokemonDto> pokemon = pokemonService.findBySpeciesId(speciesId)
                .stream().map(pokemonMapper::toDto).toList();
        return ResponseEntity.ok(pokemon);
    }

    @GetMapping("/nickname/{nickname}")
    public ResponseEntity<List<PokemonDto>> getPokemonByNickname(@PathVariable String nickname) {
        List<PokemonDto> pokemon = pokemonService.findByNickname(nickname)
                .stream().map(pokemonMapper::toDto).toList();
        return ResponseEntity.ok(pokemon);
    }

    @GetMapping("/location/{locationFound}")
    public ResponseEntity<List<PokemonDto>> getPokemonByLocationFound(@PathVariable String locationFound) {
        List<PokemonDto> pokemon = pokemonService.findByLocationFound(locationFound)
                .stream().map(pokemonMapper::toDto).toList();
        return ResponseEntity.ok(pokemon);
    }

    // QUERIES

    @GetMapping("/level")
    public ResponseEntity<List<PokemonDto>> getPokemonByLevel(
            @RequestParam int min,
            @RequestParam int max) {
        List<PokemonDto> pokemon = pokemonService.findByLevelBetween(min, max)
                .stream().map(pokemonMapper::toDto).toList();
        return ResponseEntity.ok(pokemon);
    }

    @GetMapping("/shiny/{shiny}")
    public ResponseEntity<List<PokemonDto>> getPokemonByShiny(@PathVariable boolean shiny) {
        List<PokemonDto> pokemon = pokemonService.findByShiny(shiny)
                .stream().map(pokemonMapper::toDto).toList();
        return ResponseEntity.ok(pokemon);
    }

    @GetMapping("/gender/{gender}")
    public ResponseEntity<List<PokemonDto>> getPokemonByGender(@PathVariable Gender gender) {
        List<PokemonDto> pokemon = pokemonService.findByGender(gender)
                .stream().map(pokemonMapper::toDto).toList();
        return ResponseEntity.ok(pokemon);
    }

    @GetMapping("/ability/{abilityId}")
    public ResponseEntity<List<PokemonDto>> getPokemonByAbility(@PathVariable Long abilityId) {
        List<PokemonDto> pokemon = pokemonService.findByAbilityId(abilityId)
                .stream().map(pokemonMapper::toDto).toList();
        return ResponseEntity.ok(pokemon);
    }

    @GetMapping("/move/{moveId}")
    public ResponseEntity<List<PokemonDto>> getPokemonByMove(@PathVariable Long moveId) {
        List<PokemonDto> pokemon = pokemonService.findByMoveId(moveId)
                .stream().map(pokemonMapper::toDto).toList();
        return ResponseEntity.ok(pokemon);
    }

    @GetMapping("/item/{itemId}")
    public ResponseEntity<List<PokemonDto>> getPokemonByEquippedItem(@PathVariable Long itemId) {
        List<PokemonDto> pokemon = pokemonService.findByEquippedItemId(itemId)
                .stream().map(pokemonMapper::toDto).toList();
        return ResponseEntity.ok(pokemon);
    }

    // QUERIES — (EVs)

    @GetMapping("/evs/hp")
    public ResponseEntity<List<PokemonDto>> getPokemonByHpEv(@RequestParam int min, @RequestParam int max) {
        List<PokemonDto> pokemon = pokemonService.findByHpEvBetween(min, max)
                .stream().map(pokemonMapper::toDto).toList();
        return ResponseEntity.ok(pokemon);
    }

    @GetMapping("/evs/attack")
    public ResponseEntity<List<PokemonDto>> getPokemonByAttackEv(@RequestParam int min, @RequestParam int max) {
        List<PokemonDto> pokemon = pokemonService.findByAttackEvBetween(min, max)
                .stream().map(pokemonMapper::toDto).toList();
        return ResponseEntity.ok(pokemon);
    }

    @GetMapping("/evs/defense")
    public ResponseEntity<List<PokemonDto>> getPokemonByDefenseEv(@RequestParam int min, @RequestParam int max) {
        List<PokemonDto> pokemon = pokemonService.findByDefenseEvBetween(min, max)
                .stream().map(pokemonMapper::toDto).toList();
        return ResponseEntity.ok(pokemon);
    }

    @GetMapping("/evs/special-attack")
    public ResponseEntity<List<PokemonDto>> getPokemonBySpecialAttackEv(@RequestParam int min, @RequestParam int max) {
        List<PokemonDto> pokemon = pokemonService.findBySpecialAttackEvBetween(min, max)
                .stream().map(pokemonMapper::toDto).toList();
        return ResponseEntity.ok(pokemon);
    }

    @GetMapping("/evs/special-defense")
    public ResponseEntity<List<PokemonDto>> getPokemonBySpecialDefenseEv(@RequestParam int min, @RequestParam int max) {
        List<PokemonDto> pokemon = pokemonService.findBySpecialDefenseEvBetween(min, max)
                .stream().map(pokemonMapper::toDto).toList();
        return ResponseEntity.ok(pokemon);
    }

    @GetMapping("/evs/speed")
    public ResponseEntity<List<PokemonDto>> getPokemonBySpeedEv(@RequestParam int min, @RequestParam int max) {
        List<PokemonDto> pokemon = pokemonService.findBySpeedEvBetween(min, max)
                .stream().map(pokemonMapper::toDto).toList();
        return ResponseEntity.ok(pokemon);
    }
}
