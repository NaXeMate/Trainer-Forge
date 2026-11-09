import type { SliderFilter } from "./sliderTypes";

export type PokemonClass = "COMMON" | "LEGENDARY" | "MYTHICAL" | "ULTRABEAST" | "PARADOX";

export type PokedexStat = "hp" | "attack" | "defense" | "special-attack" | "special-defense" | "speed";
export type { SliderMode as StatFilterMode } from "./sliderTypes";

export interface PokedexStatFilter extends SliderFilter {
  stat: PokedexStat;
}

export interface PokedexTextFilters {
  name: string;
  generation: string;
  region: string;
  ability: string;
}

/** Response from GET /api/pokedex, using the backend field names. */
export interface PokedexEntry {
  id: number;
  nationalPokedex: number;
  name: string;
  imageUrl: string;
  generation: number;
  region: string;
  pokemonClass: PokemonClass;
  type1: string;
  type2: string | null;
  ability1: string;
  ability2: string | null;
  hiddenAbility: string | null;
  description: string;
  category: string;
  weight: number;
  height: number;
  hpBase: number;
  attackBase: number;
  defenseBase: number;
  specialAttackBase: number;
  specialDefenseBase: number;
  speedBase: number;
}
