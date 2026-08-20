package dev.trainerforge.validator;

import java.util.List;

import dev.trainerforge.exception.notfound.VideogamePokedexNotFoundException;

public final class VideogamePokedexValidator {

    private VideogamePokedexValidator() {
    }

    public static void validatePokedexExists(boolean exists, Long pokedexId) {
        if (!exists) {
            throw new VideogamePokedexNotFoundException(
                "No videogame-pokedex associations found for pokedex with id: " + pokedexId + ".");
        }
    }

    public static void validateByPokedexResult(List<?> result, Long pokedexId) {
        validateResult(result, "No videogame-pokedex associations found for pokedex with id: " + pokedexId + ".");
    }

    public static void validateVideogameExists(boolean exists, Long videogameId) {
        if (!exists) {
            throw new VideogamePokedexNotFoundException(
                "No videogame-pokedex associations found for videogame with id: " + videogameId + ".");
        }
    }

    public static void validateByVideogameResult(List<?> result, Long videogameId) {
        validateResult(result, "No videogame-pokedex associations found for videogame with id: " + videogameId + ".");
    }

    private static void validateResult(List<?> result, String message) {
        if (result == null || result.isEmpty()) {
            throw new VideogamePokedexNotFoundException(message);
        }
    }
}
