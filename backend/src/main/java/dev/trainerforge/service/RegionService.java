package dev.trainerforge.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import dev.trainerforge.exception.InvalidFilterValueException;
import dev.trainerforge.exception.RegionNotFoundException;
import dev.trainerforge.model.entities.Region;
import dev.trainerforge.repository.RegionRepository;

@Transactional(readOnly = true)
@Service
public class RegionService {

    private final RegionRepository regionRepo;

    private static final Long GENERATION_MAX_ID = 10L;

    public RegionService(RegionRepository regionRepo) {
        this.regionRepo = regionRepo;
    }

    public List<Region> findAll() {
        return regionRepo.findAll();
    }

    public Region findById(Long id) {
        return regionRepo.findById(id)
        .orElseThrow(() -> new RegionNotFoundException(id));
    }

    public Region findByName(String name) {
        return regionRepo.findByName(name)
        .orElseThrow(() -> new RegionNotFoundException("Region not found with name: " + name + "."));
    }

    public List<Region> findByGenerationId(Long generationId) {
        if (generationId < 1 || generationId > GENERATION_MAX_ID) {
            throw new InvalidFilterValueException("Generation ID must be between 1 and " + GENERATION_MAX_ID + ".");
        }
        
        List<Region> result = regionRepo.findByGenerationId(generationId);

        if (result.isEmpty()) {
            throw new RegionNotFoundException("No regions found for generation ID: " + generationId + ".");
        }

        return result;
    }
}
