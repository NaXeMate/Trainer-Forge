package dev.trainerforge.validator;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import dev.trainerforge.exception.InvalidFilterValueException;
import dev.trainerforge.exception.notfound.MoveNotFoundException;
import dev.trainerforge.model.enumerated.MoveClass;

class MoveValidatorTest {

    @Nested
    class TypeIdValidation {

        @ParameterizedTest(name = "accepts type ID {0}")
        @ValueSource(longs = {1L, 18L})
        void shouldAcceptTypeIdsInsideAllowedRange(Long typeId) {
            assertDoesNotThrow(() -> MoveValidator.validateTypeId(typeId));
        }

        @ParameterizedTest(name = "rejects type ID {0}")
        @NullSource
        @ValueSource(longs = {0L, 19L})
        void shouldRejectTypeIdsOutsideAllowedRange(Long typeId) {
            assertInvalidFilter(
                "Type ID must be between 1 and 18.",
                () -> MoveValidator.validateTypeId(typeId)
            );
        }
    }

    @Nested
    class PowerValidation {

        @ParameterizedTest(name = "accepts power {0}")
        @ValueSource(ints = {0, 5, 120})
        void shouldAcceptNonNegativeMultiplesOfFive(int power) {
            assertDoesNotThrow(() -> MoveValidator.validatePower(power));
        }

        @ParameterizedTest(name = "rejects negative power {0}")
        @ValueSource(ints = {-5, -1})
        void shouldRejectNegativePower(int power) {
            assertInvalidFilter(
                "Power cannot be negative. If you want to filter by STATUS moves, use 0 as the power value.",
                () -> MoveValidator.validatePower(power)
            );
        }

        @ParameterizedTest(name = "rejects power {0} because it is not a multiple of five")
        @ValueSource(ints = {1, 6, 99})
        void shouldRejectPowerThatIsNotMultipleOfFive(int power) {
            assertInvalidFilter(
                "Power must be a multiple of 5.",
                () -> MoveValidator.validatePower(power)
            );
        }
    }

    @Nested
    class PowerRangeValidation {

        @ParameterizedTest(name = "accepts power range {0}-{1}")
        @CsvSource({"0, 0", "0, 5", "40, 120"})
        void shouldAcceptValidPowerRanges(int minPower, int maxPower) {
            assertDoesNotThrow(() -> MoveValidator.validatePowerBetween(minPower, maxPower));
        }

        @ParameterizedTest(name = "rejects negative power range {0}-{1}")
        @CsvSource({"-5, 0", "0, -5"})
        void shouldRejectPowerRangeWithNegativeBounds(int minPower, int maxPower) {
            assertInvalidFilter(
                "Power values must not be negative. If you want to filter by STATUS moves, use 0 as the power value.",
                () -> MoveValidator.validatePowerBetween(minPower, maxPower)
            );
        }

        @ParameterizedTest(name = "rejects non-multiple power range {0}-{1}")
        @CsvSource({"1, 5", "0, 6"})
        void shouldRejectPowerRangeWithBoundsThatAreNotMultiplesOfFive(int minPower, int maxPower) {
            assertInvalidFilter(
                "Power must be a multiple of 5.",
                () -> MoveValidator.validatePowerBetween(minPower, maxPower)
            );
        }

        @Test
        void shouldRejectPowerRangeWhenMinimumIsGreaterThanMaximum() {
            assertInvalidFilter(
                "Minimum power (10) cannot be greater than maximum power (5).",
                () -> MoveValidator.validatePowerBetween(10, 5)
            );
        }
    }

    @Nested
    class AccuracyValidation {

        @ParameterizedTest(name = "accepts accuracy {0}")
        @ValueSource(ints = {0, 5, 100})
        void shouldAcceptValidAccuracy(int accuracy) {
            assertDoesNotThrow(() -> MoveValidator.validateAccuracy(accuracy));
        }

        @ParameterizedTest(name = "rejects out-of-range accuracy {0}")
        @ValueSource(ints = {-5, 105})
        void shouldRejectAccuracyOutsideAllowedRange(int accuracy) {
            assertInvalidFilter(
                "Accuracy must be between 0 and 100.",
                () -> MoveValidator.validateAccuracy(accuracy)
            );
        }

        @ParameterizedTest(name = "rejects accuracy {0} because it is not a multiple of five")
        @ValueSource(ints = {1, 99})
        void shouldRejectAccuracyThatIsNotMultipleOfFive(int accuracy) {
            assertInvalidFilter(
                "Accuracy must be a multiple of 5.",
                () -> MoveValidator.validateAccuracy(accuracy)
            );
        }
    }

    @Nested
    class AccuracyRangeValidation {

        @ParameterizedTest(name = "accepts accuracy range {0}-{1}")
        @CsvSource({"0, 0", "0, 100", "35, 75"})
        void shouldAcceptValidAccuracyRanges(int minAccuracy, int maxAccuracy) {
            assertDoesNotThrow(() -> MoveValidator.validateAccuracyBetween(minAccuracy, maxAccuracy));
        }

