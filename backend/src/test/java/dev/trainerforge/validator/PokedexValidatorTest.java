package dev.trainerforge.validator;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import dev.trainerforge.exception.InvalidFilterValueException;
import dev.trainerforge.exception.notfound.PokedexNotFoundException;
import dev.trainerforge.model.enumerated.PokemonClass;

class PokedexValidatorTest {

    @Nested
    class IdentifierValidation {

        @Test
        void shouldAcceptConfiguredIdentifierBoundaries() {
            assertAll(
                () -> PokedexValidator.validateNationalPokedex(1L),
                () -> PokedexValidator.validateNationalPokedex(1025L),
                () -> PokedexValidator.validateGenerationId(1L),
                () -> PokedexValidator.validateGenerationId(10L),
                () -> PokedexValidator.validateRegionId(1L),
                () -> PokedexValidator.validateRegionId(10L),
                () -> PokedexValidator.validateTypeId(1L),
                () -> PokedexValidator.validateTypeId(18L),
                () -> PokedexValidator.validateAbilityId(1L),
                () -> PokedexValidator.validateAbilityId(326L)
            );
        }

        @Test
        void shouldRejectIdentifiersOutsideConfiguredRanges() {
            assertAll(
                () -> assertInvalidFilter(
                    "National Pokedex number must be between 1 and 1025.",
                    () -> PokedexValidator.validateNationalPokedex(null)
                ),
                () -> assertInvalidFilter(
                    "National Pokedex number must be between 1 and 1025.",
                    () -> PokedexValidator.validateNationalPokedex(1026L)
                ),
                () -> assertInvalidFilter(
                    "Generation ID must be between 1 and 10.",
                    () -> PokedexValidator.validateGenerationId(11L)
                ),
                () -> assertInvalidFilter(
                    "Region ID must be between 1 and 10.",
                    () -> PokedexValidator.validateRegionId(0L)
                ),
                () -> assertInvalidFilter(
                    "Type ID must be between 1 and 18.",
                    () -> PokedexValidator.validateTypeId(19L)
                ),
                () -> assertInvalidFilter(
                    "Ability ID must be between 1 and 326.",
                    () -> PokedexValidator.validateAbilityId(327L)
                )
            );
        }

        @Test
        void shouldAcceptIdentifiersInsideDynamicMaximums() {
            assertAll(
                () -> PokedexValidator.validateNationalPokedex(25L, 50L),
                () -> PokedexValidator.validateGenerationId(5L, 5L),
                () -> PokedexValidator.validateRegionId(3L, 5L),
                () -> PokedexValidator.validateTypeId(10L, 18L),
                () -> PokedexValidator.validateAbilityId(100L, 200L)
            );
        }

        @Test
        void shouldRejectIdentifiersOutsideDynamicMaximums() {
            assertAll(
                () -> assertInvalidFilter(
                    "National Pokedex number must be between 1 and 50.",
                    () -> PokedexValidator.validateNationalPokedex(null, 50L)
                ),
                () -> assertInvalidFilter(
                    "National Pokedex number must be between 1 and 50.",
                    () -> PokedexValidator.validateNationalPokedex(51L, 50L)
                ),
                () -> assertInvalidFilter(
                    "Generation ID must be between 1 and 5.",
                    () -> PokedexValidator.validateGenerationId(6L, 5L)
                ),
                () -> assertInvalidFilter(
                    "Region ID must be between 1 and null.",
                    () -> PokedexValidator.validateRegionId(1L, null)
                ),
                () -> assertInvalidFilter(
                    "Type ID must be between 1 and 18.",
                    () -> PokedexValidator.validateTypeId(0L, 18L)
                ),
                () -> assertInvalidFilter(
                    "Ability ID must be between 1 and 200.",
                    () -> PokedexValidator.validateAbilityId(201L, 200L)
                )
            );
        }
    }

    @Nested
    class DecimalValidation {

        @Test
        void shouldAcceptWeightAndHeightBoundaries() {
            assertAll(
                () -> PokedexValidator.validateWeight(decimal("0.1")),
                () -> PokedexValidator.validateWeight(decimal("999.9")),
                () -> PokedexValidator.validateHeight(decimal("0.1")),
                () -> PokedexValidator.validateHeight(decimal("100.0"))
            );
        }

