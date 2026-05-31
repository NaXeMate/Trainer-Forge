package dev.trainerforge.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import dev.trainerforge.exception.InvalidFilterValueException;
import dev.trainerforge.exception.notfound.ItemNotFoundException;
import dev.trainerforge.model.entities.PokemonItem;
import dev.trainerforge.model.enumerated.ItemType;
import dev.trainerforge.repository.PokemonItemRepository;

@Transactional(readOnly = true)
@Service
public class PokemonItemService {

    private final PokemonItemRepository pokemonItemRepo;

    private static final Long GENERATION_MAX_ID = 10L;

    public PokemonItemService(PokemonItemRepository pokemonItemRepo) {
        this.pokemonItemRepo = pokemonItemRepo;
    }

    public List<PokemonItem> findAll() {
        return pokemonItemRepo.findAll();
    }

    public PokemonItem findById(Long id) {
        return pokemonItemRepo.findById(id)
        .orElseThrow(() -> new ItemNotFoundException(id));
    }

    public PokemonItem findByName(String name) {
        return pokemonItemRepo.findByName(name)
        .orElseThrow(() -> new ItemNotFoundException("Item not found with name: " + name + "."));
    }

    public List<PokemonItem> findByType(ItemType type) {
        List<PokemonItem> result = pokemonItemRepo.findByType(type);

        if (result.isEmpty()) {
            throw new ItemNotFoundException("No items found with type: " + type + ".");
        }

        return result;
    }

    /**
     * Retrieves items introduced in a generation after validating generation bounds.
     *
     * @param generationId identifier of the generation used as filter criteria.
     * @return all items associated with the requested generation.
     * @throws InvalidFilterValueException when the generation identifier is outside the supported range.
     * @throws ItemNotFoundException when no items are registered for the provided generation.
     */
    public List<PokemonItem> findByGenerationId(Long generationId) {
        if (generationId < 1 || generationId > GENERATION_MAX_ID) {
            throw new InvalidFilterValueException("Generation ID must be between 1 and " + GENERATION_MAX_ID + ".");
        }

        List<PokemonItem> result = pokemonItemRepo.findByGenerationId(generationId);

        if (result.isEmpty()) {
            throw new ItemNotFoundException("No items found with generation ID: " + generationId + ".");
        }
        
        return result;
    }
}
