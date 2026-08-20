package dev.trainerforge.validator;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import dev.trainerforge.exception.notfound.VideogameNotFoundException;

class VideogameValidatorTest {

    @ParameterizedTest
    @ValueSource(longs = {1L, 10L})
    void shouldAcceptValidGenerationAndRegionIds(Long id) {
        assertAll(
            () -> VideogameValidator.validateGenerationId(id),
            () -> VideogameValidator.validateRegionId(id)
        );
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(longs = {0L, 11L})
    void shouldRejectInvalidGenerationIds(Long generationId) {
        assertIllegalArgument(
            "Generation ID must be between 1 and 10.",
            () -> VideogameValidator.validateGenerationId(generationId)
        );
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(longs = {0L, 11L})
    void shouldRejectInvalidRegionIds(Long regionId) {
        assertIllegalArgument(
            "Region ID must be between 1 and 10.",
            () -> VideogameValidator.validateRegionId(regionId)
        );
    }

    @Test
    void shouldAcceptNonEmptyResults() {
        List<Object> result = List.of(new Object());

        assertAll(
            () -> VideogameValidator.validateByGenerationResult(result, 1L),
            () -> VideogameValidator.validateByRegionResult(result, 2L),
            () -> VideogameValidator.validateByPokedexResult(result, 3L)
        );
    }

    @Test
    void shouldRejectEmptyResultsWithSpecificMessages() {
        assertAll(
            () -> assertVideogameNotFound(
                "No videogames found for Generation ID: 1.",
                () -> VideogameValidator.validateByGenerationResult(List.of(), 1L)
            ),
            () -> assertVideogameNotFound(
                "No videogames found for Region ID: 2.",
                () -> VideogameValidator.validateByRegionResult(List.of(), 2L)
            ),
            () -> assertVideogameNotFound(
                "There are no Pokemon in this videogame ID: 3.",
                () -> VideogameValidator.validateByPokedexResult(List.of(), 3L)
            ),
            () -> assertVideogameNotFound(
                "There are no Pokemon in this videogame ID: 3.",
                () -> VideogameValidator.validateByPokedexResult(null, 3L)
            )
        );
    }

    private static void assertIllegalArgument(String expectedMessage, Executable validation) {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, validation);
        assertEquals(expectedMessage, exception.getMessage());
    }

    private static void assertVideogameNotFound(String expectedMessage, Executable validation) {
        VideogameNotFoundException exception = assertThrows(VideogameNotFoundException.class, validation);
        assertEquals(expectedMessage, exception.getMessage());
    }
}
