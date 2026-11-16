import { POKEDEX_STATS } from "../constants/pokedex";
import type { PokedexStat } from "../types/pokedexTypes";

export const STAT_POINT_BUDGET = 66;
export const MAX_STAT_POINTS = 32;

export type StatPoints = Record<PokedexStat, number>;

export const EMPTY_STAT_POINTS: Readonly<StatPoints> = {
  hp: 0,
  attack: 0,
  defense: 0,
  "special-attack": 0,
  "special-defense": 0,
  speed: 0,
};

export function getStatPointTotal(points: StatPoints): number {
  return POKEDEX_STATS.reduce((total, { stat }) => total + points[stat], 0);
}

export function getStatPointLimit(points: StatPoints, stat: PokedexStat): number {
  const spentOnOtherStats = getStatPointTotal(points) - points[stat];
  return Math.min(MAX_STAT_POINTS, Math.max(points[stat], STAT_POINT_BUDGET - spentOnOtherStats));
}

export function hasValidStatPointValues(value: unknown): value is StatPoints {
  if (!value || typeof value !== "object") return false;
  const points = value as StatPoints;

  return POKEDEX_STATS.every(({ stat }) =>
    Number.isInteger(points[stat]) && points[stat] >= 0 && points[stat] <= MAX_STAT_POINTS
  );
}

export function isValidStatPoints(value: unknown): value is StatPoints {
  return hasValidStatPointValues(value) && getStatPointTotal(value) <= STAT_POINT_BUDGET;
}
