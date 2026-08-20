package dev.trainerforge.validator;

import java.math.BigDecimal;
import java.util.List;

import dev.trainerforge.exception.InvalidFilterValueException;
import dev.trainerforge.exception.notfound.PokedexNotFoundException;
import dev.trainerforge.model.enumerated.PokemonClass;

public final class PokedexValidator {

    private PokedexValidator() {
    }

    public static void validateNationalPokedex(Long nationalPokedex) {
        validateIdRange(nationalPokedex, ValidationLimits.Identifier.NATIONAL_POKEDEX);
    }

    public static void validateNationalPokedex(Long nationalPokedex, Long maxNationalPokedex) {
        validateIdRange(nationalPokedex, maxNationalPokedex, "National Pokedex number");
    }

    public static void validateByNationalPokedexResult(List<?> result, Long nationalPokedex) {
        validateNonEmpty(result, "Pokedex entry not found with national Pokedex number: #" + nationalPokedex + ".");
    }

    public static void validateGenerationId(Long generationId) {
        validateIdRange(generationId, ValidationLimits.Identifier.GENERATION);
    }

    public static void validateGenerationId(Long generationId, Long maxGenerationId) {
        validateIdRange(generationId, maxGenerationId, "Generation ID");
    }

    public static void validateByGenerationResult(List<?> result, Long generationId) {
        validateNonEmpty(result, "Pokedex entry not found with generation ID: " + generationId + ".");
    }

    public static void validateRegionId(Long regionId) {
        validateIdRange(regionId, ValidationLimits.Identifier.REGION);
    }

    public static void validateRegionId(Long regionId, Long maxRegionId) {
        validateIdRange(regionId, maxRegionId, "Region ID");
    }

    public static void validateByRegionResult(List<?> result, Long regionId) {
        validateNonEmpty(result, "Pokedex entry not found with region ID: " + regionId + ".");
    }

    public static void validateByPokemonClassResult(List<?> result, PokemonClass pokemonClass) {
        validateNonEmpty(result, "Pokedex entry not found with Pokemon class: " + pokemonClass + ".");
    }

    public static void validateTypeId(Long typeId) {
        validateIdRange(typeId, ValidationLimits.Identifier.TYPE);
    }

    public static void validateTypeId(Long typeId, Long maxTypeId) {
        validateIdRange(typeId, maxTypeId, "Type ID");
    }

    public static void validateByTypeResult(List<?> result, Long typeId) {
        validateNonEmpty(result, "Pokedex entry not found with type ID: " + typeId + ".");
    }

    public static void validateAbilityId(Long abilityId) {
        validateIdRange(abilityId, ValidationLimits.Identifier.ABILITY);
    }

    public static void validateAbilityId(Long abilityId, Long maxAbilityId) {
        validateIdRange(abilityId, maxAbilityId, "Ability ID");
    }

    public static void validateByAbilityResult(List<?> result, Long abilityId) {
        validateNonEmpty(result, "Pokedex entry not found with ability ID: " + abilityId + ".");
    }

    public static void validateByCategoryResult(List<?> result, String category) {
        validateNonEmpty(result, "Pokedex entry not found with category: " + category + ".");
    }

    public static void validateWeight(BigDecimal weight) {
        validateBigDecimalRange(weight, weight, ValidationLimits.DecimalRange.WEIGHT);
    }

    public static void validateWeightRange(BigDecimal minWeight, BigDecimal maxWeight) {
        validateBigDecimalRange(minWeight, maxWeight, ValidationLimits.DecimalRange.WEIGHT);
    }

    public static void validateHeight(BigDecimal height) {
        validateBigDecimalRange(height, height, ValidationLimits.DecimalRange.HEIGHT);
    }

    public static void validateHeightRange(BigDecimal minHeight, BigDecimal maxHeight) {
        validateBigDecimalRange(minHeight, maxHeight, ValidationLimits.DecimalRange.HEIGHT);
    }

    private static void validateBigDecimalRange(
        BigDecimal min,
        BigDecimal max,
        ValidationLimits.DecimalRange range
    ) {
        validateBigDecimalRange(min, max, range.min(), range.max(), range.label(), range.unit());
    }

    public static void validateBigDecimalRange(
        BigDecimal min,
        BigDecimal max,
        BigDecimal minBound,
        BigDecimal maxBound,
        String fieldName,
        String unit
    ) {
        if (min == null || max == null) {
            throw new InvalidFilterValueException(fieldName + " values cannot be null.");
        }
        if (min.compareTo(minBound) < 0) {
            throw new InvalidFilterValueException("Minimum " + fieldName + " cannot be less than " + minBound + " " + unit + ".");
        }
        if (max.compareTo(maxBound) > 0) {
            throw new InvalidFilterValueException("Maximum " + fieldName + " cannot be greater than " + maxBound + " " + unit + ".");
        }
        if (min.compareTo(max) > 0) {
            throw new InvalidFilterValueException("Minimum " + fieldName + " (" + min
                + ") cannot be greater than maximum " + fieldName + " (" + max + ") " + unit + ".");
        }
    }

