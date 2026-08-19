package dev.trainerforge.validator;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import dev.trainerforge.exception.InvalidFilterValueException;
import dev.trainerforge.model.entities.Pokemon;

class BattleSimulatorValidatorTest {

    @Test
    void shouldAcceptNonNullTeamId() {
        assertDoesNotThrow(() -> BattleSimulatorValidator.validateTeamId(1L, "team one"));
    }

    @Test
    void shouldRejectNullTeamIdWithItsLabel() {
        InvalidFilterValueException exception = assertThrows(
            InvalidFilterValueException.class,
            () -> BattleSimulatorValidator.validateTeamId(null, "team one")
        );

        assertEquals("Invalid team ID for team one.", exception.getMessage());
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 6})
    void shouldAcceptEqualTeamsWithinAllowedSize(int size) {
        assertDoesNotThrow(() -> BattleSimulatorValidator.validateTeamStructure(team(size), team(size)));
    }

    @Test
    void shouldRejectEmptyFirstTeam() {
        assertInvalidTeam(
            "Team one must have between 1 and 6 Pokemon.",
            List.of(),
            team(1)
        );
    }

    @Test
    void shouldRejectSecondTeamAboveMaximumSize() {
        assertInvalidTeam(
            "Team two must have between 1 and 6 Pokemon.",
            team(1),
            team(7)
        );
    }

    @Test
    void shouldRejectTeamsWithDifferentSizes() {
        assertInvalidTeam(
            "Both teams must have the same number of Pokemon to simulate a battle.",
            team(1),
            team(2)
        );
    }

    private static List<Pokemon> team(int size) {
        return Collections.nCopies(size, new Pokemon());
    }

    private static void assertInvalidTeam(String expectedMessage, List<Pokemon> teamOne, List<Pokemon> teamTwo) {
        InvalidFilterValueException exception = assertThrows(
            InvalidFilterValueException.class,
            () -> BattleSimulatorValidator.validateTeamStructure(teamOne, teamTwo)
        );
        assertEquals(expectedMessage, exception.getMessage());
    }
}
