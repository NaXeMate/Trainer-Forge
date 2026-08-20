package dev.trainerforge.validator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import dev.trainerforge.exception.notfound.GenerationNotFoundException;

class GenerationValidatorTest {

    @ParameterizedTest(name = "generation {0} resolves to {1}")
    @CsvSource({
        "1, Generation I",
        "2, Generation II",
        "3, Generation III",
        "4, Generation IV",
        "5, Generation V",
        "6, Generation VI",
        "7, Generation VII",
        "8, Generation VIII",
        "9, Generation IX",
        "10, Generation X"
    })
    void shouldResolveEverySupportedGeneration(int number, String expectedName) {
        assertEquals(expectedName, GenerationValidator.resolveGenerationName(number));
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 11})
    void shouldRejectUnsupportedGenerationNumbers(int number) {
        GenerationNotFoundException exception = assertThrows(
            GenerationNotFoundException.class,
            () -> GenerationValidator.resolveGenerationName(number)
        );

        assertEquals("Generation not found with number: " + number + ".", exception.getMessage());
    }
}
