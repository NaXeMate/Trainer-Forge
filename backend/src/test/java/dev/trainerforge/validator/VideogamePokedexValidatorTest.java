package dev.trainerforge.validator;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import dev.trainerforge.exception.notfound.VideogamePokedexNotFoundException;

class VideogamePokedexValidatorTest {

    @Test
    void shouldAcceptExistingPokedexAndVideogame() {
        assertAll(
            () -> assertDoesNotThrow(() -> VideogamePokedexValidator.validatePokedexExists(true, 25L)),
            () -> assertDoesNotThrow(() -> VideogamePokedexValidator.validateVideogameExists(true, 9L))
        );
    }

    @Test
    void shouldRejectMissingPokedexAndVideogame() {
        assertAll(
            () -> assertNotFound(
                "No videogame-pokedex associations found for pokedex with id: 25.",
                () -> VideogamePokedexValidator.validatePokedexExists(false, 25L)
            ),
            () -> assertNotFound(
                "No videogame-pokedex associations found for videogame with id: 9.",
                () -> VideogamePokedexValidator.validateVideogameExists(false, 9L)
            )
        );
    }

    @Test
    void shouldAcceptNonEmptyResults() {
        List<Object> result = List.of(new Object());

        assertAll(
            () -> VideogamePokedexValidator.validateByPokedexResult(result, 25L),
            () -> VideogamePokedexValidator.validateByVideogameResult(result, 9L)
        );
    }

    @Test
    void shouldRejectNullAndEmptyResults() {
        assertAll(
            () -> assertNotFound(
                "No videogame-pokedex associations found for pokedex with id: 25.",
                () -> VideogamePokedexValidator.validateByPokedexResult(List.of(), 25L)
            ),
            () -> assertNotFound(
                "No videogame-pokedex associations found for videogame with id: 9.",
                () -> VideogamePokedexValidator.validateByVideogameResult(List.of(), 9L)
            ),
            () -> assertNotFound(
                "No videogame-pokedex associations found for videogame with id: 9.",
                () -> VideogamePokedexValidator.validateByVideogameResult(null, 9L)
            )
        );
    }

    private static void assertNotFound(String expectedMessage, Executable validation) {
        VideogamePokedexNotFoundException exception = assertThrows(
            VideogamePokedexNotFoundException.class,
            validation
        );
        assertEquals(expectedMessage, exception.getMessage());
    }
}
