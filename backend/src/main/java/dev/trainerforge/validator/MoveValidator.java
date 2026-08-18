package dev.trainerforge.validator;

import java.util.List;

import dev.trainerforge.exception.InvalidFilterValueException;
import dev.trainerforge.exception.notfound.MoveNotFoundException;
import dev.trainerforge.model.enumerated.MoveClass;

public final class MoveValidator {

    private MoveValidator() {
    }

    public static void validateTypeId(Long typeId) {
        ValidationLimits.Identifier range = ValidationLimits.Identifier.TYPE;
        if (typeId == null || typeId < range.min() || typeId > range.max()) {
            throw new InvalidFilterValueException(
                "Type ID must be between " + range.min() + " and " + range.max() + ".");
        }
    }

    public static void validatePower(int power) {
        if (power < ValidationLimits.MIN_POWER) {
            throw new InvalidFilterValueException(
                "Power cannot be negative. If you want to filter by STATUS moves, use 0 as the power value.");
        }
        if (power % ValidationLimits.MULTIPLE_OF_FIVE != 0) {
            throw new InvalidFilterValueException("Power must be a multiple of 5.");
        }
    }

    public static void validatePowerBetween(int minPower, int maxPower) {
        if (minPower < ValidationLimits.MIN_POWER || maxPower < ValidationLimits.MIN_POWER) {
            throw new InvalidFilterValueException(
                "Power values must not be negative. If you want to filter by STATUS moves, use 0 as the power value.");
        }
        if (maxPower % ValidationLimits.MULTIPLE_OF_FIVE != 0
                || minPower % ValidationLimits.MULTIPLE_OF_FIVE != 0) {
            throw new InvalidFilterValueException("Power must be a multiple of 5.");
        }
        if (minPower > maxPower) {
            throw new InvalidFilterValueException(
                "Minimum power (" + minPower + ") cannot be greater than maximum power (" + maxPower + ").");
        }
    }

    public static void validateAccuracy(int accuracy) {
        ValidationLimits.IntegerRange range = ValidationLimits.IntegerRange.ACCURACY;
        if (accuracy < range.min() || accuracy > range.max()) {
            throw new InvalidFilterValueException("Accuracy must be between 0 and 100.");
        }
        if (accuracy % ValidationLimits.MULTIPLE_OF_FIVE != 0) {
            throw new InvalidFilterValueException("Accuracy must be a multiple of 5.");
        }
    }

    public static void validateAccuracyBetween(int minAccuracy, int maxAccuracy) {
        ValidationLimits.IntegerRange range = ValidationLimits.IntegerRange.ACCURACY;

        if (minAccuracy < range.min() || maxAccuracy < range.min()) {
            throw new InvalidFilterValueException(
                "Accuracy values musn't be negative. If you want to filter by moves that never miss, use 0 as the accuracy value.");
        }
        if (minAccuracy > range.max() || maxAccuracy > range.max()) {
            throw new InvalidFilterValueException(
                "Accuracy values must not be greater than " + range.max() + ".");
        }
        if (minAccuracy % ValidationLimits.MULTIPLE_OF_FIVE != 0
                || maxAccuracy % ValidationLimits.MULTIPLE_OF_FIVE != 0) {
            throw new InvalidFilterValueException("Accuracy must be a multiple of 5.");
        }
        if (minAccuracy > maxAccuracy) {
            throw new InvalidFilterValueException(
                "Minimum accuracy (" + minAccuracy + ") cannot be greater than maximum accuracy (" + maxAccuracy + ").");
        }
    }

    public static void validatePriorityBetween(int minPriority, int maxPriority) {
        if (minPriority > maxPriority) {
            throw new InvalidFilterValueException(
                "Minimum priority (" + minPriority + ") cannot be greater than maximum priority (" + maxPriority + ").");
        }
    }

