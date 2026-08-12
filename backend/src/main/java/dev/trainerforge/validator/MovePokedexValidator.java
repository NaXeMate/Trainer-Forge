package dev.trainerforge.validator;

import java.util.List;

import dev.trainerforge.exception.notfound.MovePokedexNotFoundException;
import dev.trainerforge.model.enumerated.LearningMethod;

public final class MovePokedexValidator {

    private MovePokedexValidator() {
    }

    public static void validateMoveExists(boolean exists, Long moveId) {
        if (!exists) {
            throw new MovePokedexNotFoundException(
                "MovePokedex entry not found with move id: " + moveId + ".");
        }
    }

    public static void validateByMoveResult(List<?> result, Long moveId) {
        validateResult(result, "MovePokedex entry not found with move id: " + moveId + ".");
    }

    public static void validatePokedexExists(boolean exists, Long pokedexId) {
        if (!exists) {
            throw new MovePokedexNotFoundException(
                "MovePokedex entry not found with pokedex id: " + pokedexId + ".");
        }
    }

    public static void validateByPokedexResult(List<?> result, Long pokedexId) {
        validateResult(result, "MovePokedex entry not found with pokedex id: " + pokedexId + ".");
    }

    public static void validateByLearningMethodResult(List<?> result, LearningMethod learningMethod) {
        validateResult(result, "MovePokedex entry not found with learning method: " + learningMethod + ".");
    }

    private static void validateResult(List<?> result, String message) {
        if (result == null || result.isEmpty()) {
            throw new MovePokedexNotFoundException(message);
        }
    }
}
