package dev.trainerforge.validator;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.junit.jupiter.api.Test;

import dev.trainerforge.exception.notfound.AchievementNotFoundException;

class AchievementValidatorTest {

    @Test
    void shouldAcceptNonEmptyVisibleResult() {
        assertDoesNotThrow(() -> AchievementValidator.validateVisibleResult(List.of(new Object())));
    }

    @Test
    void shouldRejectNullAndEmptyVisibleResults() {
        assertVisibleAchievementsNotFound(null);
        assertVisibleAchievementsNotFound(List.of());
    }

    private static void assertVisibleAchievementsNotFound(List<?> result) {
        AchievementNotFoundException exception = assertThrows(
            AchievementNotFoundException.class,
            () -> AchievementValidator.validateVisibleResult(result)
        );

        assertEquals("No visible achievements found.", exception.getMessage());
    }
}
