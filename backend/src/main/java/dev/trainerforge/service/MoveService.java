package dev.trainerforge.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import dev.trainerforge.exception.InvalidFilterValueException;
import dev.trainerforge.exception.MoveNotFoundException;
import dev.trainerforge.exception.MoveSecondaryEffectNotFoundException;
import dev.trainerforge.exception.MoveTargetNotFoundException;
import dev.trainerforge.model.entities.Move;
import dev.trainerforge.model.entities.MoveSecondaryEffect;
import dev.trainerforge.model.entities.MoveTarget;
import dev.trainerforge.model.enumerated.MoveClass;
import dev.trainerforge.repository.MoveRepository;
import dev.trainerforge.repository.MoveSecondaryEffectRepository;
import dev.trainerforge.repository.MoveTargetRepository;

@Transactional(readOnly = true)
@Service
public class MoveService {

    private static final Long MAX_TYPE_ID = 18L;

    private static final Long GENERATION_MAX_ID = 10L;

    private static final int MAX_PP = 40;
    private static final int MIN_PP = 5;

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

    public List<Move> findByTypeId(Long typeId) {
        
        if (typeId < 1 || typeId > MAX_TYPE_ID) {
            throw new InvalidFilterValueException("Type ID must be between 1 and " + MAX_TYPE_ID + ".");
        }

        List<Move> result = moveRepo.findByTypeId(typeId);

        if (result.isEmpty()) {
            throw new MoveNotFoundException("Move not found with type ID: " + typeId + ".");
        }
        
        return result;
    }

    public List<Move> findByMoveClass(MoveClass moveClass) {
        List<Move> result = moveRepo.findByMoveClass(moveClass);

        if (result.isEmpty()) {
            throw new MoveNotFoundException("Move not found with move class: " + moveClass + ".");
        }
        
        return result;
    }

    public List<Move> findByPower(int power) {
        if (power < 0) {
            throw new InvalidFilterValueException("Power must be a negative value. If you want to filter by STATUS moves, use 0 as the power value.");
        }
        
        if (power % 5 != 0) {
            throw new InvalidFilterValueException("Accuracy must be a multiple of 5.");
        }

        List<Move> result = moveRepo.findByPower(power);

        if (result.isEmpty()) {
            throw new MoveNotFoundException("Move not found with power: " + power + ".");
        }
        
        return result;
    }

    public List<Move> findByPowerBetween(int minPower, int maxPower) {
        if (minPower < 0 || maxPower < 0) {
            throw new InvalidFilterValueException("Power values musn't be negative. If you want to filter by STATUS moves, use 0 as the power value.");
        }

        if (maxPower % 5 != 0 || minPower % 5 != 0) {
            throw new InvalidFilterValueException("Accuracy must be a multiple of 5.");
        }

        if (minPower > maxPower) {
            throw new InvalidFilterValueException("Minimum power (" + minPower + ") cannot be greater than maximum power (" + maxPower + ").");
        }
        
        List<Move> result = moveRepo.findByPowerBetween(minPower, maxPower);

        if (result.isEmpty()) {
            throw new MoveNotFoundException("Move not found with power between: " + minPower + " and " + maxPower + ".");
        }
        
        return result;
    }

    public List<Move> findByAccuracy(int accuracy) {
        if (accuracy < 0 || accuracy > 100) {
            throw new InvalidFilterValueException("Accuracy must be between 0 and 100.");
        } else if (accuracy % 5 != 0) {
            throw new InvalidFilterValueException("Accuracy must be a multiple of 5.");
        }
        
        List<Move> result = moveRepo.findByAccuracy(accuracy);

        if (result.isEmpty()) {
            throw new MoveNotFoundException("Move not found with accuracy: " + accuracy + ".");
        }
        
        return result;
    }

