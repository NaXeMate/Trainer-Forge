package dev.trainerforge.validator;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import dev.trainerforge.dto.response.TeamDto;
import dev.trainerforge.exception.InvalidFilterValueException;
import dev.trainerforge.exception.notfound.TeamNotFoundException;
import dev.trainerforge.model.enumerated.TeamModality;

class TeamValidatorTest {

    @Test
    void shouldAcceptNonNullIdentifiers() {
        assertAll(
            () -> TeamValidator.validateTrainerId(0L),
            () -> TeamValidator.validateVideogameId(0L)
        );
    }

    @Test
    void shouldRejectNullIdentifiers() {
        assertAll(
            () -> assertInvalidFilter(
                "The trainer ID cannot be null.",
                () -> TeamValidator.validateTrainerId(null)
            ),
            () -> assertInvalidFilter(
                "The videogame ID cannot be null.",
                () -> TeamValidator.validateVideogameId(null)
            )
        );
    }

    @Test
    void shouldAcceptExistingTrainerAndVideogame() {
        assertAll(
            () -> TeamValidator.validateTrainerExists(true, 3L),
            () -> TeamValidator.validateVideogameExists(true, 9L)
        );
    }

    @Test
    void shouldRejectMissingTrainerAndVideogame() {
        assertAll(
            () -> assertInvalidFilter(
                "No trainer found with id: 3.",
                () -> TeamValidator.validateTrainerExists(false, 3L)
            ),
            () -> assertInvalidFilter(
                "No videogame found with id: 9.",
                () -> TeamValidator.validateVideogameExists(false, 9L)
            )
        );
    }

    @Test
    void shouldValidateModality() {
        assertDoesNotThrow(() -> TeamValidator.validateModality(TeamModality.NORMAL));
        assertInvalidFilter(
            "The modality cannot be null.",
            () -> TeamValidator.validateModality(null)
        );
    }

    @Test
    void shouldAcceptNonEmptyResults() {
        List<Object> result = List.of(new Object());

        assertAll(
            () -> TeamValidator.validateByTrainerResult(result, 3L),
            () -> TeamValidator.validateByVideogameResult(result, 9L),
            () -> TeamValidator.validateByModalityResult(result, TeamModality.NORMAL),
            () -> TeamValidator.validateByVisibilityResult(result, true),
            () -> TeamValidator.validateByVisibilityResult(result, false)
        );
    }

    @Test
    void shouldRejectEmptyResultsWithSpecificMessages() {
        assertAll(
            () -> assertTeamNotFound(
                "No teams found for trainer with id: 3.",
                () -> TeamValidator.validateByTrainerResult(List.of(), 3L)
            ),
            () -> assertTeamNotFound(
                "No teams found for videogame with id: 9.",
                () -> TeamValidator.validateByVideogameResult(List.of(), 9L)
            ),
            () -> assertTeamNotFound(
                "No teams found for modality: NORMAL.",
                () -> TeamValidator.validateByModalityResult(List.of(), TeamModality.NORMAL)
            ),
            () -> assertTeamNotFound(
                "No hidden teams were found.",
                () -> TeamValidator.validateByVisibilityResult(List.of(), true)
            ),
            () -> assertTeamNotFound(
                "No public teams were found.",
                () -> TeamValidator.validateByVisibilityResult(List.of(), false)
            ),
            () -> assertTeamNotFound(
                "No teams found for trainer with id: 3.",
                () -> TeamValidator.validateByTrainerResult(null, 3L)
            )
        );
    }

    @Test
    void shouldValidateOwnership() {
        assertDoesNotThrow(() -> TeamValidator.validateOwnership(true, 4L));

        TeamNotFoundException exception = assertThrows(
            TeamNotFoundException.class,
            () -> TeamValidator.validateOwnership(false, 4L)
        );
        assertEquals("Team not found with id: 4.", exception.getMessage());
    }

    @Test
    void shouldAcceptMissingOrMatchingTrainerAssignment() {
        assertAll(
            () -> TeamValidator.validateTrainerAssignment(teamDto(null), "ash"),
            () -> TeamValidator.validateTrainerAssignment(teamDto("ash"), "ash")
        );
    }

    @Test
    void shouldRejectAssignmentToAnotherTrainer() {
        assertInvalidFilter(
            "A team cannot be assigned to another trainer.",
            () -> TeamValidator.validateTrainerAssignment(teamDto("misty"), "ash")
        );
    }

    @Test
    void shouldReturnAuthenticatedUsername() {
        Authentication authentication = new UsernamePasswordAuthenticationToken(
            "ash",
            "password",
            List.of()
        );

        assertEquals("ash", TeamValidator.getAuthenticatedUsername(authentication));
    }

    @Test
    void shouldRejectMissingInvalidOrBlankAuthentication() {
        Authentication unauthenticated = new UsernamePasswordAuthenticationToken("ash", "password");
        Authentication blankName = new UsernamePasswordAuthenticationToken(" ", "password", List.of());

        assertAll(
            () -> assertAuthenticationRequired(null),
            () -> assertAuthenticationRequired(unauthenticated),
            () -> assertAuthenticationRequired(blankName)
        );
    }

    private static TeamDto teamDto(String trainerUsername) {
        return new TeamDto(null, "Kanto team", TeamModality.NORMAL, false, trainerUsername, "Red");
    }

    private static void assertInvalidFilter(String expectedMessage, Executable validation) {
        InvalidFilterValueException exception = assertThrows(InvalidFilterValueException.class, validation);
        assertEquals(expectedMessage, exception.getMessage());
    }

    private static void assertTeamNotFound(String expectedMessage, Executable validation) {
        TeamNotFoundException exception = assertThrows(TeamNotFoundException.class, validation);
        assertEquals(expectedMessage, exception.getMessage());
    }

    private static void assertAuthenticationRequired(Authentication authentication) {
        AuthenticationCredentialsNotFoundException exception = assertThrows(
            AuthenticationCredentialsNotFoundException.class,
            () -> TeamValidator.getAuthenticatedUsername(authentication)
        );
        assertEquals("Authentication is required.", exception.getMessage());
    }
}
