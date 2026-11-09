import type { MoveOption } from "../types/moveTypes";
import type { PokedexEntry } from "../types/pokedexTypes";
import type { SliderFilter } from "../types/sliderTypes";
import { sortByCriteria } from "./multiSort";
import type { SortCriterion } from "./multiSort";
import { POKEDEX_SORT_GROUPS, sortPokedexEntries } from "./pokedexSort";
import type { PokedexSortCriterion } from "./pokedexSort";

const excludedPokemonSortKeys = new Set(["pokemonClass", "category", "region", "height", "weight"]);
export const TEAM_POKEMON_SORT_GROUPS = [{
  label: POKEDEX_SORT_GROUPS[0].label,
  options: POKEDEX_SORT_GROUPS[0].options.filter(option => !excludedPokemonSortKeys.has(option.key)),
}];

export const MOVE_RANGES = [
  { key: "power", label: "Power", kind: "number", min: 0, max: 250 },
  { key: "accuracy", label: "Accuracy", kind: "number", min: 0, max: 100 },
  { key: "pp", label: "PP", kind: "number", min: 5, max: 40 },
] as const;

export const TEAM_MOVE_SORT_GROUPS = [{
  label: "Moves",
  options: [
    MOVE_RANGES[0],
    MOVE_RANGES[1],
    { key: "moveClass", label: "Class", kind: "text" },
    MOVE_RANGES[2],
    { key: "name", label: "Name", kind: "text" },
  ],
}] as const;

export type MoveSortCriterion = SortCriterion<typeof TEAM_MOVE_SORT_GROUPS[0]["options"][number]["key"]>;
type MoveRangeKey = typeof MOVE_RANGES[number]["key"];
export type MoveFilters = { type: string; moveClass: string; ranges: Record<MoveRangeKey, SliderFilter> };
export type PokemonFilters = { type: string; ability: string };

export const EMPTY_MOVE_FILTERS: MoveFilters = {
  type: "",
  moveClass: "",
  ranges: {
    power: { enabled: false, mode: "equal", value: 75 },
    accuracy: { enabled: false, mode: "equal", value: 90 },
    pp: { enabled: false, mode: "equal", value: 15 },
  },
};
export const EMPTY_POKEMON_FILTERS: PokemonFilters = { type: "", ability: "" };

function normalize(value: string | null) {
  return value?.trim().toLowerCase() ?? "";
}

function moveNumber(move: MoveOption, key: MoveRangeKey) {
  // The API can represent absent power or accuracy as zero.
  return key === "pp" ? move.pp : move[key] || null;
}

function matchesSlider(value: number, filter: SliderFilter) {
  if (!filter.enabled) return true;
  if (filter.mode === "minimum") return value >= filter.value;
  if (filter.mode === "maximum") return value <= filter.value;
  return value === filter.value;
}

export function getTeamPickerPokemon(
  entries: readonly PokedexEntry[], query: string, filters: PokemonFilters, criteria: readonly PokedexSortCriterion[],
) {
  const search = normalize(query);
  const numericSearch = /^\d+$/.test(search);
  const type = normalize(filters.type);
  const ability = normalize(filters.ability);
  const matches = entries.filter(pokemon =>
    (numericSearch ? String(pokemon.id).includes(search) : normalize(pokemon.name).includes(search)) &&
    (!type || [pokemon.type1, pokemon.type2].some(value => normalize(value) === type)) &&
    (!ability || [pokemon.ability1, pokemon.ability2, pokemon.hiddenAbility].some(value => normalize(value) === ability)),
  );
  return sortPokedexEntries(matches, criteria);
}

export function getTeamPickerMoves(
  moves: readonly MoveOption[], query: string, filters: MoveFilters, criteria: readonly MoveSortCriterion[],
) {
  const search = normalize(query);
  const type = normalize(filters.type);
  const moveClass = normalize(filters.moveClass);
  const matches = moves.filter(move =>
    normalize(move.name).includes(search) &&
    (!type || normalize(move.type) === type) &&
    (!moveClass || normalize(move.moveClass) === moveClass) &&
    MOVE_RANGES.every(({ key }) => matchesSlider(move[key] ?? 0, filters.ranges[key])),
  );
  return sortByCriteria(matches, criteria, (move, key) =>
    key === "name" || key === "moveClass" ? move[key] : moveNumber(move, key));
}
