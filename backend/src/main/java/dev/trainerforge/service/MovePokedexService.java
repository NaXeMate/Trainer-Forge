package dev.trainerforge.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import dev.trainerforge.exception.MovePokedexNotFoundException;
import dev.trainerforge.model.entities.MovePokedex;
import dev.trainerforge.model.enumerated.LearningMethod;
import dev.trainerforge.repository.MovePokedexRepository;

@Transactional(readOnly = true)
@Service
public class MovePokedexService {

    private final MovePokedexRepository movePokedexRepo;

    public MovePokedexService(MovePokedexRepository movePokedexRepo) {
        this.movePokedexRepo = movePokedexRepo;
    }

    public List<MovePokedex> findAll() {
        return movePokedexRepo.findAll();
    }

    public MovePokedex findById(Long id) {
        return movePokedexRepo.findById(id)
        .orElseThrow(() -> new MovePokedexNotFoundException(id));
    }

    public List<MovePokedex> findByLearningMethod(LearningMethod learningMethod) {
        List<MovePokedex> result = movePokedexRepo.findByLearningMethod(learningMethod);

        if (result.isEmpty()) {
            throw new MovePokedexNotFoundException("MovePokedex entry not found with learning method: " + learningMethod + ".");
        }
        
        return result;
    }
}
