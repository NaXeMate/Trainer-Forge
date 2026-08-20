package dev.trainerforge.validator;

import java.util.List;

import dev.trainerforge.exception.notfound.VideogameNotFoundException;

public final class VideogameValidator {

    private VideogameValidator() {
    }

    public static void validateGenerationId(Long generationId) {
        ValidationLimits.Identifier range = ValidationLimits.Identifier.GENERATION;
        if (generationId == null || generationId < range.min() || generationId > range.max()) {
            throw new IllegalArgumentException(
                "Generation ID must be between " + range.min() + " and " + range.max() + ".");
        }
    }

    public static void validateRegionId(Long regionId) {
        ValidationLimits.Identifier range = ValidationLimits.Identifier.REGION;
        if (regionId == null || regionId < range.min() || regionId > range.max()) {
            throw new IllegalArgumentException(
                "Region ID must be between " + range.min() + " and " + range.max() + ".");
        }
    }

    public static void validateByGenerationResult(List<?> result, Long generationId) {
        validateResult(result, "No videogames found for Generation ID: " + generationId + ".");
    }

    public static void validateByRegionResult(List<?> result, Long regionId) {
        validateResult(result, "No videogames found for Region ID: " + regionId + ".");
    }

    public static void validateByPokedexResult(List<?> result, Long videogameId) {
        validateResult(result, "There are no Pokemon in this videogame ID: " + videogameId + ".");
    }

    private static void validateResult(List<?> result, String message) {
        if (result == null || result.isEmpty()) {
            throw new VideogameNotFoundException(message);
        }
    }
}
