package dev.trainerforge.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import dev.trainerforge.exception.notfound.RegionNotFoundException;
import dev.trainerforge.model.entities.Region;
import dev.trainerforge.repository.RegionRepository;
import dev.trainerforge.validator.RegionValidator;

@Transactional(readOnly = true)
@Service
public class RegionService {

    private final RegionRepository regionRepo;

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

    /**
     * Retrieves regions introduced in a generation after validating generation boundaries.
     *
     * @param generationId identifier of the generation used to filter regions.
     * @return all regions introduced in the requested generation.
     * @throws InvalidFilterValueException when the generation identifier is outside the supported range.
     * @throws RegionNotFoundException when no regions are associated with the provided generation.
     */
    public List<Region> findByGenerationId(Long generationId) {
        RegionValidator.validateGenerationId(generationId);

        List<Region> result = regionRepo.findByGenerationId(generationId);
        RegionValidator.validateByGenerationResult(result, generationId);

        return result;
    }
}
