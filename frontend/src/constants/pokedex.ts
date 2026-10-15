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
