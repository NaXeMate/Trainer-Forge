package dev.trainerforge.validator;

import java.util.List;

import dev.trainerforge.exception.notfound.AchievementNotFoundException;

public final class AchievementValidator {

    private AchievementValidator() {
    }

    public static void validateVisibleResult(List<?> result) {
        if (result == null || result.isEmpty()) {
            throw new AchievementNotFoundException("No visible achievements found.");
        }
    }
}
