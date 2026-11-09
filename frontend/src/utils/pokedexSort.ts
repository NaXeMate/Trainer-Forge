import type { PokedexEntry } from "../types/pokedexTypes";
import { sortByCriteria } from "./multiSort";
import type { SortCriterion } from "./multiSort";

export const POKEDEX_SORT_GROUPS = [
  {
    label: "Pokémon",
    options: [
      { key: "id", label: "ID", kind: "number" },
      { key: "nationalPokedex", label: "National Dex Num.", kind: "number" },
      { key: "name", label: "Name", kind: "text" },
      { key: "generation", label: "Generation", kind: "number" },
      { key: "region", label: "Region", kind: "text" },
      { key: "type1", label: "Primary type", kind: "text" },
      { key: "type2", label: "Secondary type", kind: "text" },
      { key: "ability1", label: "Primary ability", kind: "text" },
      { key: "ability2", label: "Secondary ability", kind: "text" },
      { key: "hiddenAbility", label: "Hidden ability", kind: "text" },
      { key: "pokemonClass", label: "Class", kind: "text" },
      { key: "category", label: "Category", kind: "text" },
      { key: "height", label: "Height", kind: "number" },
      { key: "weight", label: "Weight", kind: "number" },
    ],
  },
  {
    label: "Base stats",
    options: [
      { key: "hpBase", label: "HP", kind: "number" },
      { key: "attackBase", label: "Attack", kind: "number" },
      { key: "defenseBase", label: "Defense", kind: "number" },
      { key: "specialAttackBase", label: "Sp. Attack", kind: "number" },
      { key: "specialDefenseBase", label: "Sp. Defense", kind: "number" },
      { key: "speedBase", label: "Speed", kind: "number" },
      { key: "statTotal", label: "Base stat total", kind: "number" },
    ],
  },
] as const;

export type PokedexSortKey = typeof POKEDEX_SORT_GROUPS[number]["options"][number]["key"];
export type { SortDirection } from "./multiSort";
export type PokedexSortCriterion = SortCriterion<PokedexSortKey>;

const collator = new Intl.Collator(undefined, { sensitivity: "base", numeric: true });

function sortValue(entry: PokedexEntry, key: PokedexSortKey): string | number | null {
  if (key === "statTotal") {
    return entry.hpBase + entry.attackBase + entry.defenseBase + entry.specialAttackBase + entry.specialDefenseBase + entry.speedBase;
  }

  if (key === "name") return Array.from(entry.name.trim())[0] ?? "";
  return entry[key];
}

export function sortPokedexEntries(entries: readonly PokedexEntry[], criteria: readonly PokedexSortCriterion[]): PokedexEntry[] {
  if (criteria.length === 0) return [...entries].sort((left, right) => left.id - right.id);

  const nameCriterion = criteria.find(criterion => criterion.key === "name");
  return sortByCriteria(entries, criteria, sortValue, (left, right) => {
    if (!nameCriterion) return 0;
    const comparison = collator.compare(left.name, right.name);
    return nameCriterion.direction === "ascending" ? comparison : -comparison;
  });
}
