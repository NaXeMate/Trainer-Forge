package dev.trainerforge.validator;

import java.util.List;

import dev.trainerforge.exception.notfound.AbilityNotFoundException;

public final class AbilityValidator {

    private AbilityValidator() {
    }

    public static void validateGenerationId(Long generationId) {
        ValidationLimits.Identifier range = ValidationLimits.Identifier.GENERATION;
        if (generationId == null || generationId < range.min() || generationId > range.max()) {
            throw new IllegalArgumentException(
                "Generation ID must be between " + range.min() + " and " + range.max() + ".");
        }
    }

    public static void validateByGenerationResult(List<?> result, Long generationId) {
        if (result == null || result.isEmpty()) {
            throw new AbilityNotFoundException("No abilities found for Generation ID: " + generationId + ".");
        }
    }
}
