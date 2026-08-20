package dev.trainerforge.validator;

import java.util.List;

import dev.trainerforge.exception.InvalidFilterValueException;
import dev.trainerforge.exception.notfound.ItemNotFoundException;
import dev.trainerforge.model.enumerated.ItemType;

public final class PokemonItemValidator {

    private PokemonItemValidator() {
    }

    public static void validateGenerationId(Long generationId) {
        ValidationLimits.Identifier range = ValidationLimits.Identifier.GENERATION;
        if (generationId == null || generationId < range.min() || generationId > range.max()) {
            throw new InvalidFilterValueException(
                "Generation ID must be between " + range.min() + " and " + range.max() + ".");
        }
    }

    public static void validateByTypeResult(List<?> result, ItemType type) {
        validateResult(result, "No items found with type: " + type + ".");
    }

    public static void validateByGenerationResult(List<?> result, Long generationId) {
        validateResult(result, "No items found with generation ID: " + generationId + ".");
    }

    private static void validateResult(List<?> result, String message) {
        if (result == null || result.isEmpty()) {
            throw new ItemNotFoundException(message);
        }
    }
}
