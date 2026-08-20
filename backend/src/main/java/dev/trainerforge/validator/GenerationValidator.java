package dev.trainerforge.validator;

import dev.trainerforge.exception.notfound.GenerationNotFoundException;

public final class GenerationValidator {

    private GenerationValidator() {
    }

    public static String resolveGenerationName(int number) {
        ValidationLimits.Identifier range = ValidationLimits.Identifier.GENERATION;
        if (number < range.min() || number > range.max()) {
            throw new GenerationNotFoundException("Generation not found with number: " + number + ".");
        }

        return switch (number) {
            case 1 -> "Generation I";
            case 2 -> "Generation II";
            case 3 -> "Generation III";
            case 4 -> "Generation IV";
            case 5 -> "Generation V";
            case 6 -> "Generation VI";
            case 7 -> "Generation VII";
            case 8 -> "Generation VIII";
            case 9 -> "Generation IX";
            case 10 -> "Generation X";
            default -> throw new GenerationNotFoundException("Generation not found with number: " + number + ".");
        };
    }
}
