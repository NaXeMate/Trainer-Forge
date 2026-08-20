package dev.trainerforge.validator;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.junit.jupiter.api.Test;

import dev.trainerforge.exception.notfound.NatureNotFoundException;
import dev.trainerforge.model.enumerated.NatureRiseLower;

class NatureValidatorTest {

    @Test
    void shouldAcceptNonEmptyResults() {
        List<Object> result = List.of(new Object());

        assertAll(
            () -> assertDoesNotThrow(() -> NatureValidator.validateByRiseResult(result, NatureRiseLower.ATTACK)),
            () -> assertDoesNotThrow(() -> NatureValidator.validateByLowerResult(result, NatureRiseLower.DEFENSE))
        );
    }

    @Test
    void shouldRejectNullAndEmptyResults() {
        assertAll(
            () -> assertNatureNotFound(
                "No natures found with rise: ATTACK.",
                () -> NatureValidator.validateByRiseResult(null, NatureRiseLower.ATTACK)
            ),
            () -> assertNatureNotFound(
                "No natures found with rise: ATTACK.",
                () -> NatureValidator.validateByRiseResult(List.of(), NatureRiseLower.ATTACK)
            ),
            () -> assertNatureNotFound(
                "No natures found with lower: DEFENSE.",
                () -> NatureValidator.validateByLowerResult(List.of(), NatureRiseLower.DEFENSE)
            )
        );
    }

    private static void assertNatureNotFound(String expectedMessage, org.junit.jupiter.api.function.Executable validation) {
        NatureNotFoundException exception = assertThrows(NatureNotFoundException.class, validation);
        assertEquals(expectedMessage, exception.getMessage());
    }
}
