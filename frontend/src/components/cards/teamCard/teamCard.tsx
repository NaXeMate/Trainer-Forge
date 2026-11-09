import type { CSSProperties } from "react";
import type { PokedexEntry } from "../../../types/pokedexTypes";
import { getTypeColor } from "../../../utils/pokemonPresentation";
import "../../../styles/pokemonTypeColors.css";
import "./teamCard.css";

type TeamCardProps = {
  pokemon: PokedexEntry;
  slotNumber: number;
  moves: readonly [string, string, string, string];
  ability: string;
  item: string;
  onRemove: () => void;
  onReplace: (trigger: HTMLButtonElement) => void;
  onChooseMove: (index: number, trigger: HTMLButtonElement) => void;
  onAbilityChange: (ability: string) => void;
  onItemChange: (item: string) => void;
  onEditStatPoints: (trigger: HTMLButtonElement) => void;
};

export default function TeamCard({
  pokemon,
  slotNumber,
  moves,
  ability,
  item,
  onRemove,
  onReplace,
  onChooseMove,
  onAbilityChange,
  onItemChange,
  onEditStatPoints,
}: TeamCardProps) {
  const { name, imageUrl, type1, type2, ability1, ability2, hiddenAbility } = pokemon;
  const abilities = [...new Set([ability1, ability2, hiddenAbility].filter(Boolean))];
  const style = {
    "--team-type-start": getTypeColor(type1),
    "--team-type-end": getTypeColor(type2 ?? type1),
  } as CSSProperties;

  return (
    <article className="team-pokemon" style={style} aria-label={`Slot ${slotNumber}: ${name}`}>
      <button className="team-pokemon__remove" type="button" aria-label={`Remove ${name} from slot ${slotNumber}`} onClick={onRemove}>
        <svg aria-hidden="true" width="16" height="16" viewBox="0 0 16 16" fill="none" stroke="currentColor" strokeWidth="1.5" strokeLinecap="round">
          <path d="m4 4 8 8M12 4l-8 8" />
        </svg>
      </button>
      <button className="team-pokemon__species" type="button" aria-label={`Replace ${name} in slot ${slotNumber}`} onClick={event => onReplace(event.currentTarget)}>
        <img src={imageUrl} alt="" />
        <span>{name}</span>
      </button>
      <div className="team-pokemon__fields">
        {moves.map((move, index) => (
          <button
            className="team-pokemon__field"
            type="button"
            key={index}
            aria-label={`${name}, move ${index + 1}${move ? `: ${move}` : ""}`}
            onClick={event => onChooseMove(index, event.currentTarget)}
          >
            {move || `MOVE ${index + 1}`}
          </button>
        ))}
        <label className="team-pokemon__field">
          <span className="visually-hidden">{name}, ability</span>
          <select value={ability} onChange={event => onAbilityChange(event.target.value)}>
            <option value="">ABILITY</option>
            {abilities.map(option => <option key={option}>{option}</option>)}
          </select>
        </label>
        <label className="team-pokemon__field">
          <span className="visually-hidden">{name}, equipped item</span>
          <input value={item} placeholder="EQUIPPED ITEM" maxLength={60} onChange={event => onItemChange(event.target.value)} />
        </label>
        <button className="team-pokemon__field" type="button" onClick={event => onEditStatPoints(event.currentTarget)}>
          STAT POINTS
        </button>
      </div>
    </article>
  );
}