        @Test
        void shouldRejectInvalidWeightAndHeightValues() {
            assertAll(
                () -> assertInvalidFilter(
                    "Weight values cannot be null.",
                    () -> PokedexValidator.validateWeight(null)
                ),
                () -> assertInvalidFilter(
                    "Minimum Weight cannot be less than 0.1 kg.",
                    () -> PokedexValidator.validateWeight(decimal("0.0"))
                ),
                () -> assertInvalidFilter(
                    "Maximum Weight cannot be greater than 999.9 kg.",
                    () -> PokedexValidator.validateWeight(decimal("1000.0"))
                ),
                () -> assertInvalidFilter(
                    "Minimum Height cannot be less than 0.1 m.",
                    () -> PokedexValidator.validateHeight(decimal("0.0"))
                ),
                () -> assertInvalidFilter(
                    "Maximum Height cannot be greater than 100.0 m.",
                    () -> PokedexValidator.validateHeight(decimal("100.1"))
                )
            );
        }

        @Test
        void shouldAcceptValidWeightAndHeightRanges() {
            assertAll(
                () -> PokedexValidator.validateWeightRange(decimal("0.1"), decimal("999.9")),
                () -> PokedexValidator.validateWeightRange(decimal("25.0"), decimal("25.0")),
                () -> PokedexValidator.validateHeightRange(decimal("0.1"), decimal("100.0")),
                () -> PokedexValidator.validateHeightRange(decimal("1.7"), decimal("1.7"))
            );
        }

        @Test
        void shouldRejectNullOutOfBoundsOrInvertedDecimalRanges() {
            assertAll(
                () -> assertInvalidFilter(
                    "Weight values cannot be null.",
                    () -> PokedexValidator.validateWeightRange(null, decimal("1.0"))
                ),
                () -> assertInvalidFilter(
                    "Minimum Weight cannot be less than 0.1 kg.",
                    () -> PokedexValidator.validateWeightRange(decimal("0.0"), decimal("1.0"))
                ),
                () -> assertInvalidFilter(
                    "Maximum Weight cannot be greater than 999.9 kg.",
                    () -> PokedexValidator.validateWeightRange(decimal("1.0"), decimal("1000.0"))
                ),
                () -> assertInvalidFilter(
                    "Minimum Weight (20.0) cannot be greater than maximum Weight (10.0) kg.",
                    () -> PokedexValidator.validateWeightRange(decimal("20.0"), decimal("10.0"))
                ),
                () -> assertInvalidFilter(
                    "Height values cannot be null.",
                    () -> PokedexValidator.validateHeightRange(decimal("1.0"), null)
                ),
                () -> assertInvalidFilter(
                    "Minimum Height (2.0) cannot be greater than maximum Height (1.0) m.",
                    () -> PokedexValidator.validateHeightRange(decimal("2.0"), decimal("1.0"))
                )
            );
        }

        @Test
        void shouldValidateCustomDecimalRange() {
            assertDoesNotThrow(() -> PokedexValidator.validateBigDecimalRange(
                decimal("2.0"),
                decimal("8.0"),
                decimal("1.0"),
                decimal("10.0"),
                "Damage",
                "points"
            ));

            assertInvalidFilter(
                "Maximum Damage cannot be greater than 10.0 points.",
                () -> PokedexValidator.validateBigDecimalRange(
                    decimal("2.0"),
                    decimal("11.0"),
                    decimal("1.0"),
                    decimal("10.0"),
                    "Damage",
                    "points"
                )
            );
        }

        @Test
        void shouldIdentifyExactDecimalRangesIgnoringScale() {
            assertAll(
                () -> assertTrue(PokedexValidator.isExactRange(decimal("1.0"), decimal("1.00"))),
                () -> assertFalse(PokedexValidator.isExactRange(decimal("1.0"), decimal("1.1")))
            );
        }
    }

    @Nested
    class StatValidation {

        @Test
        void shouldAcceptStatBoundariesThroughAllOverloads() {
            assertAll(
                () -> PokedexValidator.validateStatRange(5, "HP"),
                () -> PokedexValidator.validateStatRange(255, ValidationLimits.BaseStat.ATTACK),
                () -> PokedexValidator.validateStatRange(50, "Custom", 10, 100)
            );
        }

