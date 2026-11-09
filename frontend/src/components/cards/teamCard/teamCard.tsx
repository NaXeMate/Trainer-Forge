import { useId } from "react";
import type { CSSProperties, ReactNode } from "react";
import megaEvolutionIcon from "../../../assets/icons/specialGimmicks/megaevolution.svg";
import zPowerIcon from "../../../assets/icons/specialGimmicks/z-power.svg";
import { POKEDEX_STATS } from "../../../constants/pokedex";
import type { EquippableItemType } from "../../../types/itemTypes";
import type { PokedexEntry } from "../../../types/pokedexTypes";
import type { StatPoints } from "../../../utils/championsStatPoints";
import { getTypeColor } from "../../../utils/pokemonPresentation";
import Combobox from "../../common/combobox";
import type { ComboboxOption } from "../../common/combobox";
import "../../../styles/pokemonTypeColors.css";
import "../../../styles/teamControl.css";
import "./teamCard.css";

type ItemOption = ComboboxOption & { type: EquippableItemType };

export type ItemCatalogue =
  | { status: "loading" | "error" }
  | { status: "success"; options: readonly ItemOption[] };

type TeamCardProps = {
  pokemon: PokedexEntry;
  slotNumber: number;
  moves: readonly [string, string, string, string];
  ability: string;
  item: string;
  itemCatalogue: ItemCatalogue;
  onRetryItems: () => void;
  statPoints: StatPoints;
  onRemove: () => void;
  onReplace: (trigger: HTMLButtonElement) => void;
  onChooseMove: (index: number, trigger: HTMLButtonElement) => void;
  onClearMove: (index: number) => void;
  onAbilityChange: (ability: string) => void;
  onItemChange: (item: string) => void;
  onEditStatPoints: (trigger: HTMLButtonElement) => void;
};

const STAT_ABBREVIATIONS = {
  hp: "HP",
  attack: "ATK.",
  defense: "DEF.",
  "special-attack": "SP. ATK.",
  "special-defense": "SP. DEF.",
  speed: "SPD.",
} as const;

function CloseIcon() {
  return (
    <svg aria-hidden="true" width="16" height="16" viewBox="0 0 16 16" fill="none" stroke="currentColor" strokeWidth="1.5" strokeLinecap="round">
      <path d="m4 4 8 8M12 4l-8 8" />
    </svg>
  );
}

function ClearFieldButton({ label, onClear }: { label: string; onClear: () => void }) {
  return (
    <button
      className="team-pokemon__clear"
      type="button"
      aria-label={label}
      onClick={event => {
        const control = event.currentTarget.previousElementSibling;
        onClear();
        if (control instanceof HTMLElement) control.focus();
      }}
    >
      <CloseIcon />
    </button>
  );
}

export default function TeamCard({
  pokemon,
  slotNumber,
  moves,
  ability,
  item,
  itemCatalogue,
  onRetryItems,
  statPoints,
  onRemove,
  onReplace,
  onChooseMove,
  onClearMove,
  onAbilityChange,
  onItemChange,
  onEditStatPoints,
}: TeamCardProps) {
  const itemInputId = useId();
  const { name, imageUrl, type1, type2, ability1, ability2, hiddenAbility } = pokemon;
  const abilityOptions = [...new Set([ability1, ability2, hiddenAbility])]
    .filter((entry): entry is string => Boolean(entry))
    .map(entry => ({ value: entry, label: entry }));
  const itemOptions = itemCatalogue.status === "success" ? itemCatalogue.options : [];
  const itemName = item.trim().toLowerCase();
  const selectedItemType = itemOptions.find(option => option.value.toLowerCase() === itemName)?.type;
  let itemEmptyMessage: ReactNode = "No matching items.";
  if (itemCatalogue.status === "loading") itemEmptyMessage = "Loading items…";
  if (itemCatalogue.status === "error") {
    itemEmptyMessage = <>Could not load items. <button className="team-pokemon__retry-items" type="button" onClick={onRetryItems}>Retry</button></>;
  }
  const statSpread = POKEDEX_STATS
    .filter(({ stat }) => statPoints[stat] > 0)
    .map(({ stat }) => `${statPoints[stat]} ${STAT_ABBREVIATIONS[stat]}`);
  const style = {
    "--team-type-start": getTypeColor(type1),
    "--team-type-end": getTypeColor(type2 ?? type1),
  } as CSSProperties;

  return (
    <article className="team-pokemon" style={style} aria-label={`Slot ${slotNumber}: ${name}`}>
      <button className="team-pokemon__remove" type="button" aria-label={`Remove ${name} from slot ${slotNumber}`} onClick={onRemove}>
        <CloseIcon />
      </button>
      <button className="team-pokemon__species" type="button" aria-label={`Replace ${name} in slot ${slotNumber}`} onClick={event => onReplace(event.currentTarget)}>
        <img src={imageUrl} alt="" />
        <span>{name}</span>
      </button>
      <div className="team-pokemon__fields">
        {moves.map((move, index) => (
          <div className="team-pokemon__field" key={index}>
            <span className="team-pokemon__field-label">MOVE {index + 1}</span>
            <div className="team-pokemon__clearable">
              <button
                className="team-control"
                type="button"
                data-empty={!move}
                aria-label={`${name}, move ${index + 1}${move ? `: ${move}` : ""}`}
                aria-haspopup="dialog"
                onClick={event => onChooseMove(index, event.currentTarget)}
              >
                {move || "—"}
              </button>
              {move && <ClearFieldButton label={`Clear move ${index + 1} for ${name}`} onClear={() => onClearMove(index)} />}
            </div>
          </div>
        ))}
        <div className="team-pokemon__field">
          <span className="team-pokemon__field-label">ABILITY</span>
          <Combobox
            inputClassName="team-control"
            label={`${name}, ability`}
            placeholder="—"
            value={ability}
            options={abilityOptions}
            onChange={onAbilityChange}
          />
        </div>
        <div className="team-pokemon__field team-pokemon__field--item" data-item-type={selectedItemType}>
          <label className="team-pokemon__field-label" htmlFor={itemInputId}>EQUIPPED ITEM</label>
          <Combobox
            inputId={itemInputId}
            inputClassName="team-control"
            label={`${name}, equipped item`}
            placeholder="—"
            value={item}
            options={itemOptions}
            readOnly={itemCatalogue.status !== "success"}
            emptyMessage={itemEmptyMessage}
            invalidMessage="Choose an item from the list."
            clearLabel={`Clear equipped item for ${name}`}
            leadingAdornment={
              <span className="team-pokemon__item-icons" aria-hidden="true">
                <img src={megaEvolutionIcon} alt="" data-icon="mega" />
                <img src={zPowerIcon} alt="" data-icon="z" />
              </span>
            }
            onChange={onItemChange}
          />
        </div>
        <div className="team-pokemon__field">
          <span className="team-pokemon__field-label">STAT POINTS</span>
          <button
            className="team-control"
            type="button"
            data-empty={!statSpread.length}
            aria-label={`${name}, stat points: ${statSpread.join(" / ") || "0 SP"}`}
            aria-haspopup="dialog"
            onClick={event => onEditStatPoints(event.currentTarget)}
          >
            {statSpread.length ? statSpread.map((entry, index) => (
              <span key={entry} className="team-pokemon__stat-pair">
                {entry}{index < statSpread.length - 1 ? " /" : ""}{" "}
              </span>
            )) : "0 SP"}
          </button>
        </div>
      </div>
    </article>
  );
}
