package dev.trainerforge.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import dev.trainerforge.exception.notfound.NatureNotFoundException;
import dev.trainerforge.model.entities.Nature;
import dev.trainerforge.model.enumerated.NatureRiseLower;
import dev.trainerforge.repository.NatureRepository;

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

    public List<Nature> findByRise(NatureRiseLower rise) {
        List<Nature> result = natureRepo.findByRise(rise);

        if (result.isEmpty()) {
            throw new NatureNotFoundException("No natures found with rise: " + rise + ".");
        }

        return result;
    }

    public List<Nature> findByLower(NatureRiseLower lower) {
        List<Nature> result = natureRepo.findByLower(lower);

        if (result.isEmpty()) {
            throw new NatureNotFoundException("No natures found with lower: " + lower + ".");
        }

        return result;
    }
}
