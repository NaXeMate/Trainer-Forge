package dev.trainerforge.validator;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import dev.trainerforge.exception.notfound.MovePokedexNotFoundException;
import dev.trainerforge.model.enumerated.LearningMethod;

class MovePokedexValidatorTest {

    @Test
    void shouldAcceptExistingMoveAndPokedex() {
        assertAll(
            () -> assertDoesNotThrow(() -> MovePokedexValidator.validateMoveExists(true, 4L)),
            () -> assertDoesNotThrow(() -> MovePokedexValidator.validatePokedexExists(true, 25L))
        );
    }

    @Test
    void shouldRejectMissingMoveAndPokedex() {
        assertAll(
            () -> assertNotFound(
                "MovePokedex entry not found with move id: 4.",
                () -> MovePokedexValidator.validateMoveExists(false, 4L)
            ),
            () -> assertNotFound(
                "MovePokedex entry not found with pokedex id: 25.",
                () -> MovePokedexValidator.validatePokedexExists(false, 25L)
            )
        );
    }

    @Test
    void shouldAcceptNonEmptyResults() {
        List<Object> result = List.of(new Object());

        assertAll(
            () -> MovePokedexValidator.validateByMoveResult(result, 4L),
            () -> MovePokedexValidator.validateByPokedexResult(result, 25L),
            () -> MovePokedexValidator.validateByLearningMethodResult(result, LearningMethod.LEVEL)
        );
    }

    @Test
    void shouldRejectNullAndEmptyResultsWithSpecificMessages() {
        assertAll(
            () -> assertNotFound(
                "MovePokedex entry not found with move id: 4.",
                () -> MovePokedexValidator.validateByMoveResult(List.of(), 4L)
            ),
            () -> assertNotFound(
                "MovePokedex entry not found with pokedex id: 25.",
                () -> MovePokedexValidator.validateByPokedexResult(List.of(), 25L)
            ),
            () -> assertNotFound(
                "MovePokedex entry not found with learning method: LEVEL.",
                () -> MovePokedexValidator.validateByLearningMethodResult(List.of(), LearningMethod.LEVEL)
            ),
            () -> assertNotFound(
                "MovePokedex entry not found with move id: 4.",
                () -> MovePokedexValidator.validateByMoveResult(null, 4L)
            )
        );
    }

    private static void assertNotFound(String expectedMessage, Executable validation) {
        MovePokedexNotFoundException exception = assertThrows(MovePokedexNotFoundException.class, validation);
        assertEquals(expectedMessage, exception.getMessage());
    }
}
