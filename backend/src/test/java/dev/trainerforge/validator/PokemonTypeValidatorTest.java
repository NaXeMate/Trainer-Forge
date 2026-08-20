package dev.trainerforge.validator;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import dev.trainerforge.exception.notfound.PokemonTypeNotFoundException;

class PokemonTypeValidatorTest {

    @Test
    void shouldAcceptValidName() {
        assertDoesNotThrow(() -> PokemonTypeValidator.validateName("Fire"));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "\t"})
    void shouldRejectNullEmptyOrBlankName(String name) {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> PokemonTypeValidator.validateName(name)
        );

        assertEquals("Name cannot be null or blank.", exception.getMessage());
    }

    @ParameterizedTest
    @ValueSource(longs = {1L, 10L})
    void shouldAcceptValidGenerationIds(Long generationId) {
        assertDoesNotThrow(() -> PokemonTypeValidator.validateGenerationId(generationId));
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(longs = {0L, 11L})
    void shouldRejectInvalidGenerationIds(Long generationId) {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> PokemonTypeValidator.validateGenerationId(generationId)
        );

        assertEquals("Generation ID must be between 1 and 10.", exception.getMessage());
    }

    @Test
    void shouldAcceptNonEmptyGenerationResult() {
        assertDoesNotThrow(() -> PokemonTypeValidator.validateByGenerationResult(List.of(new Object()), 1L));
    }

    @Test
    void shouldRejectNullAndEmptyGenerationResults() {
        assertGenerationNotFound(null);
        assertGenerationNotFound(List.of());
    }

    private static void assertGenerationNotFound(List<?> result) {
        PokemonTypeNotFoundException exception = assertThrows(
            PokemonTypeNotFoundException.class,
            () -> PokemonTypeValidator.validateByGenerationResult(result, 1L)
        );

        assertEquals("No PokemonTypes found for generation with id: 1.", exception.getMessage());
    }
}
