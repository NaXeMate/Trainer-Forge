package dev.trainerforge.service;

import java.util.Comparator;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import dev.trainerforge.exception.InvalidFilterValueException;
import dev.trainerforge.model.entities.Pokedex;
import dev.trainerforge.model.entities.Pokemon;
import dev.trainerforge.model.entities.PokemonTeam;
import dev.trainerforge.model.entities.PokemonType;
import dev.trainerforge.model.entities.Team;
import dev.trainerforge.repository.TypeEffectivenessRepository;

@Transactional(readOnly = true)
@Service
public class BattleSimulatorService {

    private final TeamService teamService;
    private final TypeEffectivenessRepository typeEffectivenessRepo;

    private static final double LEVEL_WEIGHT      = 0.6;
    private static final double BASE_STATS_WEIGHT = 0.4;
    private static final double EV_BONUS_MAX      = 0.2;
    private static final int    STAT_EV_MAX       = 32; // Pokemon Champions EV system
    private static final int    EV_STATS_COUNT    = 6;
    private static final double DRAW_EPSILON      = 0.01;
    private static final int    TEAM_MAX_SIZE     = 6;
    private static final int    TEAM_MIN_SIZE     = 1;
    private static final double TYPE_ADVANTAGE_BONUS = 0.2;

    public BattleSimulatorService(TeamService teamService, TypeEffectivenessRepository typeEffectivenessRepo) {
        this.teamService = teamService;
        this.typeEffectivenessRepo = typeEffectivenessRepo;
    }

    // SIMULATION

    /**
     * Simulates a battle outcome between two teams of equal size.
     *
     * Compares pokemon by slot position, so both teams must have matching roster sizes.
     *
     * @param teamId1 identifier of the first team in the matchup.
     * @param teamId2 identifier of the second team in the matchup.
     * @return a formatted battle summary including side scores, winner, and confidence percentage.
     * @throws InvalidFilterValueException when a team identifier is null or team structures are incompatible for simulation.
     */
    public String simulateBattle(Long teamId1, Long teamId2) {
        validateTeamId(teamId1, "team one");
        validateTeamId(teamId2, "team two");

        Team teamOne = teamService.findById(teamId1);
        Team teamTwo = teamService.findById(teamId2);

        List<Pokemon> teamOnePokemons = extractTeamPokemons(teamOne);
        List<Pokemon> teamTwoPokemons = extractTeamPokemons(teamTwo);

        validateTeamStructure(teamOnePokemons, teamTwoPokemons);

        int teamSize = teamOnePokemons.size();
        String battleMode = teamSize + " vs. " + teamSize;

        double teamOneScore = calculateSideScore(teamOnePokemons, teamTwoPokemons);
        double teamTwoScore = calculateSideScore(teamTwoPokemons, teamOnePokemons);

        return buildResultMessage(battleMode, teamOne.getName(), teamOneScore, teamTwo.getName(), teamTwoScore);
    }

    // SCORING

    /**
     * Calculates the side score as the average of per-slot matchup scores.
     *
     * @param attackers ordered pokemon list for the evaluated side.
     * @param defenders ordered pokemon list for the opposing side.
     * @return average score obtained by comparing each pokemon against the opponent in the same slot.
     */
    private double calculateSideScore(List<Pokemon> attackers, List<Pokemon> defenders) {
        double totalScore = 0.0;
        int teamSize = attackers.size();

        for (int index = 0; index < teamSize; index++) {
            Pokemon attacker = attackers.get(index);
            Pokemon defender = defenders.get(index);
            totalScore += calculatePokemonScore(attacker, defender);
        }

        return totalScore / teamSize;
    }

    /**
     * Calculates an individual matchup score by applying type effectiveness over base score.
     *
     * @param attacker pokemon whose score is being computed.
     * @param defender opposing pokemon used to derive the type modifier.
     * @return final attacker score for this one-on-one slot matchup.
     */
    private double calculatePokemonScore(Pokemon attacker, Pokemon defender) {
        double baseScore = calculatePokemonBaseScore(attacker);
        double typeModifier = calculateOffensiveTypeModifier(attacker, defender);
        return baseScore * typeModifier;
    }

