package dev.trainerforge.validator;

import java.util.List;
import java.util.regex.Pattern;

import dev.trainerforge.exception.InvalidFilterValueException;
import dev.trainerforge.exception.notfound.TrainerNotFoundException;
import dev.trainerforge.model.enumerated.TrainerClass;

public final class TrainerValidator {

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    private static final Pattern FRIEND_CODE_PATTERN = Pattern.compile("^TF-\\d{4}-\\d{4}$");

    private TrainerValidator() {
    }

    public static void validateUsername(String username) {
        if (username == null || username.isBlank()) {
            throw new InvalidFilterValueException("The username cannot be empty.");
        }
        if (username.length() > ValidationLimits.Length.USERNAME.max()) {
            throw new InvalidFilterValueException("The username exceeds the maximum allowed length.");
        }
    }

    public static void validateEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new InvalidFilterValueException("The email cannot be empty.");
        }
        if (email.length() > ValidationLimits.Length.EMAIL.max()) {
            throw new InvalidFilterValueException("The email exceeds the maximum allowed length.");
        }
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            throw new InvalidFilterValueException("The email does not have a valid format.");
        }
    }

    public static void validateRealName(String realName) {
        if (realName != null && realName.length() > ValidationLimits.Length.REAL_NAME.max()) {
            throw new InvalidFilterValueException("The real name exceeds the maximum allowed length.");
        }
    }

    public static void validatePassword(String password) {
        if (password == null || password.isBlank()) {
            throw new InvalidFilterValueException("The password cannot be empty.");
        }
    }

    public static void validateUsernameUniqueness(boolean alreadyExists) {
        if (alreadyExists) {
            throw new InvalidFilterValueException("A trainer with that username already exists.");
        }
    }

    public static void validateEmailUniqueness(boolean alreadyExists) {
        if (alreadyExists) {
            throw new InvalidFilterValueException("A trainer with that email already exists.");
        }
    }

    public static void validateFriendCode(String friendCode) {
        if (friendCode == null || friendCode.isBlank()) {
            throw new InvalidFilterValueException("The friend code cannot be empty.");
        }
        if (!FRIEND_CODE_PATTERN.matcher(friendCode).matches()) {
            throw new InvalidFilterValueException(
                "The friend code does not have a valid format. It should be in the format 'TF-XXXX-XXXX'.");
        }
    }

    public static void validateRegionId(Long regionId) {
        ValidationLimits.Identifier range = ValidationLimits.Identifier.REGION;
        if (regionId == null || regionId < range.min() || regionId > range.max()) {
            throw new InvalidFilterValueException(
                "Region ID must be between " + range.min() + " and " + range.max() + ".");
        }
    }

    public static void validateTrainerExists(boolean exists, Long trainerId) {
        if (!exists) {
            throw new TrainerNotFoundException(trainerId);
        }
    }

    public static void validateByRegionResult(List<?> result, Long regionId) {
        validateResult(result, "No trainers found in region with id: " + regionId + ".");
    }

    public static void validateByFavoriteGameResult(List<?> result, Long favoriteGameId) {
        validateResult(result, "No trainers found with favorite game id: " + favoriteGameId + ".");
    }

    public static void validateByFavoritePokemonResult(List<?> result, Long favoritePokemonId) {
        validateResult(result, "No trainers found with favorite Pokemon id: " + favoritePokemonId + ".");
    }

    public static void validateByBestFriendResult(List<?> result, Long bestFriendId) {
        validateResult(result, "No trainers found with best friend id: " + bestFriendId + ".");
    }

    public static void validateByTrainerClassResult(List<?> result, TrainerClass trainerClass) {
        validateResult(result, "No trainers found with trainer class: " + trainerClass + ".");
    }

    public static void validateByTrainerGamesResult(List<?> result, Long trainerId) {
        validateResult(result, "No game possessions found for trainer with id: " + trainerId + ".");
    }

    public static void validateByVideogameResult(List<?> result, Long videogameId) {
        validateResult(result, "No trainers found with videogame id: " + videogameId + ".");
    }

    private static void validateResult(List<?> result, String message) {
        if (result == null || result.isEmpty()) {
            throw new TrainerNotFoundException(message);
        }
    }
}