    public static void validateByWeightResult(List<?> result, BigDecimal weight) {
        validateNonEmpty(result, "Pokedex entry not found with weight: " + weight + " kg.");
    }

    public static void validateByHeightResult(List<?> result, BigDecimal height) {
        validateNonEmpty(result, "Pokedex entry not found with height: " + height + " m.");
    }

    public static void validateByWeightRangeResult(List<?> result, BigDecimal minWeight, BigDecimal maxWeight) {
        validateNonEmpty(result, "Pokedex entry not found with weight between " + minWeight + " and " + maxWeight + " kg.");
    }

    public static void validateByHeightRangeResult(List<?> result, BigDecimal minHeight, BigDecimal maxHeight) {
        validateNonEmpty(result, "Pokedex entry not found with height between " + minHeight + " and " + maxHeight + " m.");
    }

    public static void validateStatRange(int value, String statName) {
        validateStatRange(value, statName, ValidationLimits.IntegerRange.BASE_STAT);
    }

    public static void validateStatRange(int value, ValidationLimits.BaseStat stat) {
        validateStatRange(value, stat.label(), ValidationLimits.IntegerRange.BASE_STAT);
    }

    private static void validateStatRange(
        int value,
        String statName,
        ValidationLimits.IntegerRange range
    ) {
        validateStatRange(value, statName, range.min(), range.max());
    }

    public static void validateStatRange(int value, String statName, int minBaseStat, int maxBaseStat) {
        if (value < minBaseStat || value > maxBaseStat) {
            throw new InvalidFilterValueException(statName + " base stat must be between " + minBaseStat + " and " + maxBaseStat + ".");
        }
    }

    public static void validateStatRangeBetween(int minValue, int maxValue, String statName) {
        validateStatRangeBetween(minValue, maxValue, statName, ValidationLimits.IntegerRange.BASE_STAT);
    }

    public static void validateStatRangeBetween(int minValue, int maxValue, ValidationLimits.BaseStat stat) {
        validateStatRangeBetween(minValue, maxValue, stat.label(), ValidationLimits.IntegerRange.BASE_STAT);
    }

    private static void validateStatRangeBetween(
        int minValue,
        int maxValue,
        String statName,
        ValidationLimits.IntegerRange range
    ) {
        validateStatRangeBetween(minValue, maxValue, statName, range.min(), range.max());
    }

    public static void validateStatRangeBetween(int minValue, int maxValue, String statName, int minBaseStat, int maxBaseStat) {
        if (minValue < minBaseStat) {
            throw new InvalidFilterValueException("Minimum " + statName + " cannot be less than " + minBaseStat + ".");
        }
        if (maxValue > maxBaseStat) {
            throw new InvalidFilterValueException("Maximum " + statName + " cannot be greater than " + maxBaseStat + ".");
        }
        if (minValue > maxValue) {
            throw new InvalidFilterValueException("Minimum " + statName + " (" + minValue + ") cannot be greater than maximum " + statName + " (" + maxValue + ").");
        }
    }

    public static void validateByStatResult(List<?> result, String statName, int value) {
        validateByStatResult(result, "Pokedex entry found with " + statName + " base stat: " + value + ".");
    }

    public static void validateByStatResult(List<?> result, ValidationLimits.BaseStat stat, int value) {
        validateByStatResult(result, stat.label(), value);
    }

    public static void validateByStatRangeResult(List<?> result, String statName, int minValue, int maxValue) {
        validateByStatResult(result, "Pokedex entry found with " + statName + " between " + minValue + " and " + maxValue + ".");
    }

    public static void validateByStatRangeResult(
        List<?> result,
        ValidationLimits.BaseStat stat,
        int minValue,
        int maxValue
    ) {
        validateByStatRangeResult(result, stat.label(), minValue, maxValue);
    }

    public static void validateByStatResult(List<?> result, String message) {
        validateNonEmpty(result, message);
    }

    public static void validateByVideogamesResult(List<?> result, Long pokedexId) {
        validateNonEmpty(result, "The Pokemon with the ID: " + pokedexId + " doesn't appear in any videogames.");
    }

    public static boolean isExactRange(BigDecimal min, BigDecimal max) {
        return min.compareTo(max) == 0;
    }

    public static boolean isExactRange(int min, int max) {
        return min == max;
    }

    private static void validateIdRange(Long value, ValidationLimits.Identifier range) {
        if (value == null || value < range.min() || value > range.max()) {
            throw new InvalidFilterValueException(range.label() + " must be between " + range.min() + " and " + range.max() + ".");
        }
    }

    private static void validateIdRange(Long value, Long max, String fieldName) {
        if (value == null || max == null || value < 1L || value > max) {
            throw new InvalidFilterValueException(fieldName + " must be between 1 and " + max + ".");
        }
    }

    private static void validateNonEmpty(List<?> result, String message) {
        if (result == null || result.isEmpty()) {
            throw new PokedexNotFoundException(message);
        }
    }
}
