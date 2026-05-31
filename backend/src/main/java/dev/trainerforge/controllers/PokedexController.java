package dev.trainerforge.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import dev.trainerforge.model.entities.Pokedex;
import dev.trainerforge.model.enumerated.PokemonClass;
import dev.trainerforge.service.PokedexService;

@RestController
@RequestMapping("/api/pokedex")
public class PokedexController {

    private final PokedexService pokedexService;

    public PokedexController(PokedexService pokedexService) {
        this.pokedexService = pokedexService;
    }

    // POKEDEX LIST PAGE - Complete list, name and number searching.

    @GetMapping
    public ResponseEntity<List<Pokedex>> getAllPokedexEntries() {
        List<Pokedex> pokedexEntries = pokedexService.findAll();
        return ResponseEntity.ok(pokedexEntries);
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<Pokedex> getPokedexEntryByName(@PathVariable String name) {
        Pokedex pokedexEntry = pokedexService.findByName(name);
        return ResponseEntity.ok(pokedexEntry);
    }

    @GetMapping("/number/{nationalPokedexNumber}")
    public ResponseEntity<List<Pokedex>> getPokedexEntryByNumber(@PathVariable Long nationalPokedexNumber) {
        List<Pokedex> pokedexEntries = pokedexService.findByNationalPokedex(nationalPokedexNumber);
        return ResponseEntity.ok(pokedexEntries);
    }

    // FILTERS - Tytpe, Generation, Region, Ability, Class
    
    @GetMapping("/type/{typeId}")
    public ResponseEntity<List<Pokedex>> getPokedexEntriesByType(@PathVariable Long typeId) {
        List<Pokedex> pokedexEntries = pokedexService.findByType(typeId);
        return ResponseEntity.ok(pokedexEntries);
    }

    @GetMapping("/generation/{generationId}")
    public ResponseEntity<List<Pokedex>> getPokedexEntriesByGeneration(@PathVariable Long generationId) {
        List<Pokedex> pokedexEntries = pokedexService.findByGenerationId(generationId);
        return ResponseEntity.ok(pokedexEntries);
    }

    @GetMapping("/region/{regionId}")
    public ResponseEntity<List<Pokedex>> getPokedexEntriesByRegion(@PathVariable Long regionId) {
        List<Pokedex> pokedexEntries = pokedexService.findByRegionId(regionId);
        return ResponseEntity.ok(pokedexEntries);
    }

    @GetMapping("/ability/{abilityId}")
    public ResponseEntity<List<Pokedex>> getPokedexEntriesByAbility(@PathVariable Long abilityId) {
        List<Pokedex> pokedexEntries = pokedexService.findByAbility(abilityId);
        return ResponseEntity.ok(pokedexEntries);
    }

    @GetMapping("/class/{pokemonClass}")
    public ResponseEntity<List<Pokedex>> getPokedexEntriesByClass(@PathVariable PokemonClass pokemonClass) {
        List<Pokedex> pokedexEntries = pokedexService.findByPokemonClass(pokemonClass);
        return ResponseEntity.ok(pokedexEntries);
    }

    // STAT FILTERS

    @GetMapping("/stats/hp")
    public ResponseEntity<List<Pokedex>> getPokedexEntriesByHp(@RequestParam int min, @RequestParam int max) {
        List<Pokedex> pokedexEntries = pokedexService.findByHpBaseBetween(min, max);
        return ResponseEntity.ok(pokedexEntries);
    }

    @GetMapping("/stats/attack")
    public ResponseEntity<List<Pokedex>> getPokedexEntriesByAttack(@RequestParam int min, @RequestParam int max) {
        List<Pokedex> pokedexEntries = pokedexService.findByAttackBaseBetween(min, max);
        return ResponseEntity.ok(pokedexEntries);
    }

    @GetMapping("/stats/defense")
    public ResponseEntity<List<Pokedex>> getPokedexEntriesByDefense(@RequestParam int min, @RequestParam int max) {
        List<Pokedex> pokedexEntries = pokedexService.findByDefenseBaseBetween(min, max);
        return ResponseEntity.ok(pokedexEntries);
    }

    @GetMapping("/stats/special-attack")
    public ResponseEntity<List<Pokedex>> getPokedexEntriesBySpecialAttack(@RequestParam int min, @RequestParam int max) {
        List<Pokedex> pokedexEntries = pokedexService.findBySpecialAttackBaseBetween(min, max);
        return ResponseEntity.ok(pokedexEntries);
    }

    @GetMapping("/stats/special-defense")
    public ResponseEntity<List<Pokedex>> getPokedexEntriesBySpecialDefense(@RequestParam int min, @RequestParam int max) {
        List<Pokedex> pokedexEntries = pokedexService.findBySpecialDefenseBaseBetween(min, max);
        return ResponseEntity.ok(pokedexEntries);
    }

    @GetMapping("/stats/speed")
    public ResponseEntity<List<Pokedex>> getPokedexEntriesBySpeed(@RequestParam int min, @RequestParam int max) {
        List<Pokedex> pokedexEntries = pokedexService.findBySpeedBaseBetween(min, max);
        return ResponseEntity.ok(pokedexEntries);
    }

    // POKEDEX DETAIL PAGE

    @GetMapping("/{id}")
    public ResponseEntity<Pokedex> getPokedexEntryById(@PathVariable Long id) {
        Pokedex pokedexEntry = pokedexService.findById(id);
        return ResponseEntity.ok(pokedexEntry);
    }
}
