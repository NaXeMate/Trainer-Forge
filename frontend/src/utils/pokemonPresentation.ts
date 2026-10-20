import type { CSSProperties } from "react";
import type { PokedexEntry } from "../types/pokedexTypes";

// The API provides type names; all editable colors live in pokedex-colors.css.
const typeColors = new Map<string, string>([
  ["normal", "var(--pokedex-type-normal)"],
  ["fire", "var(--pokedex-type-fire)"],
  ["water", "var(--pokedex-type-water)"],
  ["electric", "var(--pokedex-type-electric)"],
  ["grass", "var(--pokedex-type-grass)"],
  ["ice", "var(--pokedex-type-ice)"],
  ["fighting", "var(--pokedex-type-fighting)"],
  ["poison", "var(--pokedex-type-poison)"],
  ["ground", "var(--pokedex-type-ground)"],
  ["flying", "var(--pokedex-type-flying)"],
  ["psychic", "var(--pokedex-type-psychic)"],
  ["bug", "var(--pokedex-type-bug)"],
  ["rock", "var(--pokedex-type-rock)"],
  ["ghost", "var(--pokedex-type-ghost)"],
  ["dragon", "var(--pokedex-type-dragon)"],
  ["dark", "var(--pokedex-type-dark)"],
  ["steel", "var(--pokedex-type-steel)"],
  ["fairy", "var(--pokedex-type-fairy)"],
]);

export function getTypeColor(type: string): string {
  return typeColors.get(type.trim().toLowerCase()) ?? "var(--pokedex-type-unknown)";
}

type CardStyle = CSSProperties & {
  "--pokedex-card-type-start": string;
  "--pokedex-card-type-end": string;
};

/** Derive presentation from server data without assigning colors in TypeScript. */
export function getPokedexCardPresentation(
  pokemon: Pick<PokedexEntry, "type1" | "type2" | "pokemonClass">,
): { className: string; style: CardStyle } {
  const primary = getTypeColor(pokemon.type1);
  const secondary = pokemon.type2?.trim() ? getTypeColor(pokemon.type2) : primary;
  let className = "pokedex-card";

  if (pokemon.pokemonClass === "LEGENDARY") {
    className += " pokedex-card--legendary";
  } else if (pokemon.pokemonClass === "MYTHICAL") {
    className += " pokedex-card--mythical";
  }

  return {
    className,
    style: {
      "--pokedex-card-type-start": primary,
      "--pokedex-card-type-end": secondary,
    },
  };
}
