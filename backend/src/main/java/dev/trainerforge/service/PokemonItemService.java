package dev.trainerforge.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import dev.trainerforge.exception.notfound.ItemNotFoundException;
import dev.trainerforge.model.entities.PokemonItem;
import dev.trainerforge.model.enumerated.ItemType;
import dev.trainerforge.repository.PokemonItemRepository;
import dev.trainerforge.validator.PokemonItemValidator;

@Transactional(readOnly = true)
@Service
public class PokemonItemService {

    private final PokemonItemRepository pokemonItemRepo;

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
        PokemonItemValidator.validateByTypeResult(result, type);

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
        PokemonItemValidator.validateGenerationId(generationId);

        List<PokemonItem> result = pokemonItemRepo.findByGenerationId(generationId);
        PokemonItemValidator.validateByGenerationResult(result, generationId);

        return result;
    }
}
