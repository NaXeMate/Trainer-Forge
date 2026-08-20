package dev.trainerforge.validator;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import dev.trainerforge.dto.response.PokemonDto;
import dev.trainerforge.exception.InvalidFilterValueException;
import dev.trainerforge.exception.notfound.PokemonNotFoundException;
import dev.trainerforge.model.entities.Nature;
import dev.trainerforge.model.enumerated.Gender;
import dev.trainerforge.model.enumerated.NatureRiseLower;

class PokemonValidatorTest {

    @Nested
    class DtoValidation {

        @Test
        void shouldAcceptValidDtoWithMissingOptionalValues() {
            assertDoesNotThrow(() -> PokemonValidator.validatePokemonFromDto(validDto(null, " ", null), true));
        }

        @Test
        void shouldAcceptValidDtoWithUniqueOptionalMoves() {
            assertDoesNotThrow(() -> PokemonValidator.validatePokemonFromDto(
                validDto("Quick Attack", "Iron Tail", "Volt Tackle"),
                true
            ));
        }

        @Test
        void shouldRejectDtoWhenSpeciesDoesNotExist() {
            assertInvalidFilter(
                "No Pokemon found with species id: 25.",
                () -> PokemonValidator.validatePokemonFromDto(validDto(null, null, null), false)
            );
        }

        @Test
        void shouldRejectDtoWithDuplicatedMoves() {
            assertInvalidFilter(
                "All moves must be different from each other.",
                () -> PokemonValidator.validatePokemonFromDto(
                    validDto("Thunderbolt", null, null),
                    true
                )
            );
        }
    }

    @Nested
    class SimpleValueValidation {

        @Test
        void shouldAcceptExistingSpeciesAndValidSimpleValues() {
            assertAll(
                () -> PokemonValidator.validateSpecies(true, 25L),
                () -> PokemonValidator.validateLocationFound("Route 1"),
                () -> PokemonValidator.validateAbility("Static"),
                () -> PokemonValidator.validateMove("Thunderbolt"),
                () -> PokemonValidator.validateEquippedItem("Light Ball"),
                () -> PokemonValidator.validateNature("Timid"),
                () -> PokemonValidator.validateNickname("Sparky"),
                () -> PokemonValidator.validateGender(Gender.MALE)
            );
        }

        @Test
        void shouldRejectMissingSpecies() {
            assertInvalidFilter(
                "No Pokemon found with species id: 25.",
                () -> PokemonValidator.validateSpecies(false, 25L)
            );
        }

        @Test
        void shouldRejectNullSimpleValues() {
            assertAll(
                () -> assertInvalidFilter(
                    "The location found cannot be empty.",
                    () -> PokemonValidator.validateLocationFound(null)
                ),
                () -> assertInvalidFilter(
                    "The ability cannot be empty.",
                    () -> PokemonValidator.validateAbility(null)
                ),
                () -> assertInvalidFilter(
                    "The move cannot be empty.",
                    () -> PokemonValidator.validateMove(null)
                ),
                () -> assertInvalidFilter(
                    "The equipped item cannot be empty.",
                    () -> PokemonValidator.validateEquippedItem(null)
                ),
                () -> assertInvalidFilter(
                    "The nature cannot be empty.",
                    () -> PokemonValidator.validateNature(null)
                ),
                () -> assertInvalidFilter(
                    "The nickname cannot be empty.",
                    () -> PokemonValidator.validateNickname(null)
                ),
                () -> assertInvalidFilter(
                    "The gender filter cannot be null.",
                    () -> PokemonValidator.validateGender(null)
                )
            );
        }

