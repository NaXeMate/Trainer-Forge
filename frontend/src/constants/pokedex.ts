import type { PokedexStat } from "../types/pokedexTypes";

export const BASE_STAT_MIN = 0;
export const BASE_STAT_MAX = 255;
export const BASE_STAT_DEFAULT = 127;

export const POKEDEX_STATS = [
  { stat: "hp", label: "HP" },
  { stat: "attack", label: "ATTACK" },
  { stat: "defense", label: "DEFENSE" },
  { stat: "special-attack", label: "SP. ATK." },
  { stat: "special-defense", label: "SP. DEF." },
  { stat: "speed", label: "SPEED" },
] as const satisfies readonly { stat: PokedexStat; label: string }[];

// IDs and names from the backend's pokemon_types reference data.
export const POKEMON_TYPES = [
  { id: 3, name: "BUG" },
  { id: 15, name: "DARK" },
  { id: 4, name: "DRAGON" },
  { id: 5, name: "ELECTRIC" },
  { id: 8, name: "FAIRY" },
  { id: 10, name: "FIGHTING" },
  { id: 7, name: "FIRE" },
  { id: 18, name: "FLYING" },
  { id: 6, name: "GHOST" },
  { id: 12, name: "GRASS" },
  { id: 16, name: "GROUND" },
  { id: 9, name: "ICE" },
  { id: 11, name: "NORMAL" },
  { id: 17, name: "POISON" },
  { id: 13, name: "PSYCHIC" },
  { id: 14, name: "ROCK" },
  { id: 1, name: "STEEL" },
  { id: 2, name: "WATER" },
] as const;

export function findPokemonType(value: string) {
  return POKEMON_TYPES.find(type => type.name.toLowerCase() === value.trim().toLowerCase());
}
