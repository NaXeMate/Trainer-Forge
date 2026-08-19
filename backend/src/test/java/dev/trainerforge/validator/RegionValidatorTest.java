package dev.trainerforge.validator;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import dev.trainerforge.exception.InvalidFilterValueException;
import dev.trainerforge.exception.notfound.RegionNotFoundException;

class RegionValidatorTest {

    @ParameterizedTest
    @ValueSource(longs = {1L, 10L})
    void shouldAcceptValidGenerationIds(Long generationId) {
        assertDoesNotThrow(() -> RegionValidator.validateGenerationId(generationId));
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(longs = {0L, 11L})
    void shouldRejectInvalidGenerationIds(Long generationId) {
        InvalidFilterValueException exception = assertThrows(
            InvalidFilterValueException.class,
            () -> RegionValidator.validateGenerationId(generationId)
        );

        assertEquals("Generation ID must be between 1 and 10.", exception.getMessage());
    }

    @Test
    void shouldAcceptNonEmptyGenerationResult() {
        assertDoesNotThrow(() -> RegionValidator.validateByGenerationResult(List.of(new Object()), 1L));
    }

    @Test
    void shouldRejectNullAndEmptyGenerationResults() {
        assertGenerationNotFound(null);
        assertGenerationNotFound(List.of());
    }

    private static void assertGenerationNotFound(List<?> result) {
        RegionNotFoundException exception = assertThrows(
            RegionNotFoundException.class,
            () -> RegionValidator.validateByGenerationResult(result, 1L)
        );

        assertEquals("No regions found for generation ID: 1.", exception.getMessage());
    }
}
