import { useMemo, type CSSProperties } from "react";

import type { PokedexEntry } from "../../types/pokedexTypes";
import { getTypeColor } from "../../utils/pokemonPresentation";
import { TEAM_SIZE, type Draft } from "../../utils/teamDraft";
import {
  analyzeTeamTypes,
  getAttackingTypesByMove,
  getTeamTypeProfiles,
  type PokemonType,
} from "../../utils/typeEffectiveness";

import type { MoveLoadState } from "./movePicker";

const ANALYZER_TYPES: readonly PokemonType[] = [
  "GRASS", "FIRE", "WATER", "NORMAL", "ELECTRIC", "GHOST",
  "FAIRY", "FLYING", "ICE", "ROCK", "GROUND", "PSYCHIC",
  "FIGHTING", "DARK", "BUG", "DRAGON", "POISON", "STEEL",
];

const TYPE_ICONS = import.meta.glob<string>("../../assets/icons/pokemonTypes/*.svg", {
  eager: true,
  query: "?url",
  import: "default",
});

type TypeAnalyzerProps = {
  members: Draft["members"];
  pokedex: ReadonlyMap<number, PokedexEntry>;
  moveState: MoveLoadState;
  onRetry: () => void;
};

export default function TypeAnalyzer({ members, pokedex, moveState, onRetry }: TypeAnalyzerProps) {
  const attackTypesByName = useMemo(
    () => getAttackingTypesByMove(moveState.status === "success" ? moveState.moves : []),
    [moveState],
  );
  const team = getTeamTypeProfiles(members, pokedex, attackTypesByName);
  const coverageUnavailable = moveState.status === "error" && members.some(member => member?.moves.some(Boolean));

  return (
    <section className="type-analyzer" aria-labelledby="type-analyzer-title">
      <h2 id="type-analyzer-title">TYPE VIABILITY</h2>

      <div className="type-analyzer__grid">
        {ANALYZER_TYPES.map(type => {
          const { weak, covered, superEffective } = analyzeTeamTypes(team, type);
          const uncovered = weak - covered;
          const markCount = Math.max(TEAM_SIZE, weak + superEffective);
          const marks = Array.from({ length: markCount }, (_, index) => {
            if (index < uncovered) return "weak";
            if (index < weak) return "covered";
            if (index < weak + superEffective) return "strong";
            return "neutral";
          });
          const denseClass = markCount > TEAM_SIZE ? " type-analyzer__marks--dense" : "";

          return (
            <div className="type-analyzer__row" key={type}>
              <span
                className="type-analyzer__icon"
                style={{ "--type-color": getTypeColor(type) } as CSSProperties}
                title={type}
              >
                <img src={TYPE_ICONS[`../../assets/icons/pokemonTypes/${type.toLowerCase()}.svg`]} alt={type} />
              </span>
              <div
                className={`type-analyzer__marks${denseClass}`}
                role="img"
                aria-label={`${type}: ${uncovered} weak, ${covered} covered, ${superEffective} super effective`}
                title={`${uncovered} weak · ${covered} covered · ${superEffective} super effective`}
              >
                {marks.map((mark, index) => (
                  <span
                    key={index}
                    aria-hidden="true"
                    className={`type-analyzer__mark type-analyzer__mark--${mark}`}
                  />
                ))}
              </div>
            </div>
          );
        })}
      </div>

      {coverageUnavailable && (
        <p className="type-analyzer__note" role="status">
          Could not load move coverage.{" "}
          <button type="button" onClick={onRetry}>Retry</button>
        </p>
      )}
    </section>
  );
}
