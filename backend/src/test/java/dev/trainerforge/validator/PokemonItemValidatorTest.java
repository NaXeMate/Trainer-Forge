package dev.trainerforge.validator;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import dev.trainerforge.exception.InvalidFilterValueException;
import dev.trainerforge.exception.notfound.ItemNotFoundException;
import dev.trainerforge.model.enumerated.ItemType;

class PokemonItemValidatorTest {

    @ParameterizedTest
    @ValueSource(longs = {1L, 10L})
    void shouldAcceptValidGenerationIds(Long generationId) {
        assertDoesNotThrow(() -> PokemonItemValidator.validateGenerationId(generationId));
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(longs = {0L, 11L})
    void shouldRejectInvalidGenerationIds(Long generationId) {
        InvalidFilterValueException exception = assertThrows(
            InvalidFilterValueException.class,
            () -> PokemonItemValidator.validateGenerationId(generationId)
        );

        assertEquals("Generation ID must be between 1 and 10.", exception.getMessage());
    }

    @Test
    void shouldAcceptNonEmptyResults() {
        List<Object> result = List.of(new Object());

        assertAll(
            () -> PokemonItemValidator.validateByTypeResult(result, ItemType.HELD_ITEM),
            () -> PokemonItemValidator.validateByGenerationResult(result, 1L)
        );
    }

    @Test
    void shouldRejectEmptyResultsWithSpecificMessages() {
        assertAll(
            () -> assertItemNotFound(
                "No items found with type: HELD_ITEM.",
                () -> PokemonItemValidator.validateByTypeResult(List.of(), ItemType.HELD_ITEM)
            ),
            () -> assertItemNotFound(
                "No items found with generation ID: 1.",
                () -> PokemonItemValidator.validateByGenerationResult(List.of(), 1L)
            ),
            () -> assertItemNotFound(
                "No items found with generation ID: 1.",
                () -> PokemonItemValidator.validateByGenerationResult(null, 1L)
            )
        );
    }

    private static void assertItemNotFound(String expectedMessage, Executable validation) {
        ItemNotFoundException exception = assertThrows(ItemNotFoundException.class, validation);
        assertEquals(expectedMessage, exception.getMessage());
    }
}