        @Test
        void shouldRejectBlankTextValues() {
            assertAll(
                () -> assertInvalidFilter(
                    "The location found cannot be empty.",
                    () -> PokemonValidator.validateLocationFound(" ")
                ),
                () -> assertInvalidFilter(
                    "The ability cannot be empty.",
                    () -> PokemonValidator.validateAbility(" ")
                ),
                () -> assertInvalidFilter(
                    "The move cannot be empty.",
                    () -> PokemonValidator.validateMove(" ")
                ),
                () -> assertInvalidFilter(
                    "The equipped item cannot be empty.",
                    () -> PokemonValidator.validateEquippedItem(" ")
                ),
                () -> assertInvalidFilter(
                    "The nature cannot be empty.",
                    () -> PokemonValidator.validateNature(" ")
                ),
                () -> assertInvalidFilter(
                    "The nickname cannot be empty.",
                    () -> PokemonValidator.validateNickname(" ")
                )
            );
        }
    }

    @Nested
    class LevelValidation {

        @ParameterizedTest
        @ValueSource(ints = {1, 50, 100})
        void shouldAcceptValidLevels(int level) {
            assertDoesNotThrow(() -> PokemonValidator.validateLevel(level));
        }

        @ParameterizedTest
        @ValueSource(ints = {0, 101})
        void shouldRejectInvalidLevels(int level) {
            assertInvalidFilter(
                "Levels must be between 1 and 100.",
                () -> PokemonValidator.validateLevel(level)
            );
        }

        @ParameterizedTest
        @CsvSource({"1, 1", "1, 100", "25, 75"})
        void shouldAcceptValidLevelRanges(int minLevel, int maxLevel) {
            assertDoesNotThrow(() -> PokemonValidator.validateLevelBetween(minLevel, maxLevel));
        }

        @Test
        void shouldRejectInvertedLevelRange() {
            assertInvalidFilter(
                "Minimum level (75) cannot be greater than maximum level (25).",
                () -> PokemonValidator.validateLevelBetween(75, 25)
            );
        }
    }

    @Nested
    class EvValidation {

        @ParameterizedTest
        @ValueSource(ints = {0, 16, 32})
        void shouldAcceptValidEvValues(int value) {
            assertDoesNotThrow(() -> PokemonValidator.validateEvRange(value, "HP"));
        }

        @ParameterizedTest
        @ValueSource(ints = {-1, 33})
        void shouldRejectInvalidEvValues(int value) {
            assertInvalidFilter(
                "HP EVs must be between 0 and 32.",
                () -> PokemonValidator.validateEvRange(value, "HP")
            );
        }

        @ParameterizedTest
        @CsvSource({"0, 0", "0, 32", "10, 20"})
        void shouldAcceptValidEvRanges(int minValue, int maxValue) {
            assertDoesNotThrow(() -> PokemonValidator.validateEvRangeBetween(minValue, maxValue, "HP"));
        }

        @Test
        void shouldRejectEvRangeBelowMinimum() {
            assertInvalidFilter(
                "Minimum HP EVs cannot be less than 0.",
                () -> PokemonValidator.validateEvRangeBetween(-1, 20, "HP")
            );
        }

        @Test
        void shouldRejectEvRangeAboveMaximum() {
            assertInvalidFilter(
                "Maximum HP EVs cannot be greater than 32.",
                () -> PokemonValidator.validateEvRangeBetween(0, 33, "HP")
            );
        }

        @Test
        void shouldRejectInvertedEvRange() {
            assertInvalidFilter(
                "Minimum HP EVs (20) cannot be greater than maximum HP EVs (10).",
                () -> PokemonValidator.validateEvRangeBetween(20, 10, "HP")
            );
        }

        @Test
        void shouldIdentifyExactRanges() {
            assertAll(
                () -> assertTrue(PokemonValidator.isExactRange(16, 16)),
                () -> assertFalse(PokemonValidator.isExactRange(15, 16))
            );
        }
    }

    @Nested
    class ResultValidation {

