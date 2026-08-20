package dev.trainerforge.validator;

import java.util.List;

import dev.trainerforge.exception.InvalidFilterValueException;
import dev.trainerforge.exception.notfound.PokemonNotFoundException;
import dev.trainerforge.exception.notfound.PokemonTeamNotFoundException;

public final class PokemonTeamValidator {

    private PokemonTeamValidator() {
    }

    public static long[] parseIdentifiers(String teamId, String pokemonId) {
        try {
            return new long[] { Long.parseLong(teamId), Long.parseLong(pokemonId) };
        } catch (NumberFormatException ex) {
            throw new InvalidFilterValueException("teamId and pokemonId must be numeric.");
        }
    }

    public static void validatePokemonExists(boolean exists, Long pokemonId) {
        if (!exists) {
            throw new PokemonNotFoundException(pokemonId);
        }
    }

    public static void validatePosition(int position) {
        ValidationLimits.IntegerRange range = ValidationLimits.IntegerRange.TEAM_POSITION;
        if (position < range.min() || position > range.max()) {
            throw new InvalidFilterValueException(
                "Position must be between " + range.min() + " and " + range.max() + ".");
        }
    }

    public static void validatePositionAvailable(boolean occupied, int position, Long teamId) {
        if (occupied) {
            String suffix = teamId == null ? " in this team." : " in team with id: " + teamId + ".";
            throw new InvalidFilterValueException("Position " + position + " is already occupied" + suffix);
        }
    }

    public static void validatePokemonNotInTeam(boolean alreadyInTeam, Long pokemonId, Long teamId) {
        if (alreadyInTeam) {
            throw new InvalidFilterValueException(
                "Pokemon with id " + pokemonId + " is already in team with id: " + teamId + ".");
        }
    }

    public static void validateByTeamResult(List<?> result, Long teamId) {
        validateResult(result, "No Pokemon-Team associations found for team with id: " + teamId + ".");
    }

    public static void validateByPokemonResult(List<?> result, Long pokemonId) {
        validateResult(result, "No team-pokemon association found with pokemon id: " + pokemonId + ".");
    }

    public static void validateByPositionResult(List<?> result, int position) {
        validateResult(result, "No team-pokemon association found with position: " + position + ".");
    }

    private static void validateResult(List<?> result, String message) {
        if (result == null || result.isEmpty()) {
            throw new PokemonTeamNotFoundException(message);
        }
    }
}
