package dev.trainerforge.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import dev.trainerforge.exception.notfound.MoveNotFoundException;
import dev.trainerforge.exception.notfound.MoveSecondaryEffectNotFoundException;
import dev.trainerforge.exception.notfound.MoveTargetNotFoundException;
import dev.trainerforge.model.entities.Move;
import dev.trainerforge.model.entities.MoveSecondaryEffect;
import dev.trainerforge.model.entities.MoveTarget;
import dev.trainerforge.model.enumerated.MoveClass;
import dev.trainerforge.repository.MoveRepository;
import dev.trainerforge.repository.MoveSecondaryEffectRepository;
import dev.trainerforge.repository.MoveTargetRepository;
import dev.trainerforge.validator.MoveValidator;

@Transactional(readOnly = true)
@Service
public class MoveService {

    private final MoveRepository moveRepo;
    private final MoveTargetRepository moveTargetRepo;
    private final MoveSecondaryEffectRepository moveSecondaryEffectRepo;

    public MoveService(MoveRepository moveRepo, MoveTargetRepository moveTargetRepo, MoveSecondaryEffectRepository moveSecondaryEffectRepo) {
        this.moveRepo = moveRepo;
        this.moveTargetRepo = moveTargetRepo;
        this.moveSecondaryEffectRepo = moveSecondaryEffectRepo;
    }

    public List<Move> findAll() {
        return moveRepo.findAll();
    }

    public Move findById(Long id) {
        return moveRepo.findById(id)
        .orElseThrow(() -> new MoveNotFoundException(id));
    }

    public Move findByName(String name) {
        return moveRepo.findByName(name)
        .orElseThrow(() -> new MoveNotFoundException("Move not found with name: " + name + "."));
    }

    /**
     * Retrieves moves for a pokemon type after validating the type identifier range.
     *
     * @param typeId identifier of the pokemon type used as filter criteria.
     * @return all moves assigned to the requested type.
     * @throws InvalidFilterValueException when the type identifier is outside the supported range.
     * @throws MoveNotFoundException when no moves exist for the provided type.
     */
    public List<Move> findByTypeId(Long typeId) {
        MoveValidator.validateTypeId(typeId);

        List<Move> result = moveRepo.findByTypeId(typeId);
        MoveValidator.validateByTypeResult(result, typeId);

        return result;
    }

    public List<Move> findByMoveClass(MoveClass moveClass) {
        List<Move> result = moveRepo.findByMoveClass(moveClass);
        MoveValidator.validateByMoveClassResult(result, moveClass);

        return result;
    }

    /**
     * Retrieves moves with an exact power value after validating battle data constraints.
     *
     * @param power target power value used to filter moves.
     * @return all moves that match the requested power.
     * @throws InvalidFilterValueException when power is negative or not a multiple of five.
     * @throws MoveNotFoundException when no moves match the provided power value.
     */
    public List<Move> findByPower(int power) {
        MoveValidator.validatePower(power);
        List<Move> result = moveRepo.findByPower(power);
        MoveValidator.validateByPowerResult(result, power);

        return result;
    }

    /**
     * Retrieves moves within a power interval after validating both bounds.
     *
     * @param minPower lower power bound used for filtering.
     * @param maxPower upper power bound used for filtering.
     * @return all moves whose power lies within the requested interval.
     * @throws InvalidFilterValueException when bounds are negative, not multiples of five, or out of order.
     * @throws MoveNotFoundException when no moves are found inside the provided power range.
     */
    public List<Move> findByPowerBetween(int minPower, int maxPower) {
        MoveValidator.validatePowerBetween(minPower, maxPower);

        List<Move> result = moveRepo.findByPowerBetween(minPower, maxPower);
        MoveValidator.validateByPowerRangeResult(result, minPower, maxPower);

        return result;
    }

    public List<Move> findByAccuracy(int accuracy) {
        MoveValidator.validateAccuracy(accuracy);

        List<Move> result = moveRepo.findByAccuracy(accuracy);
        MoveValidator.validateByAccuracyResult(result, accuracy);

        return result;
    }

