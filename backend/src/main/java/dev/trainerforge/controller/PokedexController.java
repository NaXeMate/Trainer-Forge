package dev.trainerforge.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import dev.trainerforge.dto.response.PokedexDto;
import dev.trainerforge.mapper.PokedexMapper;
import dev.trainerforge.model.enumerated.PokemonClass;
import dev.trainerforge.service.PokedexService;

@RestController
@RequestMapping("/api/pokedex")
public class PokedexController {

    private final PokedexService pokedexService;
    private final PokedexMapper pokedexMapper;

    public PokedexController(PokedexService pokedexService, PokedexMapper pokedexMapper) {
        this.pokedexService = pokedexService;
        this.pokedexMapper = pokedexMapper;
    }

    @GetMapping
    public ResponseEntity<List<PokedexDto>> getAllPokedexEntries() {
        List<PokedexDto> pokedexEntries = pokedexService.findAll()
                .stream().map(pokedexMapper::toDto).toList();
        return ResponseEntity.ok(pokedexEntries);
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<PokedexDto> getPokedexEntryByName(@PathVariable String name) {
        PokedexDto pokedexEntry = pokedexMapper.toDto(pokedexService.findByName(name));
        return ResponseEntity.ok(pokedexEntry);
    }

    @GetMapping("/number/{nationalPokedex}")
    public ResponseEntity<List<PokedexDto>> getPokedexEntriesByNationalNumber(@PathVariable Long nationalPokedex) {
        List<PokedexDto> pokedexEntries = pokedexService.findByNationalPokedex(nationalPokedex)
                .stream().map(pokedexMapper::toDto).toList();
        return ResponseEntity.ok(pokedexEntries);
    }

    @GetMapping("/type/{typeId}")
    public ResponseEntity<List<PokedexDto>> getPokedexEntriesByType(@PathVariable Long typeId) {
        List<PokedexDto> pokedexEntries = pokedexService.findByType(typeId)
                .stream().map(pokedexMapper::toDto).toList();
        return ResponseEntity.ok(pokedexEntries);
    }

    @GetMapping("/generation/{generationId}")
    public ResponseEntity<List<PokedexDto>> getPokedexEntriesByGeneration(@PathVariable Long generationId) {
        List<PokedexDto> pokedexEntries = pokedexService.findByGenerationId(generationId)
                .stream().map(pokedexMapper::toDto).toList();
        return ResponseEntity.ok(pokedexEntries);
    }

    @GetMapping("/region/{regionId}")
    public ResponseEntity<List<PokedexDto>> getPokedexEntriesByRegion(@PathVariable Long regionId) {
        List<PokedexDto> pokedexEntries = pokedexService.findByRegionId(regionId)
                .stream().map(pokedexMapper::toDto).toList();
        return ResponseEntity.ok(pokedexEntries);
    }

    @GetMapping("/ability/{abilityId}")
    public ResponseEntity<List<PokedexDto>> getPokedexEntriesByAbility(@PathVariable Long abilityId) {
        List<PokedexDto> pokedexEntries = pokedexService.findByAbility(abilityId)
                .stream().map(pokedexMapper::toDto).toList();
        return ResponseEntity.ok(pokedexEntries);
    }

    @GetMapping("/class/{pokemonClass}")
    public ResponseEntity<List<PokedexDto>> getPokedexEntriesByClass(@PathVariable PokemonClass pokemonClass) {
        List<PokedexDto> pokedexEntries = pokedexService.findByPokemonClass(pokemonClass)
                .stream().map(pokedexMapper::toDto).toList();
        return ResponseEntity.ok(pokedexEntries);
    }

    @GetMapping("/stats/hp")
    public ResponseEntity<List<PokedexDto>> getPokedexEntriesByHp(@RequestParam int min, @RequestParam int max) {
        List<PokedexDto> pokedexEntries = pokedexService.findByHpBaseBetween(min, max)
                .stream().map(pokedexMapper::toDto).toList();
        return ResponseEntity.ok(pokedexEntries);
    }

    @GetMapping("/stats/attack")
    public ResponseEntity<List<PokedexDto>> getPokedexEntriesByAttack(@RequestParam int min, @RequestParam int max) {
        List<PokedexDto> pokedexEntries = pokedexService.findByAttackBaseBetween(min, max)
                .stream().map(pokedexMapper::toDto).toList();
        return ResponseEntity.ok(pokedexEntries);
    }

    @GetMapping("/stats/defense")
    public ResponseEntity<List<PokedexDto>> getPokedexEntriesByDefense(@RequestParam int min, @RequestParam int max) {
        List<PokedexDto> pokedexEntries = pokedexService.findByDefenseBaseBetween(min, max)
                .stream().map(pokedexMapper::toDto).toList();
        return ResponseEntity.ok(pokedexEntries);
    }

    @GetMapping("/stats/special-attack")
    public ResponseEntity<List<PokedexDto>> getPokedexEntriesBySpecialAttack(@RequestParam int min, @RequestParam int max) {
        List<PokedexDto> pokedexEntries = pokedexService.findBySpecialAttackBaseBetween(min, max)
                .stream().map(pokedexMapper::toDto).toList();
        return ResponseEntity.ok(pokedexEntries);
    }

    @GetMapping("/stats/special-defense")
    public ResponseEntity<List<PokedexDto>> getPokedexEntriesBySpecialDefense(@RequestParam int min, @RequestParam int max) {
        List<PokedexDto> pokedexEntries = pokedexService.findBySpecialDefenseBaseBetween(min, max)
                .stream().map(pokedexMapper::toDto).toList();
        return ResponseEntity.ok(pokedexEntries);
    }

    @GetMapping("/stats/speed")
    public ResponseEntity<List<PokedexDto>> getPokedexEntriesBySpeed(@RequestParam int min, @RequestParam int max) {
        List<PokedexDto> pokedexEntries = pokedexService.findBySpeedBaseBetween(min, max)
                .stream().map(pokedexMapper::toDto).toList();
        return ResponseEntity.ok(pokedexEntries);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PokedexDto> getPokedexEntryById(@PathVariable Long id) {
        PokedexDto pokedexEntry = pokedexMapper.toDto(pokedexService.findById(id));
        return ResponseEntity.ok(pokedexEntry);
    }
}