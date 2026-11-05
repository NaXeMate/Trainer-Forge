import type { PokedexEntry } from "../types/pokedexTypes";

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
export type SortDirection = "ascending" | "descending";
export interface PokedexSortCriterion {
  key: PokedexSortKey;
  direction: SortDirection;
}

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
  return [...entries].sort((left, right) => {
    for (const { key, direction } of criteria) {
      const a = sortValue(left, key);
      const b = sortValue(right, key);
      
      if (a == null || b == null) {
        if (a !== b) return a == null ? 1 : -1;
        continue;
      }
      const comparison = typeof a === "number" && typeof b === "number"  ? a - b : collator.compare(String(a), String(b));
      
      if (comparison !== 0) return direction === "ascending" ? comparison : -comparison;
    }

    if (nameCriterion) {
      const comparison = collator.compare(left.name, right.name);
      if (comparison !== 0) return nameCriterion.direction === "ascending" ? comparison : -comparison;
    }
    
    return 0;
  });
}
