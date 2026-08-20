package dev.trainerforge.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import dev.trainerforge.exception.notfound.MovePokedexNotFoundException;
import dev.trainerforge.model.entities.MovePokedex;
import dev.trainerforge.model.enumerated.LearningMethod;
import dev.trainerforge.repository.MovePokedexRepository;
import dev.trainerforge.validator.MovePokedexValidator;

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

    /**
     * Retrieves move-learning entries for a move after checking that the move exists.
     *
     * @param moveId identifier of the move used to filter learning entries.
     * @return all move-pokedex entries that reference the provided move.
     * @throws MovePokedexNotFoundException when the move does not exist or has no learning entries.
     */
    public List<MovePokedex> findByMoveId(Long moveId) {
        MovePokedexValidator.validateMoveExists(moveService.existsById(moveId), moveId);

        List<MovePokedex> result = movePokedexRepo.findByMoveId(moveId);
        MovePokedexValidator.validateByMoveResult(result, moveId);

        return result;
    }

    /**
     * Retrieves move-learning entries for a species after checking that the species exists.
     *
     * @param pokedexId identifier of the species used to filter learning entries.
     * @return all move-pokedex entries that reference the provided species.
     * @throws MovePokedexNotFoundException when the species does not exist or has no learning entries.
     */
    public List<MovePokedex> findByPokedexId(Long pokedexId) {
        MovePokedexValidator.validatePokedexExists(pokedexService.existsById(pokedexId), pokedexId);

        List<MovePokedex> result = movePokedexRepo.findByPokedexId(pokedexId);
        MovePokedexValidator.validateByPokedexResult(result, pokedexId);

        return result;
    }

    /**
     * Retrieves move-learning entries by learning method.
     *
     * @param learningMethod learning channel used to filter entries, such as level-up or TM.
     * @return all move-pokedex entries that use the requested learning method.
     * @throws MovePokedexNotFoundException when no entries match the provided learning method.
     */
    public List<MovePokedex> findByLearningMethod(LearningMethod learningMethod) {
        List<MovePokedex> result = movePokedexRepo.findByLearningMethod(learningMethod);
        MovePokedexValidator.validateByLearningMethodResult(result, learningMethod);

        return result;
    }
}
