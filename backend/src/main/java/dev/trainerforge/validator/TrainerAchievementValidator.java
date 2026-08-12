package dev.trainerforge.validator;

import java.time.LocalDateTime;
import java.util.List;

import dev.trainerforge.exception.InvalidFilterValueException;
import dev.trainerforge.exception.notfound.AchievementNotFoundException;
import dev.trainerforge.exception.notfound.TrainerAchievementNotFoundException;
import dev.trainerforge.exception.notfound.TrainerNotFoundException;

public final class TrainerAchievementValidator {

    private TrainerAchievementValidator() {
    }

    public static Long parseAchievementId(String achievement) {
        try {
            return Long.valueOf(achievement);
        } catch (NumberFormatException ex) {
            throw new InvalidFilterValueException("Achievement id must be a number.");
        }
    }

    public static void validateAchievementExists(boolean exists, Long achievementId) {
        if (!exists) {
            throw new AchievementNotFoundException(achievementId);
        }
    }

    public static void validateNotAlreadyUnlocked(boolean alreadyUnlocked, Long trainerId, Long achievementId) {
        if (alreadyUnlocked) {
            throw new InvalidFilterValueException(
                "Trainer with id: " + trainerId + " has already unlocked achievement with id: " + achievementId + ".");
        }
    }

    public static void validateTrainerExists(boolean exists, Long trainerId) {
        if (!exists) {
            throw new TrainerNotFoundException(trainerId);
        }
    }

    public static void validateDate(LocalDateTime dateObtained) {
        if (dateObtained == null) {
            throw new InvalidFilterValueException("Date obtained cannot be null.");
        }
    }

    public static void validateDateBetween(LocalDateTime startDate, LocalDateTime endDate) {
        if (startDate == null || endDate == null) {
            throw new InvalidFilterValueException("Start date and end date cannot be null.");
        }
        if (startDate.isAfter(endDate)) {
            throw new InvalidFilterValueException("Start date cannot be after end date.");
        }
    }

    public static void validateByTrainerResult(List<?> result, Long trainerId) {
        validateResult(result, "No achievements found for trainer with id: " + trainerId + ".");
    }

    public static void validateByAchievementResult(List<?> result, Long achievementId) {
        validateResult(result, "No trainers found with achievement id: " + achievementId + ".");
    }

    public static void validateByDateResult(List<?> result, LocalDateTime dateObtained) {
        validateResult(result, "No achievements found obtained on: " + dateObtained + ".");
    }

    public static void validateByDateRangeResult(List<?> result, LocalDateTime startDate, LocalDateTime endDate) {
        validateResult(result, "No achievements found obtained between: " + startDate + " and " + endDate + ".");
    }

    private static void validateResult(List<?> result, String message) {
        if (result == null || result.isEmpty()) {
            throw new TrainerAchievementNotFoundException(message);
        }
    }
}
