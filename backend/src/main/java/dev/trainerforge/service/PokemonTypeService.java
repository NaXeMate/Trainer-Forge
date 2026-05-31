package dev.trainerforge.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import dev.trainerforge.exception.notfound.PokemonTypeNotFoundException;
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

    /**
     * Resolves a pokemon type by its canonical name after validating the input text.
     *
     * @param name pokemon type name received from the caller.
     * @return the pokemon type that matches the provided name.
     * @throws IllegalArgumentException when the type name is null or blank.
     * @throws PokemonTypeNotFoundException when no pokemon type matches the provided name.
     */
    public PokemonType findByName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Name cannot be null or blank.");
        }

        return pokemonTypeRepo.findByName(name)
            .orElseThrow(() -> new PokemonTypeNotFoundException("No PokemonType found with name: " + name + "."));
    }

    /**
     * Retrieves pokemon types introduced in a specific generation.
     *
     * @param generationId identifier of the generation used for filtering.
     * @return all pokemon types associated with the requested generation.
     * @throws IllegalArgumentException when the generation identifier is null or outside the supported range.
     * @throws PokemonTypeNotFoundException when no pokemon type is linked to the provided generation.
     */
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
