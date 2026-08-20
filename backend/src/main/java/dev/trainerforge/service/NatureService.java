package dev.trainerforge.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import dev.trainerforge.exception.notfound.NatureNotFoundException;
import dev.trainerforge.model.entities.Nature;
import dev.trainerforge.model.enumerated.NatureRiseLower;
import dev.trainerforge.repository.NatureRepository;
import dev.trainerforge.validator.NatureValidator;

@Transactional(readOnly = true)
@Service
public class NatureService {

    private final NatureRepository natureRepo;

    public NatureService(NatureRepository natureRepo) {
        this.natureRepo = natureRepo;
    }

    public List<Nature> findAll() {
        return natureRepo.findAll();
    }

    public Nature findById(Long id) {
        return natureRepo.findById(id)
        .orElseThrow(() -> new NatureNotFoundException(id));
    }

    public Nature findByName(String name) {
        return natureRepo.findByName(name)
        .orElseThrow(() -> new NatureNotFoundException("Nature not found with name: " + name + "."));
    }

    /**
     * Retrieves natures that increase a specific stat.
     *
     * @param rise stat modifier used as the increasing trait filter.
     * @return all natures that boost the provided stat.
     * @throws NatureNotFoundException when no nature raises the requested stat.
     */
    public List<Nature> findByRise(NatureRiseLower rise) {
        List<Nature> result = natureRepo.findByRise(rise);
        NatureValidator.validateByRiseResult(result, rise);

        return result;
    }

    /**
     * Retrieves natures that lower a specific stat.
     *
     * @param lower stat modifier used as the decreasing trait filter.
     * @return all natures that reduce the provided stat.
     * @throws NatureNotFoundException when no nature lowers the requested stat.
     */
    public List<Nature> findByLower(NatureRiseLower lower) {
        List<Nature> result = natureRepo.findByLower(lower);
        NatureValidator.validateByLowerResult(result, lower);

        return result;
    }
}
