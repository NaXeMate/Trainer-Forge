package dev.trainerforge.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import dev.trainerforge.exception.notfound.AbilityNotFoundException;
import dev.trainerforge.model.entities.Ability;
import dev.trainerforge.repository.AbilityRepository;

@Transactional(readOnly = true)
@Service
public class AbilityService {

    private final AbilityRepository abilityRepo;

    private static final Long GENERATION_MAX_ID = 10L;

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

    public List<Ability> findByGenerationId(Long generationId) {
        if (generationId < 1 || generationId > GENERATION_MAX_ID) {
            throw new IllegalArgumentException("Generation ID must be between 1 and " + GENERATION_MAX_ID + ".");
        }

        List<Ability> result = abilityRepo.findByGenerationId(generationId);
        
        if (result.isEmpty()) {
            throw new AbilityNotFoundException("No abilities found for Generation ID: " + generationId + ".");
        }
        
        return result;
    }
}