    public static void validatePp(int pp) {
        ValidationLimits.IntegerRange range = ValidationLimits.IntegerRange.PP;
        if (pp < range.min() || pp > range.max()) {
            throw new InvalidFilterValueException("PP must be between 5 and 40.");
        }
        if (pp % ValidationLimits.MULTIPLE_OF_FIVE != 0) {
            throw new InvalidFilterValueException("PP must be a multiple of 5.");
        }
    }

    public static void validatePpBetween(int minPp, int maxPp) {
        ValidationLimits.IntegerRange range = ValidationLimits.IntegerRange.PP;
        if (minPp < range.min() || maxPp > range.max()) {
            throw new InvalidFilterValueException("PP must be between 5 and 40.");
        }
        if (maxPp % ValidationLimits.MULTIPLE_OF_FIVE != 0
                || minPp % ValidationLimits.MULTIPLE_OF_FIVE != 0) {
            throw new InvalidFilterValueException("PP must be a multiple of 5.");
        }
        if (minPp > maxPp) {
            throw new InvalidFilterValueException(
                "Minimum PP (" + minPp + ") cannot be greater than maximum PP (" + maxPp + ").");
        }
    }

    public static void validateGenerationId(Long generationId) {
        ValidationLimits.Identifier range = ValidationLimits.Identifier.GENERATION;
        if (generationId == null || generationId < range.min() || generationId > range.max()) {
            throw new InvalidFilterValueException(
                "Generation ID must be between " + range.min() + " and " + range.max() + ".");
        }
    }

    public static void validateByTypeResult(List<?> result, Long typeId) {
        validateResult(result, "Move not found with type ID: " + typeId + ".");
    }

    public static void validateByMoveClassResult(List<?> result, MoveClass moveClass) {
        validateResult(result, "Move not found with move class: " + moveClass + ".");
    }

    public static void validateByPowerResult(List<?> result, int power) {
        validateResult(result, "Move not found with power: " + power + ".");
    }

    public static void validateByPowerRangeResult(List<?> result, int minPower, int maxPower) {
        validateResult(result, "Move not found with power between: " + minPower + " and " + maxPower + ".");
    }

    public static void validateByAccuracyResult(List<?> result, int accuracy) {
        validateResult(result, "Move not found with accuracy: " + accuracy + ".");
    }

    public static void validateByAccuracyRangeResult(List<?> result, int minAccuracy, int maxAccuracy) {
        validateResult(result, "Move not found with accuracy between: " + minAccuracy + " and " + maxAccuracy + ".");
    }

    public static void validateByContactResult(List<?> result, boolean contact) {
        validateResult(result, "Move not found with contact: " + contact + ".");
    }

    public static void validateByPriorityResult(List<?> result, int priority) {
        validateResult(result, "Move not found with priority: " + priority + ".");
    }

    public static void validateByPriorityRangeResult(List<?> result, int minPriority, int maxPriority) {
        validateResult(result, "Move not found with priority between: " + minPriority + " and " + maxPriority + ".");
    }

    public static void validateByTargetResult(List<?> result, Long targetId) {
        validateResult(result, "Move not found with target ID: " + targetId + ".");
    }

    public static void validateBySecondaryEffectResult(List<?> result, Long secondaryEffectId) {
        validateResult(result, "Move not found with secondary effect ID: " + secondaryEffectId + ".");
    }

    public static void validateByPpResult(List<?> result, int pp) {
        validateResult(result, "Move not found with PP: " + pp + ".");
    }

    public static void validateByPpRangeResult(List<?> result, int minPp, int maxPp) {
        validateResult(result, "Move not found with PP between: " + minPp + " and " + maxPp + ".");
    }

    public static void validateByGenerationResult(List<?> result, Long generationId) {
        validateResult(result, "Move not found with generation ID: " + generationId + ".");
    }

    private static void validateResult(List<?> result, String message) {
        if (result == null || result.isEmpty()) {
            throw new MoveNotFoundException(message);
        }
    }
}