        @ParameterizedTest(name = "rejects negative accuracy range {0}-{1}")
        @CsvSource({"-5, 0", "0, -5"})
        void shouldRejectAccuracyRangeWithNegativeBounds(int minAccuracy, int maxAccuracy) {
            assertInvalidFilter(
                "Accuracy values musn't be negative. If you want to filter by moves that never miss, use 0 as the accuracy value.",
                () -> MoveValidator.validateAccuracyBetween(minAccuracy, maxAccuracy)
            );
        }

        @ParameterizedTest(name = "rejects accuracy range above maximum {0}-{1}")
        @CsvSource({"0, 105", "105, 105", "105, 110"})
        void shouldRejectAccuracyRangeAboveAllowedMaximum(int minAccuracy, int maxAccuracy) {
            assertInvalidFilter(
                "Accuracy values must not be greater than 100.",
                () -> MoveValidator.validateAccuracyBetween(minAccuracy, maxAccuracy)
            );
        }

        @ParameterizedTest(name = "rejects non-multiple accuracy range {0}-{1}")
        @CsvSource({"1, 100", "0, 99"})
        void shouldRejectAccuracyRangeWithBoundsThatAreNotMultiplesOfFive(int minAccuracy, int maxAccuracy) {
            assertInvalidFilter(
                "Accuracy must be a multiple of 5.",
                () -> MoveValidator.validateAccuracyBetween(minAccuracy, maxAccuracy)
            );
        }

        @Test
        void shouldRejectAccuracyRangeWhenMinimumIsGreaterThanMaximum() {
            assertInvalidFilter(
                "Minimum accuracy (75) cannot be greater than maximum accuracy (25).",
                () -> MoveValidator.validateAccuracyBetween(75, 25)
            );
        }
    }

    @Nested
    class PriorityRangeValidation {

        @ParameterizedTest(name = "accepts priority range {0}-{1}")
        @CsvSource({"-7, -7", "-7, 5", "0, 0"})
        void shouldAcceptOrderedPriorityRanges(int minPriority, int maxPriority) {
            assertDoesNotThrow(() -> MoveValidator.validatePriorityBetween(minPriority, maxPriority));
        }

        @Test
        void shouldRejectPriorityRangeWhenMinimumIsGreaterThanMaximum() {
            assertInvalidFilter(
                "Minimum priority (5) cannot be greater than maximum priority (-1).",
                () -> MoveValidator.validatePriorityBetween(5, -1)
            );
        }
    }

    @Nested
    class PpValidation {

        @ParameterizedTest(name = "accepts PP {0}")
        @ValueSource(ints = {5, 10, 40})
        void shouldAcceptValidPp(int pp) {
            assertDoesNotThrow(() -> MoveValidator.validatePp(pp));
        }

        @ParameterizedTest(name = "rejects out-of-range PP {0}")
        @ValueSource(ints = {0, 45})
        void shouldRejectPpOutsideAllowedRange(int pp) {
            assertInvalidFilter(
                "PP must be between 5 and 40.",
                () -> MoveValidator.validatePp(pp)
            );
        }

        @ParameterizedTest(name = "rejects PP {0} because it is not a multiple of five")
        @ValueSource(ints = {6, 39})
        void shouldRejectPpThatIsNotMultipleOfFive(int pp) {
            assertInvalidFilter(
                "PP must be a multiple of 5.",
                () -> MoveValidator.validatePp(pp)
            );
        }
    }

    @Nested
    class PpRangeValidation {

        @ParameterizedTest(name = "accepts PP range {0}-{1}")
        @CsvSource({"5, 5", "5, 40", "10, 30"})
        void shouldAcceptValidPpRanges(int minPp, int maxPp) {
            assertDoesNotThrow(() -> MoveValidator.validatePpBetween(minPp, maxPp));
        }

        @ParameterizedTest(name = "rejects out-of-range PP range {0}-{1}")
        @CsvSource({"0, 5", "5, 45"})
        void shouldRejectPpRangeOutsideAllowedRange(int minPp, int maxPp) {
            assertInvalidFilter(
                "PP must be between 5 and 40.",
                () -> MoveValidator.validatePpBetween(minPp, maxPp)
            );
        }

        @ParameterizedTest(name = "rejects non-multiple PP range {0}-{1}")
        @CsvSource({"6, 40", "5, 39"})
        void shouldRejectPpRangeWithBoundsThatAreNotMultiplesOfFive(int minPp, int maxPp) {
            assertInvalidFilter(
                "PP must be a multiple of 5.",
                () -> MoveValidator.validatePpBetween(minPp, maxPp)
            );
        }

        @Test
        void shouldRejectPpRangeWhenMinimumIsGreaterThanMaximum() {
            assertInvalidFilter(
                "Minimum PP (20) cannot be greater than maximum PP (10).",
                () -> MoveValidator.validatePpBetween(20, 10)
            );
        }
    }

    @Nested
    class GenerationIdValidation {

        @ParameterizedTest(name = "accepts generation ID {0}")
        @ValueSource(longs = {1L, 10L})
        void shouldAcceptGenerationIdsInsideAllowedRange(Long generationId) {
            assertDoesNotThrow(() -> MoveValidator.validateGenerationId(generationId));
        }

