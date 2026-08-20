package dev.trainerforge.validator;

import java.util.List;

import dev.trainerforge.exception.notfound.PokemonTypeNotFoundException;

public final class PokemonTypeValidator {

    private PokemonTypeValidator() {
    }

    public static void validateName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Name cannot be null or blank.");
        }
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
            throw new PokemonTypeNotFoundException(
                "No PokemonTypes found for generation with id: " + generationId + ".");
        }
    }
}