    /**
     * Derives a pokemon base score from species stats, level scaling, and EV scaling.
     *
     * Applies level and stat factors as additive bonuses over the species base-stat sum so low-level pokemon still produce a non-zero baseline.
     *
     * @param pokemon pokemon whose intrinsic score baseline is being calculated.
     * @return base score before matchup type modifiers are applied.
     */
    private double calculatePokemonBaseScore(Pokemon pokemon) {
        Pokedex species = pokemon.getSpecies();

        int baseStatsSum = calculateBaseStatsSum(species);
        double levelFactor = calculateLevelFactor(pokemon.getLevel());
        double statsFactor = calculateBaseStatsFactor(baseStatsSum);
        double evFactor = calculateEvFactor(pokemon);

        return baseStatsSum * (1.0 + levelFactor + statsFactor) * evFactor;
    }

    private int calculateBaseStatsSum(Pokedex species) {
        return species.getHpBase()
            + species.getAttackBase()
            + species.getDefenseBase()
            + species.getSpecialAttackBase()
            + species.getSpecialDefenseBase()
            + species.getSpeedBase();
    }

    private double calculateLevelFactor(int level) {
        return (level / 100.0) * LEVEL_WEIGHT;
    }

    private double calculateBaseStatsFactor(int baseStatsSum) {
        return (baseStatsSum / 600.0) * BASE_STATS_WEIGHT;
    }

    private double calculateEvFactor(Pokemon pokemon) {
        int totalEVs = calculateTotalEVs(pokemon);
        double evRatio = totalEVs / (double) (STAT_EV_MAX * EV_STATS_COUNT);

        return 1.0 + (EV_BONUS_MAX * evRatio);
    }

    private int calculateTotalEVs(Pokemon pokemon) {
        return pokemon.getHpEv()
            + pokemon.getAttackEv()
            + pokemon.getDefenseEv()
            + pokemon.getSpecialAttackEv()
            + pokemon.getSpecialDefenseEv()
            + pokemon.getSpeedEv();
    }

    /**
     * Computes the offensive type modifier using all attacker-type versus defender-type pairs.
     *
     * Queries type multipliers from persistence. Any disadvantage immediately forces a 0.5 penalty, while each advantage adds a fixed bonus over 1.0.
     *
     * @param attacker attacking pokemon whose species types are evaluated.
     * @param defender defending pokemon whose species types are evaluated.
     * @return aggregated offensive modifier for the attacker against the defender.
     */
    private double calculateOffensiveTypeModifier(Pokemon attacker, Pokemon defender) {
        List<String> attackerTypes = getTypeNames(attacker.getSpecies());
        List<String> defenderTypes = getTypeNames(defender.getSpecies());

        if (attackerTypes.isEmpty() || defenderTypes.isEmpty()) {
            return 1.0;
        }

        int advantageCount = 0;

        for (String attackerType : attackerTypes) {
            for (String defenderType : defenderTypes) {
                double multiplier = findTypeMultiplier(attackerType, defenderType);

                if (isTypeDisadvantage(multiplier)) {
                    return 0.5;
                }

                if (isTypeAdvantage(multiplier)) {
                    advantageCount++;
                }
            }
        }

        return 1.0 + (advantageCount * TYPE_ADVANTAGE_BONUS);
    }

    private List<String> getTypeNames(Pokedex species) {
        List<String> typeNames = new ArrayList<>();

        addTypeName(typeNames, species.getType1());
        addTypeName(typeNames, species.getType2());

        return typeNames;
    }

    private void addTypeName(List<String> typeNames, PokemonType type) {
        if (Objects.isNull(type)) {
            return;
        }

        String normalizedName = normalizeTypeName(type.getName());
        if (normalizedName != null) {
            typeNames.add(normalizedName);
        }
    }

