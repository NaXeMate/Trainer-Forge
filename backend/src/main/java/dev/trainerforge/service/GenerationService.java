package dev.trainerforge.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import dev.trainerforge.exception.notfound.GenerationNotFoundException;
import dev.trainerforge.model.entities.Generation;
import dev.trainerforge.repository.GenerationRepository;

@Service
@Transactional(readOnly = true)
public class GenerationService {

    private final GenerationRepository generationRepo;

    public GenerationService(GenerationRepository generationRepo) {
        this.generationRepo = generationRepo;
    }

    public List<Generation> findAll() {
        return generationRepo.findAll();
    }

    public Generation findById(Long id) {
        return generationRepo.findById(id)
        .orElseThrow(() -> new GenerationNotFoundException(id));
    }

    /**
     * Resolves a generation entity from its numeric index.
     *
     * @param number numeric generation index expected in the range supported by the switch mapping.
     * @return the generation entity associated with the resolved generation label.
     * @throws GenerationNotFoundException when the numeric index is unsupported or no matching generation exists in storage.
     */
    public Generation findByName(int number) {
        String generation = switch (number) {
            case 1 -> "Generation I";
            case 2 -> "Generation II";
            case 3 -> "Generation III";
            case 4 -> "Generation IV";
            case 5 -> "Generation V";
            case 6 -> "Generation VI";
            case 7 -> "Generation VII";
            case 8 -> "Generation VIII";
            case 9 -> "Generation IX";
            case 10 -> "Generation X";
            default -> throw new GenerationNotFoundException("Generation not found with number: " + number + ".");
        };
        
        return generationRepo.findByName(generation)
        .orElseThrow(() -> new GenerationNotFoundException("Generation not found with name: " + generation + "."));
    }
}
