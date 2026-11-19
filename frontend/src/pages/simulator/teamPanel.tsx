import { Link } from "react-router";

import type { PokedexEntry } from "../../types/pokedexTypes";
import { TEAM_SIZE, type Draft } from "../../utils/teamDraft";

import { sideLabels, type Side } from "./sides";

export type PanelOutcome = "win" | "loss" | "draw";

const emptySlots = Array.from({ length: TEAM_SIZE }, () => null);

export default function TeamPanel({ side, name = "No team selected", draft, pokedex, edit, outcome, onChoose }: {
  side: Side;
  name?: string;
  draft?: Draft;
  pokedex?: Map<number, PokedexEntry>;
  edit?: string;
  outcome?: PanelOutcome;
  onChoose?: (side: Side) => void;
}) {
  const sideLabel = sideLabels[side];
  const resultLabel = outcome === "win" ? "Victory" : outcome === "loss" ? "Defeat" : "Draw";
  return (
    <section className={`simulator-team simulator-team--${side}`} aria-label={sideLabel}>
      {outcome && <p className={`simulator-team__outcome simulator-team__outcome--${outcome}`}>{resultLabel}</p>}
      <button
        className="simulator-team__roster"
        type="button"
        aria-label={`Choose team for ${sideLabel}: ${name}`}
        aria-haspopup={onChoose ? "dialog" : undefined}
        disabled={!onChoose}
        onClick={() => onChoose?.(side)}
      >
        {(draft?.members ?? emptySlots).map((member, index) => {
          const pokemon = member ? pokedex?.get(member.pokedexId) : undefined;
          return (
            <span className="simulator-team__slot" key={index}>
              {pokemon ? <img src={pokemon.imageUrl} alt={pokemon.name} />
                : <span aria-hidden="true" className="simulator-team__empty-slot">—</span>}
            </span>
          );
        })}
      </button>
      <div className="simulator-team__identity">
        <p className="simulator-team__role">{sideLabel}</p>
        <h2 className="simulator-team__name">{name}</h2>
      </div>
      {edit && <Link
        className="simulator-team__edit"
        to={edit}
        aria-label={`Edit this team: ${name}`}
      >Edit this team...</Link>}
    </section>
  );
}