        @Test
        void shouldRejectStatsOutsideAllowedRanges() {
            assertAll(
                () -> assertInvalidFilter(
                    "HP base stat must be between 5 and 255.",
                    () -> PokedexValidator.validateStatRange(4, "HP")
                ),
                () -> assertInvalidFilter(
                    "Attack base stat must be between 5 and 255.",
                    () -> PokedexValidator.validateStatRange(256, ValidationLimits.BaseStat.ATTACK)
                ),
                () -> assertInvalidFilter(
                    "Custom base stat must be between 10 and 100.",
                    () -> PokedexValidator.validateStatRange(101, "Custom", 10, 100)
                )
            );
        }

        @Test
        void shouldAcceptValidStatRangesThroughAllOverloads() {
            assertAll(
                () -> PokedexValidator.validateStatRangeBetween(5, 255, "HP"),
                () -> PokedexValidator.validateStatRangeBetween(50, 50, ValidationLimits.BaseStat.SPEED),
                () -> PokedexValidator.validateStatRangeBetween(10, 100, "Custom", 10, 100)
            );
        }

        @Test
        void shouldRejectInvalidStatRanges() {
            assertAll(
                () -> assertInvalidFilter(
                    "Minimum HP cannot be less than 5.",
                    () -> PokedexValidator.validateStatRangeBetween(4, 100, "HP")
                ),
                () -> assertInvalidFilter(
                    "Maximum Speed cannot be greater than 255.",
                    () -> PokedexValidator.validateStatRangeBetween(5, 256, ValidationLimits.BaseStat.SPEED)
                ),
                () -> assertInvalidFilter(
                    "Minimum Custom (80) cannot be greater than maximum Custom (20).",
                    () -> PokedexValidator.validateStatRangeBetween(80, 20, "Custom", 10, 100)
                )
            );
        }

        @Test
        void shouldIdentifyExactIntegerRanges() {
            assertAll(
                () -> assertTrue(PokedexValidator.isExactRange(50, 50)),
                () -> assertFalse(PokedexValidator.isExactRange(50, 51))
            );
        }
    }

    @Nested
    class ResultValidation {

        @Test
        void shouldAcceptNonEmptyResultsForEveryFilter() {
            List<Object> result = List.of(new Object());

            assertAll(
                () -> PokedexValidator.validateByNationalPokedexResult(result, 25L),
                () -> PokedexValidator.validateByGenerationResult(result, 1L),
                () -> PokedexValidator.validateByRegionResult(result, 1L),
                () -> PokedexValidator.validateByPokemonClassResult(result, PokemonClass.COMMON),
                () -> PokedexValidator.validateByTypeResult(result, 13L),
                () -> PokedexValidator.validateByAbilityResult(result, 9L),
                () -> PokedexValidator.validateByCategoryResult(result, "Mouse"),
                () -> PokedexValidator.validateByWeightResult(result, decimal("6.0")),
                () -> PokedexValidator.validateByHeightResult(result, decimal("0.4")),
                () -> PokedexValidator.validateByWeightRangeResult(result, decimal("5.0"), decimal("7.0")),
                () -> PokedexValidator.validateByHeightRangeResult(result, decimal("0.3"), decimal("0.5")),
                () -> PokedexValidator.validateByStatResult(result, "HP", 35),
                () -> PokedexValidator.validateByStatResult(result, ValidationLimits.BaseStat.ATTACK, 55),
                () -> PokedexValidator.validateByStatRangeResult(result, "HP", 30, 40),
                () -> PokedexValidator.validateByStatRangeResult(
                    result,
                    ValidationLimits.BaseStat.SPEED,
                    80,
                    100
                ),
                () -> PokedexValidator.validateByStatResult(result, "Custom message"),
                () -> PokedexValidator.validateByVideogamesResult(result, 25L)
            );
        }