        @ParameterizedTest(name = "rejects generation ID {0}")
        @NullSource
        @ValueSource(longs = {0L, 11L})
        void shouldRejectGenerationIdsOutsideAllowedRange(Long generationId) {
            assertInvalidFilter(
                "Generation ID must be between 1 and 10.",
                () -> MoveValidator.validateGenerationId(generationId)
            );
        }
    }

    @Nested
    class ResultValidation {

        @Test
        void shouldAcceptNonEmptyResultsForEveryFilter() {
            List<Object> result = List.of(new Object());

            assertAll(
                () -> MoveValidator.validateByTypeResult(result, 18L),
                () -> MoveValidator.validateByMoveClassResult(result, MoveClass.PHYSICAL),
                () -> MoveValidator.validateByPowerResult(result, 50),
                () -> MoveValidator.validateByPowerRangeResult(result, 40, 60),
                () -> MoveValidator.validateByAccuracyResult(result, 100),
                () -> MoveValidator.validateByAccuracyRangeResult(result, 80, 100),
                () -> MoveValidator.validateByContactResult(result, true),
                () -> MoveValidator.validateByPriorityResult(result, 1),
                () -> MoveValidator.validateByPriorityRangeResult(result, -1, 1),
                () -> MoveValidator.validateByTargetResult(result, 2L),
                () -> MoveValidator.validateBySecondaryEffectResult(result, 3L),
                () -> MoveValidator.validateByPpResult(result, 15),
                () -> MoveValidator.validateByPpRangeResult(result, 10, 20),
                () -> MoveValidator.validateByGenerationResult(result, 9L)
            );
        }

        @Test
        void shouldRejectNullResult() {
            assertMoveNotFound(
                "Move not found with type ID: 18.",
                () -> MoveValidator.validateByTypeResult(null, 18L)
            );
        }

        @Test
        void shouldRejectEmptyResultsWithFilterSpecificMessages() {
            List<Object> emptyResult = List.of();

            assertAll(
                () -> assertMoveNotFound(
                    "Move not found with type ID: 18.",
                    () -> MoveValidator.validateByTypeResult(emptyResult, 18L)
                ),
                () -> assertMoveNotFound(
                    "Move not found with move class: PHYSICAL.",
                    () -> MoveValidator.validateByMoveClassResult(emptyResult, MoveClass.PHYSICAL)
                ),
                () -> assertMoveNotFound(
                    "Move not found with power: 50.",
                    () -> MoveValidator.validateByPowerResult(emptyResult, 50)
                ),
                () -> assertMoveNotFound(
                    "Move not found with power between: 40 and 60.",
                    () -> MoveValidator.validateByPowerRangeResult(emptyResult, 40, 60)
                ),
                () -> assertMoveNotFound(
                    "Move not found with accuracy: 100.",
                    () -> MoveValidator.validateByAccuracyResult(emptyResult, 100)
                ),
                () -> assertMoveNotFound(
                    "Move not found with accuracy between: 80 and 100.",
                    () -> MoveValidator.validateByAccuracyRangeResult(emptyResult, 80, 100)
                ),
                () -> assertMoveNotFound(
                    "Move not found with contact: true.",
                    () -> MoveValidator.validateByContactResult(emptyResult, true)
                ),
                () -> assertMoveNotFound(
                    "Move not found with priority: 1.",
                    () -> MoveValidator.validateByPriorityResult(emptyResult, 1)
                ),
                () -> assertMoveNotFound(
                    "Move not found with priority between: -1 and 1.",
                    () -> MoveValidator.validateByPriorityRangeResult(emptyResult, -1, 1)
                ),
                () -> assertMoveNotFound(
                    "Move not found with target ID: 2.",
                    () -> MoveValidator.validateByTargetResult(emptyResult, 2L)
                ),
                () -> assertMoveNotFound(
                    "Move not found with secondary effect ID: 3.",
                    () -> MoveValidator.validateBySecondaryEffectResult(emptyResult, 3L)
                ),
                () -> assertMoveNotFound(
                    "Move not found with PP: 15.",
                    () -> MoveValidator.validateByPpResult(emptyResult, 15)
                ),
                () -> assertMoveNotFound(
                    "Move not found with PP between: 10 and 20.",
                    () -> MoveValidator.validateByPpRangeResult(emptyResult, 10, 20)
                ),
                () -> assertMoveNotFound(
                    "Move not found with generation ID: 9.",
                    () -> MoveValidator.validateByGenerationResult(emptyResult, 9L)
                )
            );
        }
    }

    private static void assertInvalidFilter(String expectedMessage, Executable validation) {
        InvalidFilterValueException exception = assertThrows(
            InvalidFilterValueException.class,
            validation
        );

        assertEquals(expectedMessage, exception.getMessage());
    }

    private static void assertMoveNotFound(String expectedMessage, Executable validation) {
        MoveNotFoundException exception = assertThrows(
            MoveNotFoundException.class,
            validation
        );

        assertEquals(expectedMessage, exception.getMessage());
    }
}
