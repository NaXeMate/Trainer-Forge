package dev.trainerforge.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import dev.trainerforge.dto.response.TeamDto.BattleInput;
import dev.trainerforge.dto.response.TeamDto.BattleResult;
import dev.trainerforge.dto.response.TeamDto.BattleResult.Outcome;
import dev.trainerforge.exception.InvalidFilterValueException;
import dev.trainerforge.model.entities.Pokedex;
import dev.trainerforge.model.entities.Pokemon;
import dev.trainerforge.model.entities.PokemonType;
import dev.trainerforge.model.entities.Team;
import dev.trainerforge.repository.TypeEffectivenessRepository;
import dev.trainerforge.validator.BattleSimulatorValidator;
import dev.trainerforge.validator.ValidationLimits;

@Transactional(readOnly = true)
@Service
public class BattleSimulatorService {

    private final TeamService teamService;
    private final TypeEffectivenessRepository typeEffectivenessRepo;

    private static final double LEVEL_WEIGHT      = 0.6;
    private static final double BASE_STATS_WEIGHT = 0.4;
    private static final double EV_BONUS_MAX      = 0.2;
    private static final int    EV_STATS_COUNT    = 6;
    private static final double DRAW_EPSILON      = 0.01;
    private static final double TYPE_ADVANTAGE_BONUS = 0.2;
    // The IDs and owners are the demo pairs inserted by migration V4.
    private static final Map<Long, String> DEMO_OPPONENTS = Map.of(1L, "red", 2L, "leaf", 3L, "liko", 4L, "may", 5L, "rod");

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
        BattleSimulatorValidator.validateTeamId(teamId1, "team one");
        BattleSimulatorValidator.validateTeamId(teamId2, "team two");

        Team teamOne = teamService.findById(teamId1);
        Team teamTwo = teamService.findById(teamId2);

        return score(savedLineup(teamOne), savedLineup(teamTwo)).summary();
    }

    public BattleResult simulateBattle(BattleInput input) {
        if (input == null) throw new InvalidFilterValueException("Choose two teams.");
        validateSelection(input.one());
        validateSelection(input.two());
        if (input.one().draft() != null && input.two().draft() != null) {
            throw new InvalidFilterValueException("Only one session draft can enter a battle.");
        }
        if (input.one().teamId() != null && input.one().teamId().equals(input.two().teamId())) {
            throw new InvalidFilterValueException("Choose two different teams.");
        }

        String owner = teamService.getCurrentUsername();
        return score(resolveSelection(input.one(), owner, false), resolveSelection(input.two(), owner, true));
    }

    private record Lineup(String name, List<Pokemon> members) {}

    private void validateSelection(BattleInput.Side side) {
        if (side == null || (side.teamId() == null) == (side.draft() == null)
                || (side.teamId() != null && side.teamId() <= 0)) {
            throw new InvalidFilterValueException("Each side must select a saved team or the session draft.");
        }
    }

    private Lineup resolveSelection(BattleInput.Side side, String owner, boolean opponent) {
        if (side.draft() != null) {
            String name = side.draft().name();
            if (name != null && name.strip().length() > 100) throw new InvalidFilterValueException("The draft name is too long.");
            var members = teamService.prepareLineup(side.draft()).stream().filter(Objects::nonNull).toList();
            return new Lineup(name == null || name.isBlank() ? "Session draft" : name.strip(), members);
        }
        Team team = teamService.findById(side.teamId());
        String teamOwner = team.getTrainer().getUsername();
        boolean demoOpponent = opponent && !team.isHidden() && teamOwner.equals(DEMO_OPPONENTS.get(team.getId()));
        if (!owner.equals(teamOwner) && !demoOpponent) {
            throw new InvalidFilterValueException(opponent ? "Choose one of your own teams or a demo team for P2."
                    : "Choose one of your own teams for P1.");
        }
        Lineup lineup = savedLineup(team);
        TeamService.validateMonotype(team.getModality(), lineup.members());
        return lineup;
    }

    private Lineup savedLineup(Team team) {
        return new Lineup(team.getName(), extractTeamPokemons(team));
    }

    private BattleResult score(Lineup first, Lineup second) {
        BattleSimulatorValidator.validateTeamStructure(first.members(), second.members());
        int count = first.members().size();
        double firstScore = calculateSideScore(first.members(), second.members());
        double secondScore = calculateSideScore(second.members(), first.members());
        Outcome outcome = Math.abs(firstScore - secondScore) < DRAW_EPSILON ? Outcome.DRAW
                : firstScore > secondScore ? Outcome.TEAM_ONE : Outcome.TEAM_TWO;
        double confidence = roundTwo(calculateConfidence(firstScore, secondScore) * 100);
        double roundedFirst = roundTwo(firstScore);
        double roundedSecond = roundTwo(secondScore);
        String summary = buildResultMessage(count + " vs. " + count, first.name(), second.name(),
                outcome, roundedFirst, roundedSecond, confidence);
        return new BattleResult(outcome, roundedFirst, roundedSecond, confidence, summary);
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
        double evRatio = totalEVs
            / (double) (ValidationLimits.IntegerRange.EV.max() * EV_STATS_COUNT);

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

    private String buildResultMessage(String mode, String sideOneName, String sideTwoName, Outcome outcome,
            double sideOneScore, double sideTwoScore, double confidence) {
        String winner = switch (outcome) {
            case TEAM_ONE -> sideOneName;
            case TEAM_TWO -> sideTwoName;
            case DRAW -> "Draw";
        };
        return "[Team Battle " + mode + "] "
            + sideOneName + " (score: " + sideOneScore + ")"
            + " vs "
            + sideTwoName + " (score: " + sideTwoScore + ")"
            + " — Winner: " + winner
            + " (confidence: " + confidence + "%).";
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
            .sorted(Comparator.comparingInt(slot -> slot.getPosition()))
            .map(slot -> slot.getPokemon())
            .toList();
    }

    private double roundTwo(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}
