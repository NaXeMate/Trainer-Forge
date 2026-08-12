package dev.trainerforge.validator;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Stream;

import dev.trainerforge.dto.response.PokemonDto;
import dev.trainerforge.exception.InvalidFilterValueException;
import dev.trainerforge.exception.notfound.PokemonNotFoundException;
import dev.trainerforge.model.entities.Nature;
import dev.trainerforge.model.enumerated.Gender;

public final class PokemonValidator {

    private PokemonValidator() {
    }

    public static void validatePokemonFromDto(PokemonDto dto, boolean speciesExists) {
        validateSpecies(speciesExists, dto.species());
        validateLocationFound(dto.locationFound());
        validateLevel(dto.level());
        validateAbility(dto.ability());
        validateMove(dto.move1());

        if (dto.move2() != null && !dto.move2().isBlank()) {
            validateMove(dto.move2());
        }
        if (dto.move3() != null && !dto.move3().isBlank()) {
            validateMove(dto.move3());
        }
        if (dto.move4() != null && !dto.move4().isBlank()) {
            validateMove(dto.move4());
        }
        if (dto.equippedItem() != null && !dto.equippedItem().isBlank()) {
            validateEquippedItem(dto.equippedItem());
        }

        validateNature(dto.nature());
        validateEvRange(dto.hpEv(), "HP");
        validateEvRange(dto.attackEv(), "Attack");
        validateEvRange(dto.defenseEv(), "Defense");
        validateEvRange(dto.specialAttackEv(), "Special Attack");
        validateEvRange(dto.specialDefenseEv(), "Special Defense");
        validateEvRange(dto.speedEv(), "Speed");

        List<String> moves = Stream.of(dto.move1(), dto.move2(), dto.move3(), dto.move4())
            .filter(move -> move != null && !move.isBlank())
            .toList();
        if (moves.size() != new HashSet<>(moves).size()) {
            throw new InvalidFilterValueException("All moves must be different from each other.");
        }
    }

    public static void validateSpecies(boolean exists, Long speciesId) {
        if (!exists) {
            throw new InvalidFilterValueException("No Pokemon found with species id: " + speciesId + ".");
        }
    }

    public static void validateLocationFound(String locationFound) {
        if (locationFound == null || locationFound.isBlank()) {
            throw new InvalidFilterValueException("The location found cannot be empty.");
        }
    }

    public static void validateLevel(int level) {
        ValidationLimits.IntegerRange range = ValidationLimits.IntegerRange.LEVEL;
        if (level < range.min() || level > range.max()) {
            throw new InvalidFilterValueException(
                "Levels must be between " + range.min() + " and " + range.max() + ".");
        }
    }

    public static void validateLevelBetween(int minLevel, int maxLevel) {
        validateLevel(minLevel);
        validateLevel(maxLevel);
        if (minLevel > maxLevel) {
            throw new InvalidFilterValueException(
                "Minimum level (" + minLevel + ") cannot be greater than maximum level (" + maxLevel + ").");
        }
    }

    public static void validateAbility(String ability) {
        if (ability == null || ability.isBlank()) {
            throw new InvalidFilterValueException("The ability cannot be empty.");
        }
    }

    public static void validateMove(String move) {
        if (move == null || move.isBlank()) {
            throw new InvalidFilterValueException("The move cannot be empty.");
        }
    }

    public static void validateEquippedItem(String equippedItem) {
        if (equippedItem == null || equippedItem.isBlank()) {
            throw new InvalidFilterValueException("The equipped item cannot be empty.");
        }
    }

    public static void validateNature(String nature) {
        if (nature == null || nature.isBlank()) {
            throw new InvalidFilterValueException("The nature cannot be empty.");
        }
    }

    public static void validateNickname(String nickname) {
        if (nickname == null || nickname.isBlank()) {
            throw new InvalidFilterValueException("The nickname cannot be empty.");
        }
    }

    public static void validateGender(Gender gender) {
        if (gender == null) {
            throw new InvalidFilterValueException("The gender filter cannot be null.");
        }
    }

    public static void validateEvRange(int value, String statName) {
        ValidationLimits.IntegerRange range = ValidationLimits.IntegerRange.EV;
        if (value < range.min() || value > range.max()) {
            throw new InvalidFilterValueException(
                statName + " EVs must be between " + range.min() + " and " + range.max() + ".");
        }
    }

    public static void validateEvRangeBetween(int minValue, int maxValue, String statName) {
        ValidationLimits.IntegerRange range = ValidationLimits.IntegerRange.EV;
        if (minValue < range.min()) {
            throw new InvalidFilterValueException(
                "Minimum " + statName + " EVs cannot be less than " + range.min() + ".");
        }
        if (maxValue > range.max()) {
            throw new InvalidFilterValueException(
                "Maximum " + statName + " EVs cannot be greater than " + range.max() + ".");
        }
        if (minValue > maxValue) {
            throw new InvalidFilterValueException(
                "Minimum " + statName + " EVs (" + minValue + ") cannot be greater than maximum "
                    + statName + " EVs (" + maxValue + ").");
        }
    }

    public static boolean isExactRange(int min, int max) {
        return min == max;
    }

    public static void validateByNicknameResult(List<?> result, String nickname) {
        validateResult(result, "No Pokemon/s found with nickname: " + nickname + ".");
    }

    public static void validateByLocationResult(List<?> result, String locationFound) {
        validateResult(result, "No Pokemon/s found captured at location " + locationFound + " were found.");
    }

    public static void validateByShinyResult(List<?> result) {
        validateResult(result, "No shiny Pokemon were found.");
    }

    public static void validateByGenderResult(List<?> result, Gender gender) {
        validateResult(result, "No Pokemon found with gender: " + gender + ".");
    }

    public static void validateByAbilityResult(List<?> result, Long abilityId) {
        validateResult(result, "No Pokemon found with ability id: " + abilityId + ".");
    }

    public static void validateByMoveResult(List<?> result, Long moveId) {
        validateResult(result, "No Pokemon found with move id: " + moveId + ".");
    }

    public static void validateByEquippedItemResult(List<?> result, Long itemId) {
        validateResult(result, "No Pokemon found with equipped item id: " + itemId + ".");
    }

    public static void validateByNatureResult(List<?> result, Nature nature) {
        validateResult(result, "No Pokemon found with nature: " + nature + ".");
    }

    public static void validateByEvResult(List<?> result, String statName, int value) {
        validateResult(result, "No Pokemon found with " + statName + " EVs: " + value + ".");
    }

    public static void validateByEvRangeResult(List<?> result, String statName, int minValue, int maxValue) {
        validateResult(result, "No Pokemon found with " + statName + " EVs between " + minValue + " and " + maxValue + ".");
    }

    private static void validateResult(List<?> result, String message) {
        if (result == null || result.isEmpty()) {
            throw new PokemonNotFoundException(message);
        }
    }
}
