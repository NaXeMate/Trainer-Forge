import type { CSSProperties } from "react";
import { getTypeColor } from "../../../utils/pokemonPresentation";
import "./pokemonTypeBadge.css";

const TYPE_ICONS = import.meta.glob<string>("../../../assets/icons/pokemonTypes/*.svg", {
  eager: true,
  query: "?url",
  import: "default",
});

export default function PokemonTypeBadge({ type, as: Element = "li" }: { type: string; as?: "li" | "span" }) {
  const name = type.trim().toLowerCase();
  const icon = TYPE_ICONS[`../../../assets/icons/pokemonTypes/${name}.svg`]
    ?? TYPE_ICONS["../../../assets/icons/pokemonTypes/normal.svg"];

  return (
    <Element className="pokemon-type-badge" style={{ "--type-color": getTypeColor(type) } as CSSProperties}>
      <span className="pokemon-type-badge__icon" aria-hidden="true">
        <img src={icon} alt="" />
      </span>
      <span className="pokemon-type-badge__label">{type.toUpperCase()}</span>
    </Element>
  );
}
