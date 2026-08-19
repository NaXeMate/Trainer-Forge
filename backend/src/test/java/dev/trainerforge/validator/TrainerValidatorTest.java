package dev.trainerforge.validator;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import dev.trainerforge.exception.InvalidFilterValueException;
import dev.trainerforge.exception.notfound.TrainerNotFoundException;
import dev.trainerforge.model.enumerated.TrainerClass;

class TrainerValidatorTest {

    @Test
    void shouldAcceptValidUsernameIncludingMaximumLength() {
        assertAll(
            () -> TrainerValidator.validateUsername("ash"),
            () -> TrainerValidator.validateUsername("a".repeat(30))
        );
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "\t"})
    void shouldRejectNullEmptyOrBlankUsername(String username) {
        assertInvalidFilter(
            "The username cannot be empty.",
            () -> TrainerValidator.validateUsername(username)
        );
    }

    @Test
    void shouldRejectUsernameAboveMaximumLength() {
        assertInvalidFilter(
            "The username exceeds the maximum allowed length.",
            () -> TrainerValidator.validateUsername("a".repeat(31))
        );
    }

    @Test
    void shouldAcceptValidEmailIncludingMaximumLength() {
        String maximumLengthEmail = "a".repeat(242) + "@example.com";

        assertAll(
            () -> TrainerValidator.validateEmail("ash@example.com"),
            () -> TrainerValidator.validateEmail(maximumLengthEmail),
            () -> assertEquals(254, maximumLengthEmail.length())
        );
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "\t"})
    void shouldRejectNullEmptyOrBlankEmail(String email) {
        assertInvalidFilter(
            "The email cannot be empty.",
            () -> TrainerValidator.validateEmail(email)
        );
    }

    @Test
    void shouldRejectEmailAboveMaximumLength() {
        assertInvalidFilter(
            "The email exceeds the maximum allowed length.",
            () -> TrainerValidator.validateEmail("a".repeat(243) + "@example.com")
        );
    }

    @ParameterizedTest
    @ValueSource(strings = {"ash", "ash@", "ash.example.com", "ash@example.c"})
    void shouldRejectInvalidEmailFormat(String email) {
        assertInvalidFilter(
            "The email does not have a valid format.",
            () -> TrainerValidator.validateEmail(email)
        );
    }

    @Test
    void shouldAcceptNullAndValidRealNameIncludingMaximumLength() {
        assertAll(
            () -> TrainerValidator.validateRealName(null),
            () -> TrainerValidator.validateRealName("A".repeat(50))
        );
    }

    @Test
    void shouldRejectRealNameAboveMaximumLength() {
        assertInvalidFilter(
            "The real name exceeds the maximum allowed length.",
            () -> TrainerValidator.validateRealName("A".repeat(51))
        );
    }

    @Test
    void shouldAcceptNonBlankPassword() {
        assertDoesNotThrow(() -> TrainerValidator.validatePassword("secret"));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "\t"})
    void shouldRejectNullEmptyOrBlankPassword(String password) {
        assertInvalidFilter(
            "The password cannot be empty.",
            () -> TrainerValidator.validatePassword(password)
        );
    }

    @Test
    void shouldValidateUsernameAndEmailUniqueness() {
        assertAll(
            () -> TrainerValidator.validateUsernameUniqueness(false),
            () -> TrainerValidator.validateEmailUniqueness(false),
            () -> assertInvalidFilter(
                "A trainer with that username already exists.",
                () -> TrainerValidator.validateUsernameUniqueness(true)
            ),
            () -> assertInvalidFilter(
                "A trainer with that email already exists.",
                () -> TrainerValidator.validateEmailUniqueness(true)
            )
        );
    }

    @Test
    void shouldAcceptValidFriendCode() {
        assertDoesNotThrow(() -> TrainerValidator.validateFriendCode("TF-1234-5678"));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "\t"})
    void shouldRejectNullEmptyOrBlankFriendCode(String friendCode) {
        assertInvalidFilter(
            "The friend code cannot be empty.",
            () -> TrainerValidator.validateFriendCode(friendCode)
        );
    }

    @ParameterizedTest
    @ValueSource(strings = {"1234-5678", "TF-123-5678", "tf-1234-5678", "TF-ABCD-5678"})
    void shouldRejectInvalidFriendCodeFormat(String friendCode) {
        assertInvalidFilter(
            "The friend code does not have a valid format. It should be in the format 'TF-XXXX-XXXX'.",
            () -> TrainerValidator.validateFriendCode(friendCode)
        );
    }

    @ParameterizedTest
    @ValueSource(longs = {1L, 10L})
    void shouldAcceptValidRegionIds(Long regionId) {
        assertDoesNotThrow(() -> TrainerValidator.validateRegionId(regionId));
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(longs = {0L, 11L})
    void shouldRejectInvalidRegionIds(Long regionId) {
        assertInvalidFilter(
            "Region ID must be between 1 and 10.",
            () -> TrainerValidator.validateRegionId(regionId)
        );
    }

    @Test
    void shouldValidateTrainerExistence() {
        assertDoesNotThrow(() -> TrainerValidator.validateTrainerExists(true, 3L));

        TrainerNotFoundException exception = assertThrows(
            TrainerNotFoundException.class,
            () -> TrainerValidator.validateTrainerExists(false, 3L)
        );
        assertEquals("Trainer not found with id: 3", exception.getMessage());
    }

    @Test
    void shouldAcceptNonEmptyResults() {
        List<Object> result = List.of(new Object());

        assertAll(
            () -> TrainerValidator.validateByRegionResult(result, 1L),
            () -> TrainerValidator.validateByFavoriteGameResult(result, 2L),
            () -> TrainerValidator.validateByFavoritePokemonResult(result, 25L),
            () -> TrainerValidator.validateByBestFriendResult(result, 3L),
            () -> TrainerValidator.validateByTrainerClassResult(result, TrainerClass.CHAMPION),
            () -> TrainerValidator.validateByTrainerGamesResult(result, 3L),
            () -> TrainerValidator.validateByVideogameResult(result, 2L)
        );
    }

    @Test
    void shouldRejectEmptyResultsWithSpecificMessages() {
        assertAll(
            () -> assertTrainerNotFound(
                "No trainers found in region with id: 1.",
                () -> TrainerValidator.validateByRegionResult(List.of(), 1L)
            ),
            () -> assertTrainerNotFound(
                "No trainers found with favorite game id: 2.",
                () -> TrainerValidator.validateByFavoriteGameResult(List.of(), 2L)
            ),
            () -> assertTrainerNotFound(
                "No trainers found with favorite Pokemon id: 25.",
                () -> TrainerValidator.validateByFavoritePokemonResult(List.of(), 25L)
            ),
            () -> assertTrainerNotFound(
                "No trainers found with best friend id: 3.",
                () -> TrainerValidator.validateByBestFriendResult(List.of(), 3L)
            ),
            () -> assertTrainerNotFound(
                "No trainers found with trainer class: CHAMPION.",
                () -> TrainerValidator.validateByTrainerClassResult(List.of(), TrainerClass.CHAMPION)
            ),
            () -> assertTrainerNotFound(
                "No game possessions found for trainer with id: 3.",
                () -> TrainerValidator.validateByTrainerGamesResult(List.of(), 3L)
            ),
            () -> assertTrainerNotFound(
                "No trainers found with videogame id: 2.",
                () -> TrainerValidator.validateByVideogameResult(List.of(), 2L)
            ),
            () -> assertTrainerNotFound(
                "No trainers found in region with id: 1.",
                () -> TrainerValidator.validateByRegionResult(null, 1L)
            )
        );
    }

    private static void assertInvalidFilter(String expectedMessage, Executable validation) {
        InvalidFilterValueException exception = assertThrows(InvalidFilterValueException.class, validation);
        assertEquals(expectedMessage, exception.getMessage());
    }

    private static void assertTrainerNotFound(String expectedMessage, Executable validation) {
        TrainerNotFoundException exception = assertThrows(TrainerNotFoundException.class, validation);
        assertEquals(expectedMessage, exception.getMessage());
    }
}
