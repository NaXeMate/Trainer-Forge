package dev.trainerforge.dto.response;

import java.util.List;

import dev.trainerforge.model.enumerated.TeamModality;

public record TeamDto(
    Long id,
    String name,
    TeamModality modality,
    boolean hidden,
    String trainerUsername,
    String videogame
) {
    /** Six ordered slots, with null entries for empty positions. */
    public record Lineup(String name, TeamModality modality, Long gameId, Boolean publicTeam,
            List<PokemonTeamDto.Member> members) {}

    public record Complete(Long id, String trainerUsername, Lineup lineup) {}

    public record BattleInput(Side one, Side two) {
        public record Side(Long teamId, Lineup draft) {}
    }

    public record BattleResult(Outcome outcome, double teamOneScore, double teamTwoScore,
            double confidencePercentage, String summary) {
        public enum Outcome { TEAM_ONE, TEAM_TWO, DRAW }
    }
}
