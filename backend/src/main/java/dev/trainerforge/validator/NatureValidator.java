package dev.trainerforge.validator;

import java.util.List;

import dev.trainerforge.exception.notfound.NatureNotFoundException;
import dev.trainerforge.model.enumerated.NatureRiseLower;

public final class NatureValidator {

    private NatureValidator() {
    }

    public static void validateByRiseResult(List<?> result, NatureRiseLower rise) {
        validateResult(result, "No natures found with rise: " + rise + ".");
    }

    public static void validateByLowerResult(List<?> result, NatureRiseLower lower) {
        validateResult(result, "No natures found with lower: " + lower + ".");
    }

    private static void validateResult(List<?> result, String message) {
        if (result == null || result.isEmpty()) {
            throw new NatureNotFoundException(message);
        }
    }
}