    public List<Move> findByAccuracyBetween(int minAccuracy, int maxAccuracy) {
        if (minAccuracy < 0 || maxAccuracy < 0) {
            throw new InvalidFilterValueException("Accuracy values musn't be negative. If you want to filter by moves that never miss, use 0 as the accuracy value.");
        } else if (maxAccuracy % 5 != 0 || minAccuracy % 5 != 0) {
            throw new InvalidFilterValueException("Accuracy must be a multiple of 5.");
        } else if (minAccuracy > maxAccuracy) {
            throw new InvalidFilterValueException("Minimum accuracy (" + minAccuracy + ") cannot be greater than maximum accuracy (" + maxAccuracy + ").");
        }
        
        List<Move> result = moveRepo.findByAccuracyBetween(minAccuracy, maxAccuracy);

        if (result.isEmpty()) {
            throw new MoveNotFoundException("Move not found with accuracy between: " + minAccuracy + " and " + maxAccuracy + ".");
        }
        
        return result;
    }

    public List<Move> findByContact(boolean contact) {
        List<Move> result = moveRepo.findByContact(contact);

        if (result.isEmpty()) {
            throw new MoveNotFoundException("Move not found with contact: " + contact + ".");
        }
        
        return result;
    }

    public List<Move> findByPriority(int priority) {
        List<Move> result = moveRepo.findByPriority(priority);

        if (result.isEmpty()) {
            throw new MoveNotFoundException("Move not found with priority: " + priority + ".");
        }
        
        return result;
    }

    public List<Move> findByPriorityBetween(int minPriority, int maxPriority) {
        if (minPriority > maxPriority) {
            throw new InvalidFilterValueException("Minimum priority (" + minPriority + ") cannot be greater than maximum priority (" + maxPriority + ").");
        }
        
        List<Move> result = moveRepo.findByPriorityBetween(minPriority, maxPriority);

        if (result.isEmpty()) {
            throw new MoveNotFoundException("Move not found with priority between: " + minPriority + " and " + maxPriority + ".");
        }
        
        return result;
    }

    public List<Move> findByTargetId(Long targetId) {
        List<Move> result = moveRepo.findByTargetId(targetId);

        if (result.isEmpty()) {
            throw new MoveNotFoundException("Move not found with target ID: " + targetId + ".");
        }
        
        return result;
    }

    public List<Move> findBySecondaryEffectId(Long secondaryEffectId) {
        List<Move> result = moveRepo.findBySecondaryEffectId(secondaryEffectId);

        if (result.isEmpty()) {
            throw new MoveNotFoundException("Move not found with secondary effect ID: " + secondaryEffectId + ".");
        }
        
        return result;
    }

    public List<Move> findByPp(int pp) {
        if (pp < MIN_PP || pp > MAX_PP) {
            throw new InvalidFilterValueException("PP must be between " + MIN_PP + " and " + MAX_PP + ".");
        } else if (pp % 5 != 0) {
            throw new InvalidFilterValueException("PP must be a multiple of 5.");
        }
        
        List<Move> result = moveRepo.findByPp(pp);

        if (result.isEmpty()) {
            throw new MoveNotFoundException("Move not found with PP: " + pp + ".");
        }
        
        return result;
    }

    public List<Move> findByPpBetween(int minPp, int maxPp) {
        if (minPp < MIN_PP || maxPp > MAX_PP) {
            throw new InvalidFilterValueException("PP must be between " + MIN_PP + " and " + MAX_PP + ".");
        } else if (maxPp % 5 != 0 || minPp % 5 != 0) {
            throw new InvalidFilterValueException("PP must be a multiple of 5.");
        } else if (minPp > maxPp) {
            throw new InvalidFilterValueException("Minimum PP (" + minPp + ") cannot be greater than maximum PP (" + maxPp + ").");
        }
        
        List<Move> result = moveRepo.findByPpBetween(minPp, maxPp);

        if (result.isEmpty()) {
            throw new MoveNotFoundException("Move not found with PP between: " + minPp + " and " + maxPp + ".");
        }
        
        return result;
    }

    public List<Move> findByGenerationId(Long generationId) {
        if (generationId < 1 || generationId > GENERATION_MAX_ID) {
            throw new InvalidFilterValueException("Generation ID must be between 1 and " + GENERATION_MAX_ID + ".");
        }

        List<Move> result = moveRepo.findByGenerationId(generationId);

        if (result.isEmpty()) {
            throw new MoveNotFoundException("Move not found with generation ID: " + generationId + ".");
        }
        
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
