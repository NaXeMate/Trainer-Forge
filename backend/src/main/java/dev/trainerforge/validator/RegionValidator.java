package dev.trainerforge.validator;

import java.util.List;

import dev.trainerforge.exception.InvalidFilterValueException;
import dev.trainerforge.exception.notfound.RegionNotFoundException;

public final class RegionValidator {

    private RegionValidator() {
    }

    public static void validateGenerationId(Long generationId) {
        ValidationLimits.Identifier range = ValidationLimits.Identifier.GENERATION;
        if (generationId == null || generationId < range.min() || generationId > range.max()) {
            throw new InvalidFilterValueException(
                "Generation ID must be between " + range.min() + " and " + range.max() + ".");
        }
    }

    public static void validateByGenerationResult(List<?> result, Long generationId) {
        if (result == null || result.isEmpty()) {
            throw new RegionNotFoundException("No regions found for generation ID: " + generationId + ".");
        }
    }
}
