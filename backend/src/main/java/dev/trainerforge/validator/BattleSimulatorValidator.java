package dev.trainerforge.validator;

import java.util.List;

import dev.trainerforge.exception.InvalidFilterValueException;
import dev.trainerforge.model.entities.Pokemon;

public final class BattleSimulatorValidator {

    private BattleSimulatorValidator() {
    }

    public static void validateTeamId(Long id, String label) {
        if (id == null) {
            throw new InvalidFilterValueException("Invalid team ID for " + label + ".");
        }
    }

    public static void validateTeamStructure(List<Pokemon> teamOne, List<Pokemon> teamTwo) {
        validateTeamSize(teamOne, "Team one");
        validateTeamSize(teamTwo, "Team two");

        if (teamOne.size() != teamTwo.size()) {
            throw new InvalidFilterValueException(
                "Both teams must have the same number of Pokemon to simulate a battle."
            );
        }
    }

    private static void validateTeamSize(List<Pokemon> team, String label) {
        int teamSize = team.size();
        ValidationLimits.IntegerRange range = ValidationLimits.IntegerRange.TEAM_SIZE;

        if (teamSize < range.min() || teamSize > range.max()) {
            throw new InvalidFilterValueException(
                label + " must have between " + range.min() + " and " + range.max() + " Pokemon."
            );
        }
    }
}
