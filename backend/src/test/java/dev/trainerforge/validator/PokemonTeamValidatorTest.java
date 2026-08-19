package dev.trainerforge.validator;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import dev.trainerforge.exception.InvalidFilterValueException;
import dev.trainerforge.exception.notfound.PokemonNotFoundException;
import dev.trainerforge.exception.notfound.PokemonTeamNotFoundException;

class PokemonTeamValidatorTest {

    @Test
    void shouldParseNumericIdentifiers() {
        assertArrayEquals(new long[] {12L, 34L}, PokemonTeamValidator.parseIdentifiers("12", "34"));
    }

    @ParameterizedTest
    @CsvSource(value = {
        "abc, 2",
        "1, abc",
        "1.5, 2",
        "9223372036854775808, 2",
        "NULL, 2",
        "1, NULL"
    }, nullValues = "NULL")
    void shouldRejectNonNumericIdentifiers(String teamId, String pokemonId) {
        assertInvalidFilter(
            "teamId and pokemonId must be numeric.",
            () -> PokemonTeamValidator.parseIdentifiers(teamId, pokemonId)
        );
    }

    @Test
    void shouldValidatePokemonExistence() {
        assertDoesNotThrow(() -> PokemonTeamValidator.validatePokemonExists(true, 25L));

        PokemonNotFoundException exception = assertThrows(
            PokemonNotFoundException.class,
            () -> PokemonTeamValidator.validatePokemonExists(false, 25L)
        );
        assertEquals("Pokemon not found with id: 25.", exception.getMessage());
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 6})
    void shouldAcceptValidPositions(int position) {
        assertDoesNotThrow(() -> PokemonTeamValidator.validatePosition(position));
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 7})
    void shouldRejectInvalidPositions(int position) {
        assertInvalidFilter(
            "Position must be between 1 and 6.",
            () -> PokemonTeamValidator.validatePosition(position)
        );
    }

    @Test
    void shouldAcceptAvailablePositionAndPokemonNotAlreadyInTeam() {
        assertAll(
            () -> PokemonTeamValidator.validatePositionAvailable(false, 2, 4L),
            () -> PokemonTeamValidator.validatePokemonNotInTeam(false, 25L, 4L)
        );
    }

    @Test
    void shouldRejectOccupiedPositions() {
        assertAll(
            () -> assertInvalidFilter(
                "Position 2 is already occupied in team with id: 4.",
                () -> PokemonTeamValidator.validatePositionAvailable(true, 2, 4L)
            ),
            () -> assertInvalidFilter(
                "Position 2 is already occupied in this team.",
                () -> PokemonTeamValidator.validatePositionAvailable(true, 2, null)
            )
        );
    }

    @Test
    void shouldRejectPokemonAlreadyPresentInTeam() {
        assertInvalidFilter(
            "Pokemon with id 25 is already in team with id: 4.",
            () -> PokemonTeamValidator.validatePokemonNotInTeam(true, 25L, 4L)
        );
    }

    @Test
    void shouldAcceptNonEmptyResults() {
        List<Object> result = List.of(new Object());

        assertAll(
            () -> PokemonTeamValidator.validateByTeamResult(result, 4L),
            () -> PokemonTeamValidator.validateByPokemonResult(result, 25L),
            () -> PokemonTeamValidator.validateByPositionResult(result, 2)
        );
    }

    @Test
    void shouldRejectEmptyResultsWithSpecificMessages() {
        assertAll(
            () -> assertAssociationNotFound(
                "No Pokemon-Team associations found for team with id: 4.",
                () -> PokemonTeamValidator.validateByTeamResult(List.of(), 4L)
            ),
            () -> assertAssociationNotFound(
                "No team-pokemon association found with pokemon id: 25.",
                () -> PokemonTeamValidator.validateByPokemonResult(List.of(), 25L)
            ),
            () -> assertAssociationNotFound(
                "No team-pokemon association found with position: 2.",
                () -> PokemonTeamValidator.validateByPositionResult(List.of(), 2)
            ),
            () -> assertAssociationNotFound(
                "No Pokemon-Team associations found for team with id: 4.",
                () -> PokemonTeamValidator.validateByTeamResult(null, 4L)
            )
        );
    }

    private static void assertInvalidFilter(String expectedMessage, Executable validation) {
        InvalidFilterValueException exception = assertThrows(InvalidFilterValueException.class, validation);
        assertEquals(expectedMessage, exception.getMessage());
    }

    private static void assertAssociationNotFound(String expectedMessage, Executable validation) {
        PokemonTeamNotFoundException exception = assertThrows(PokemonTeamNotFoundException.class, validation);
        assertEquals(expectedMessage, exception.getMessage());
    }
}
