package dev.trainerforge.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import dev.trainerforge.exception.notfound.MovePokedexNotFoundException;
import dev.trainerforge.model.entities.MovePokedex;
import dev.trainerforge.model.enumerated.LearningMethod;
import dev.trainerforge.repository.MovePokedexRepository;

@Transactional(readOnly = true)
@Service
public class MovePokedexService {

    private final MovePokedexRepository movePokedexRepo;
    private final MoveService moveService;
    private final PokedexService pokedexService;

    public MovePokedexService(MovePokedexRepository movePokedexRepo, MoveService moveService, PokedexService pokedexService) {
        this.movePokedexRepo = movePokedexRepo;
        this.moveService = moveService;
        this.pokedexService = pokedexService;
    }

    public List<MovePokedex> findAll() {
        return movePokedexRepo.findAll();
    }

    public MovePokedex findById(Long id) {
        return movePokedexRepo.findById(id)
        .orElseThrow(() -> new MovePokedexNotFoundException(id));
    }

    public List<MovePokedex> findByMoveId(Long moveId) {
        if (!moveService.existsById(moveId)) {
            throw new MovePokedexNotFoundException("MovePokedex entry not found with move id: " + moveId + ".");
        }
        List<MovePokedex> result = movePokedexRepo.findByMoveId(moveId);

        if (result.isEmpty()) {
            throw new MovePokedexNotFoundException("MovePokedex entry not found with move id: " + moveId + ".");
        }
        
        return result;
    }

    public List<MovePokedex> findByPokedexId(Long pokedexId) {
        if (!pokedexService.existsById(pokedexId)) {
            throw new MovePokedexNotFoundException("MovePokedex entry not found with pokedex id: " + pokedexId + ".");
        }
        List<MovePokedex> result = movePokedexRepo.findByPokedexId(pokedexId);

        if (result.isEmpty()) {
            throw new MovePokedexNotFoundException("MovePokedex entry not found with pokedex id: " + pokedexId + ".");
        }
        
        return result;
    }

    public List<MovePokedex> findByLearningMethod(LearningMethod learningMethod) {
        List<MovePokedex> result = movePokedexRepo.findByLearningMethod(learningMethod);

        if (result.isEmpty()) {
            throw new MovePokedexNotFoundException("MovePokedex entry not found with learning method: " + learningMethod + ".");
        }
        
        return result;
    }
}