        @Test
        void shouldRejectEmptyResultsWithSpecificMessages() {
            assertAll(
                () -> assertPokedexNotFound(
                    "Pokedex entry not found with national Pokedex number: #25.",
                    () -> PokedexValidator.validateByNationalPokedexResult(List.of(), 25L)
                ),
                () -> assertPokedexNotFound(
                    "Pokedex entry not found with generation ID: 1.",
                    () -> PokedexValidator.validateByGenerationResult(List.of(), 1L)
                ),
                () -> assertPokedexNotFound(
                    "Pokedex entry not found with region ID: 1.",
                    () -> PokedexValidator.validateByRegionResult(List.of(), 1L)
                ),
                () -> assertPokedexNotFound(
                    "Pokedex entry not found with Pokemon class: COMMON.",
                    () -> PokedexValidator.validateByPokemonClassResult(List.of(), PokemonClass.COMMON)
                ),
                () -> assertPokedexNotFound(
                    "Pokedex entry not found with type ID: 13.",
                    () -> PokedexValidator.validateByTypeResult(List.of(), 13L)
                ),
                () -> assertPokedexNotFound(
                    "Pokedex entry not found with ability ID: 9.",
                    () -> PokedexValidator.validateByAbilityResult(List.of(), 9L)
                ),
                () -> assertPokedexNotFound(
                    "Pokedex entry not found with category: Mouse.",
                    () -> PokedexValidator.validateByCategoryResult(List.of(), "Mouse")
                ),
                () -> assertPokedexNotFound(
                    "Pokedex entry not found with weight: 6.0 kg.",
                    () -> PokedexValidator.validateByWeightResult(List.of(), decimal("6.0"))
                ),
                () -> assertPokedexNotFound(
                    "Pokedex entry not found with height: 0.4 m.",
                    () -> PokedexValidator.validateByHeightResult(List.of(), decimal("0.4"))
                ),
                () -> assertPokedexNotFound(
                    "Pokedex entry not found with weight between 5.0 and 7.0 kg.",
                    () -> PokedexValidator.validateByWeightRangeResult(
                        List.of(),
                        decimal("5.0"),
                        decimal("7.0")
                    )
                ),
                () -> assertPokedexNotFound(
                    "Pokedex entry not found with height between 0.3 and 0.5 m.",
                    () -> PokedexValidator.validateByHeightRangeResult(
                        List.of(),
                        decimal("0.3"),
                        decimal("0.5")
                    )
                ),
                () -> assertPokedexNotFound(
                    "Pokedex entry found with HP base stat: 35.",
                    () -> PokedexValidator.validateByStatResult(List.of(), "HP", 35)
                ),
                () -> assertPokedexNotFound(
                    "Pokedex entry found with Attack base stat: 55.",
                    () -> PokedexValidator.validateByStatResult(
                        List.of(),
                        ValidationLimits.BaseStat.ATTACK,
                        55
                    )
                ),
                () -> assertPokedexNotFound(
                    "Pokedex entry found with HP between 30 and 40.",
                    () -> PokedexValidator.validateByStatRangeResult(List.of(), "HP", 30, 40)
                ),
                () -> assertPokedexNotFound(
                    "Pokedex entry found with Speed between 80 and 100.",
                    () -> PokedexValidator.validateByStatRangeResult(
                        List.of(),
                        ValidationLimits.BaseStat.SPEED,
                        80,
                        100
                    )
                ),
                () -> assertPokedexNotFound(
                    "Custom message",
                    () -> PokedexValidator.validateByStatResult(List.of(), "Custom message")
                ),
                () -> assertPokedexNotFound(
                    "The Pokemon with the ID: 25 doesn't appear in any videogames.",
                    () -> PokedexValidator.validateByVideogamesResult(List.of(), 25L)
                ),
                () -> assertPokedexNotFound(
                    "Pokedex entry not found with generation ID: 1.",
                    () -> PokedexValidator.validateByGenerationResult(null, 1L)
                )
            );
        }
    }

    private static BigDecimal decimal(String value) {
        return new BigDecimal(value);
    }

    private static void assertInvalidFilter(String expectedMessage, Executable validation) {
        InvalidFilterValueException exception = assertThrows(InvalidFilterValueException.class, validation);
        assertEquals(expectedMessage, exception.getMessage());
    }

    private static void assertPokedexNotFound(String expectedMessage, Executable validation) {
        PokedexNotFoundException exception = assertThrows(PokedexNotFoundException.class, validation);
        assertEquals(expectedMessage, exception.getMessage());
    }
}
