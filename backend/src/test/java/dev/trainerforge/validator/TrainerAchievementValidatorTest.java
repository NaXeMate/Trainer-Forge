package dev.trainerforge.validator;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import dev.trainerforge.exception.InvalidFilterValueException;
import dev.trainerforge.exception.notfound.AchievementNotFoundException;
import dev.trainerforge.exception.notfound.TrainerAchievementNotFoundException;
import dev.trainerforge.exception.notfound.TrainerNotFoundException;

class TrainerAchievementValidatorTest {

    private static final LocalDateTime START = LocalDateTime.of(2026, 8, 18, 10, 0);
    private static final LocalDateTime END = LocalDateTime.of(2026, 8, 18, 12, 0);

    @Test
    void shouldParseNumericAchievementId() {
        assertEquals(42L, TrainerAchievementValidator.parseAchievementId("42"));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"abc", "1.5", "9223372036854775808"})
    void shouldRejectInvalidAchievementId(String achievement) {
        assertInvalidFilter(
            "Achievement id must be a number.",
            () -> TrainerAchievementValidator.parseAchievementId(achievement)
        );
    }

    @Test
    void shouldAcceptExistingResourcesAndLockedAchievement() {
        assertAll(
            () -> TrainerAchievementValidator.validateAchievementExists(true, 8L),
            () -> TrainerAchievementValidator.validateTrainerExists(true, 3L),
            () -> TrainerAchievementValidator.validateNotAlreadyUnlocked(false, 3L, 8L)
        );
    }

    @Test
    void shouldRejectMissingResourcesAndAlreadyUnlockedAchievement() {
        AchievementNotFoundException achievementException = assertThrows(
            AchievementNotFoundException.class,
            () -> TrainerAchievementValidator.validateAchievementExists(false, 8L)
        );
        TrainerNotFoundException trainerException = assertThrows(
            TrainerNotFoundException.class,
            () -> TrainerAchievementValidator.validateTrainerExists(false, 3L)
        );

        assertAll(
            () -> assertEquals("Achievement not found with id: 8.", achievementException.getMessage()),
            () -> assertEquals("Trainer not found with id: 3", trainerException.getMessage()),
            () -> assertInvalidFilter(
                "Trainer with id: 3 has already unlocked achievement with id: 8.",
                () -> TrainerAchievementValidator.validateNotAlreadyUnlocked(true, 3L, 8L)
            )
        );
    }

    @Test
    void shouldValidateSingleDate() {
        assertDoesNotThrow(() -> TrainerAchievementValidator.validateDate(START));
        assertInvalidFilter(
            "Date obtained cannot be null.",
            () -> TrainerAchievementValidator.validateDate(null)
        );
    }

    @Test
    void shouldAcceptOrderedAndEqualDateRanges() {
        assertAll(
            () -> TrainerAchievementValidator.validateDateBetween(START, END),
            () -> TrainerAchievementValidator.validateDateBetween(START, START)
        );
    }

    @Test
    void shouldRejectNullOrInvertedDateRanges() {
        assertAll(
            () -> assertInvalidFilter(
                "Start date and end date cannot be null.",
                () -> TrainerAchievementValidator.validateDateBetween(null, END)
            ),
            () -> assertInvalidFilter(
                "Start date and end date cannot be null.",
                () -> TrainerAchievementValidator.validateDateBetween(START, null)
            ),
            () -> assertInvalidFilter(
                "Start date cannot be after end date.",
                () -> TrainerAchievementValidator.validateDateBetween(END, START)
            )
        );
    }

    @Test
    void shouldAcceptNonEmptyResults() {
        List<Object> result = List.of(new Object());

        assertAll(
            () -> TrainerAchievementValidator.validateByTrainerResult(result, 3L),
            () -> TrainerAchievementValidator.validateByAchievementResult(result, 8L),
            () -> TrainerAchievementValidator.validateByDateResult(result, START),
            () -> TrainerAchievementValidator.validateByDateRangeResult(result, START, END)
        );
    }

    @Test
    void shouldRejectEmptyResultsWithSpecificMessages() {
        assertAll(
            () -> assertResultNotFound(
                "No achievements found for trainer with id: 3.",
                () -> TrainerAchievementValidator.validateByTrainerResult(List.of(), 3L)
            ),
            () -> assertResultNotFound(
                "No trainers found with achievement id: 8.",
                () -> TrainerAchievementValidator.validateByAchievementResult(List.of(), 8L)
            ),
            () -> assertResultNotFound(
                "No achievements found obtained on: 2026-08-18T10:00.",
                () -> TrainerAchievementValidator.validateByDateResult(List.of(), START)
            ),
            () -> assertResultNotFound(
                "No achievements found obtained between: 2026-08-18T10:00 and 2026-08-18T12:00.",
                () -> TrainerAchievementValidator.validateByDateRangeResult(List.of(), START, END)
            ),
            () -> assertResultNotFound(
                "No achievements found for trainer with id: 3.",
                () -> TrainerAchievementValidator.validateByTrainerResult(null, 3L)
            )
        );
    }

    private static void assertInvalidFilter(String expectedMessage, Executable validation) {
        InvalidFilterValueException exception = assertThrows(InvalidFilterValueException.class, validation);
        assertEquals(expectedMessage, exception.getMessage());
    }

    private static void assertResultNotFound(String expectedMessage, Executable validation) {
        TrainerAchievementNotFoundException exception = assertThrows(
            TrainerAchievementNotFoundException.class,
            validation
        );
        assertEquals(expectedMessage, exception.getMessage());
    }
}