    private double findTypeMultiplier(String attackerType, String defenderType) {
        return typeEffectivenessRepo
            .findByAttackingType_NameAndDefendingType_Name(attackerType, defenderType)
            .map(te -> te.getMultiplier().doubleValue())
            .orElse(1.0);
    }

    private boolean isTypeAdvantage(double multiplier) {
        return multiplier > 1.0;
    }

    private boolean isTypeDisadvantage(double multiplier) {
        return multiplier < 1.0;
    }

    private String normalizeTypeName(String name) {
        if (name == null) {
            return null;
        }
        return name.trim();
    }

    // RESULT CONSTRUCTION

    private String buildResultMessage(String mode, String sideOneName, double sideOneScore, String sideTwoName, double sideTwoScore) {
        String winner = resolveWinnerName(sideOneName, sideTwoName, sideOneScore, sideTwoScore);
        double confidence = calculateConfidence(sideOneScore, sideTwoScore);
        double sideOneRoundedScore = roundTwo(sideOneScore);
        double sideTwoRoundedScore = roundTwo(sideTwoScore);
        double confidencePercentage = roundTwo(confidence * 100);

        return "[Team Battle " + mode + "] "
            + sideOneName + " (score: " + sideOneRoundedScore + ")"
            + " vs "
            + sideTwoName + " (score: " + sideTwoRoundedScore + ")"
            + " — Winner: " + winner
            + " (confidence: " + confidencePercentage + "%).";
    }

    private String resolveWinnerName(String sideOneName, String sideTwoName, double sideOneScore, double sideTwoScore) {
        if (Math.abs(sideOneScore - sideTwoScore) < DRAW_EPSILON) {
            return "Draw";
        }
        return sideOneScore > sideTwoScore ? sideOneName : sideTwoName;
    }

    private double calculateConfidence(double sideOneScore, double sideTwoScore) {
        double total = sideOneScore + sideTwoScore;
        if (total == 0.0) {
            return 0.0;
        }
        return Math.abs(sideOneScore - sideTwoScore) / total;
    }

    // TEAM MANAGEMENT

    /**
     * Extracts team pokemon ordered by their configured slot position.
     *
     * @param team team entity containing slot associations.
     * @return ordered pokemon list ready for slot-by-slot simulation.
     */
    private List<Pokemon> extractTeamPokemons(Team team) {
        return team.getPokemonTeams().stream()
            .sorted(Comparator.comparingInt(PokemonTeam::getPosition))
            .map(PokemonTeam::getPokemon)
            .toList();
    }

    /**
     * Validates team size bounds and enforces equal roster sizes for both sides.
     *
     * @param teamOne pokemon list for the first side.
     * @param teamTwo pokemon list for the second side.
     * @throws InvalidFilterValueException when either team is out of bounds or both team sizes differ.
     */
    private void validateTeamStructure(List<Pokemon> teamOne, List<Pokemon> teamTwo) {
        validateTeamSize(teamOne, "Team one");
        validateTeamSize(teamTwo, "Team two");

        int sizeOne = teamOne.size();
        int sizeTwo = teamTwo.size();

        if (sizeOne != sizeTwo) {
            throw new InvalidFilterValueException(
                "Both teams must have the same number of Pokémon to simulate a battle."
            );
        }
    }

    private void validateTeamSize(List<Pokemon> team, String label) {
        int teamSize = team.size();

        if (teamSize < TEAM_MIN_SIZE || teamSize > TEAM_MAX_SIZE) {
            throw new InvalidFilterValueException(
                label + " must have between " + TEAM_MIN_SIZE + " and " + TEAM_MAX_SIZE + " Pokémon."
            );
        }
    }

    // BASIC VALIDATION

    private void validateTeamId(Long id, String label) {
        if (id == null) {
            throw new InvalidFilterValueException("Invalid team ID for " + label + ".");
        }
    }

    // UTILITIES

    private double roundTwo(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}