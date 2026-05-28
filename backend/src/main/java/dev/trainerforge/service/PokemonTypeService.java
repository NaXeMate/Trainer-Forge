package dev.trainerforge.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import dev.trainerforge.exception.PokemonTypeNotFoundException;
import dev.trainerforge.model.entities.PokemonType;
import dev.trainerforge.repository.PokemonTypeRepository;

@Transactional(readOnly = true)
@Service
public class PokemonTypeService {

    private final PokemonTypeRepository pokemonTypeRepo;

    private static final Long GENERATION_MAX_ID = 10L;

    public PokemonTypeService(PokemonTypeRepository pokemonTypeRepo) {
        this.pokemonTypeRepo = pokemonTypeRepo;
    }

    public List<PokemonType> findAll() {
        return pokemonTypeRepo.findAll();
    }

    public PokemonType findById(Long id) {
        return pokemonTypeRepo.findById(id)
            .orElseThrow(() -> new PokemonTypeNotFoundException(id));
    }

    public PokemonType findByName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Name cannot be null or blank.");
        }

        return pokemonTypeRepo.findByName(name)
            .orElseThrow(() -> new PokemonTypeNotFoundException("No PokemonType found with name: " + name + "."));
    }

    public List<PokemonType> findByGenerationId(Long generationId) {
        if (generationId == null || generationId < 1 || generationId > GENERATION_MAX_ID) {
            throw new IllegalArgumentException("Generation ID must be between 1 and " + GENERATION_MAX_ID + ".");
        }

        List<PokemonType> result = pokemonTypeRepo.findByGenerationId(generationId);
        
        if (result.isEmpty()) {
            throw new PokemonTypeNotFoundException("No PokemonTypes found for generation with id: " + generationId + ".");
        }

        return result;
    }
}