        @Test
        void shouldAcceptNonEmptyResultsForEveryFilter() {
            List<Object> result = List.of(new Object());
            Nature nature = new Nature("Timid", NatureRiseLower.SPEED, NatureRiseLower.ATTACK);

            assertAll(
                () -> PokemonValidator.validateByNicknameResult(result, "Sparky"),
                () -> PokemonValidator.validateByLocationResult(result, "Route 1"),
                () -> PokemonValidator.validateByShinyResult(result),
                () -> PokemonValidator.validateByGenderResult(result, Gender.MALE),
                () -> PokemonValidator.validateByAbilityResult(result, 9L),
                () -> PokemonValidator.validateByMoveResult(result, 85L),
                () -> PokemonValidator.validateByEquippedItemResult(result, 4L),
                () -> PokemonValidator.validateByNatureResult(result, nature),
                () -> PokemonValidator.validateByEvResult(result, "HP", 16),
                () -> PokemonValidator.validateByEvRangeResult(result, "HP", 10, 20)
            );
        }

        @Test
        void shouldRejectEmptyResultsWithSpecificMessages() {
            Nature nature = new Nature("Timid", NatureRiseLower.SPEED, NatureRiseLower.ATTACK);

            assertAll(
                () -> assertPokemonNotFound(
                    "No Pokemon/s found with nickname: Sparky.",
                    () -> PokemonValidator.validateByNicknameResult(List.of(), "Sparky")
                ),
                () -> assertPokemonNotFound(
                    "No Pokemon/s found captured at location Route 1 were found.",
                    () -> PokemonValidator.validateByLocationResult(List.of(), "Route 1")
                ),
                () -> assertPokemonNotFound(
                    "No shiny Pokemon were found.",
                    () -> PokemonValidator.validateByShinyResult(List.of())
                ),
                () -> assertPokemonNotFound(
                    "No Pokemon found with gender: MALE.",
                    () -> PokemonValidator.validateByGenderResult(List.of(), Gender.MALE)
                ),
                () -> assertPokemonNotFound(
                    "No Pokemon found with ability id: 9.",
                    () -> PokemonValidator.validateByAbilityResult(List.of(), 9L)
                ),
                () -> assertPokemonNotFound(
                    "No Pokemon found with move id: 85.",
                    () -> PokemonValidator.validateByMoveResult(List.of(), 85L)
                ),
                () -> assertPokemonNotFound(
                    "No Pokemon found with equipped item id: 4.",
                    () -> PokemonValidator.validateByEquippedItemResult(List.of(), 4L)
                ),
                () -> assertPokemonNotFound(
                    "No Pokemon found with nature: " + nature + ".",
                    () -> PokemonValidator.validateByNatureResult(List.of(), nature)
                ),
                () -> assertPokemonNotFound(
                    "No Pokemon found with HP EVs: 16.",
                    () -> PokemonValidator.validateByEvResult(List.of(), "HP", 16)
                ),
                () -> assertPokemonNotFound(
                    "No Pokemon found with HP EVs between 10 and 20.",
                    () -> PokemonValidator.validateByEvRangeResult(List.of(), "HP", 10, 20)
                ),
                () -> assertPokemonNotFound(
                    "No shiny Pokemon were found.",
                    () -> PokemonValidator.validateByShinyResult(null)
                )
            );
        }
    }

    private static PokemonDto validDto(String move2, String move3, String move4) {
        return new PokemonDto(
            null,
            25L,
            "Sparky",
            "Route 1",
            50,
            false,
            Gender.MALE,
            "Static",
            "Thunderbolt",
            move2,
            move3,
            move4,
            null,
            "Timid",
            0,
            16,
            16,
            32,
            32,
            16
        );
    }

    private static void assertInvalidFilter(String expectedMessage, Executable validation) {
        InvalidFilterValueException exception = assertThrows(InvalidFilterValueException.class, validation);
        assertEquals(expectedMessage, exception.getMessage());
    }

    private static void assertPokemonNotFound(String expectedMessage, Executable validation) {
        PokemonNotFoundException exception = assertThrows(PokemonNotFoundException.class, validation);
        assertEquals(expectedMessage, exception.getMessage());
    }
}