    /**
     * Retrieves moves within an accuracy interval after validating accuracy-specific constraints.
     *
     * @param minAccuracy lower accuracy bound used for filtering.
     * @param maxAccuracy upper accuracy bound used for filtering.
     * @return all moves whose accuracy falls inside the requested interval.
     * @throws InvalidFilterValueException when bounds are negative, not multiples of five, or out of order.
     * @throws MoveNotFoundException when no moves are found inside the provided accuracy range.
     */
    public List<Move> findByAccuracyBetween(int minAccuracy, int maxAccuracy) {
        MoveValidator.validateAccuracyBetween(minAccuracy, maxAccuracy);

        List<Move> result = moveRepo.findByAccuracyBetween(minAccuracy, maxAccuracy);
        MoveValidator.validateByAccuracyRangeResult(result, minAccuracy, maxAccuracy);

        return result;
    }

    public List<Move> findByContact(boolean contact) {
        List<Move> result = moveRepo.findByContact(contact);
        MoveValidator.validateByContactResult(result, contact);

        return result;
    }

    public List<Move> findByPriority(int priority) {
        List<Move> result = moveRepo.findByPriority(priority);
        MoveValidator.validateByPriorityResult(result, priority);

        return result;
    }

    /**
     * Retrieves moves within a priority interval.
     *
     * @param minPriority lower priority bound used for filtering.
     * @param maxPriority upper priority bound used for filtering.
     * @return all moves whose priority is between the provided bounds.
     * @throws InvalidFilterValueException when the minimum priority is greater than the maximum.
     * @throws MoveNotFoundException when no moves are found in the requested priority range.
     */
    public List<Move> findByPriorityBetween(int minPriority, int maxPriority) {
        MoveValidator.validatePriorityBetween(minPriority, maxPriority);

        List<Move> result = moveRepo.findByPriorityBetween(minPriority, maxPriority);
        MoveValidator.validateByPriorityRangeResult(result, minPriority, maxPriority);

        return result;
    }

    public List<Move> findByTargetId(Long targetId) {
        List<Move> result = moveRepo.findByTargetId(targetId);
        MoveValidator.validateByTargetResult(result, targetId);

        return result;
    }

    public List<Move> findBySecondaryEffectId(Long secondaryEffectId) {
        List<Move> result = moveRepo.findBySecondaryEffectId(secondaryEffectId);
        MoveValidator.validateBySecondaryEffectResult(result, secondaryEffectId);

        return result;
    }

    public List<Move> findByPp(int pp) {
        MoveValidator.validatePp(pp);

        List<Move> result = moveRepo.findByPp(pp);
        MoveValidator.validateByPpResult(result, pp);

        return result;
    }

    /**
     * Retrieves moves within a PP interval after validating allowed PP rules.
     *
     * @param minPp lower PP bound used for filtering.
     * @param maxPp upper PP bound used for filtering.
     * @return all moves whose PP value is between the requested bounds.
     * @throws InvalidFilterValueException when bounds are outside allowed PP values, not multiples of five, or out of order.
     * @throws MoveNotFoundException when no moves are found in the provided PP interval.
     */
    public List<Move> findByPpBetween(int minPp, int maxPp) {
        MoveValidator.validatePpBetween(minPp, maxPp);

        List<Move> result = moveRepo.findByPpBetween(minPp, maxPp);
        MoveValidator.validateByPpRangeResult(result, minPp, maxPp);

        return result;
    }

    /**
     * Retrieves moves introduced in a generation after validating generation bounds.
     *
     * @param generationId identifier of the generation used as filter criteria.
     * @return all moves associated with the requested generation.
     * @throws InvalidFilterValueException when the generation identifier is outside the supported range.
     * @throws MoveNotFoundException when no moves are available for the provided generation.
     */
    public List<Move> findByGenerationId(Long generationId) {
        MoveValidator.validateGenerationId(generationId);

        List<Move> result = moveRepo.findByGenerationId(generationId);
        MoveValidator.validateByGenerationResult(result, generationId);

        return result;
    }

    public boolean existsById(Long id) {
        return moveRepo.existsById(id);
    }

    public List<MoveTarget> getMoveTargets() {
        return moveTargetRepo.findAll();
    }

    public MoveTarget getMoveTargetById(Long id) {
        return moveTargetRepo.findById(id)
        .orElseThrow(() -> new MoveTargetNotFoundException(id));
    }

    public List<MoveSecondaryEffect> getMoveSecondaryEffects() {
        return moveSecondaryEffectRepo.findAll();
    }

    public MoveSecondaryEffect getMoveSecondaryEffectById(Long id) {
        return moveSecondaryEffectRepo.findById(id)
                .orElseThrow(() -> new MoveSecondaryEffectNotFoundException(id));
    }
}
