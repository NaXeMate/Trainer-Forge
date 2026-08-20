package dev.trainerforge.validator;

import java.util.List;

import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;

import dev.trainerforge.dto.response.TeamDto;
import dev.trainerforge.exception.InvalidFilterValueException;
import dev.trainerforge.exception.notfound.TeamNotFoundException;
import dev.trainerforge.model.enumerated.TeamModality;

public final class TeamValidator {

    private TeamValidator() {
    }

    public static void validateTrainerId(Long trainerId) {
        if (trainerId == null) {
            throw new InvalidFilterValueException("The trainer ID cannot be null.");
        }
    }

    public static void validateTrainerExists(boolean exists, Long trainerId) {
        if (!exists) {
            throw new InvalidFilterValueException("No trainer found with id: " + trainerId + ".");
        }
    }

    public static void validateVideogameId(Long videogameId) {
        if (videogameId == null) {
            throw new InvalidFilterValueException("The videogame ID cannot be null.");
        }
    }

    public static void validateVideogameExists(boolean exists, Long videogameId) {
        if (!exists) {
            throw new InvalidFilterValueException("No videogame found with id: " + videogameId + ".");
        }
    }

    public static void validateModality(TeamModality modality) {
        if (modality == null) {
            throw new InvalidFilterValueException("The modality cannot be null.");
        }
    }

    public static void validateByTrainerResult(List<?> result, Long trainerId) {
        validateResult(result, "No teams found for trainer with id: " + trainerId + ".");
    }

    public static void validateByVideogameResult(List<?> result, Long videogameId) {
        validateResult(result, "No teams found for videogame with id: " + videogameId + ".");
    }

    public static void validateByModalityResult(List<?> result, TeamModality modality) {
        validateResult(result, "No teams found for modality: " + modality + ".");
    }

    public static void validateByVisibilityResult(List<?> result, boolean isHidden) {
        String visibility = isHidden ? "hidden" : "public";
        validateResult(result, "No " + visibility + " teams were found.");
    }

    public static void validateOwnership(boolean owned, Long teamId) {
        if (!owned) {
            throw new TeamNotFoundException(teamId);
        }
    }

    public static void validateTrainerAssignment(TeamDto dto, String username) {
        if (dto.trainerUsername() != null && !dto.trainerUsername().equals(username)) {
            throw new InvalidFilterValueException("A team cannot be assigned to another trainer.");
        }
    }

    public static String getAuthenticatedUsername(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()
                || authentication.getName() == null || authentication.getName().isBlank()) {
            throw new AuthenticationCredentialsNotFoundException("Authentication is required.");
        }

        return authentication.getName();
    }

    private static void validateResult(List<?> result, String message) {
        if (result == null || result.isEmpty()) {
            throw new TeamNotFoundException(message);
        }
    }
}
