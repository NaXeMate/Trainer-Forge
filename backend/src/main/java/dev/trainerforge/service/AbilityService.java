package dev.trainerforge.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import dev.trainerforge.exception.notfound.AbilityNotFoundException;
import dev.trainerforge.model.entities.Ability;
import dev.trainerforge.repository.AbilityRepository;
import dev.trainerforge.validator.AbilityValidator;

@Transactional(readOnly = true)
@Service
public class AbilityService {

    private final AbilityRepository abilityRepo;

    public AbilityService(AbilityRepository abilityRepo) {
        this.abilityRepo = abilityRepo;
    }

    public List<Ability> findAll() {
        return abilityRepo.findAll();
    }

    public Ability findById(Long id) {
        return abilityRepo.findById(id)
        .orElseThrow(() -> new AbilityNotFoundException(id));
    }

    public Ability findByName(String name) {
        return abilityRepo.findByName(name)
        .orElseThrow(() -> new AbilityNotFoundException("Ability not found with name: " + name + "."));
    }

    /**
     * Filters abilities by generation after validating the accepted generation interval.
     *
     * @param generationId identifier of the generation used to filter abilities.
     * @return all abilities that belong to the requested generation.
     * @throws IllegalArgumentException when the generation identifier is outside the supported range.
     * @throws AbilityNotFoundException when no abilities exist for the provided generation.
     */
    public List<Ability> findByGenerationId(Long generationId) {
        AbilityValidator.validateGenerationId(generationId);

        List<Ability> result = abilityRepo.findByGenerationId(generationId);
        AbilityValidator.validateByGenerationResult(result, generationId);

        return result;
    }
}
