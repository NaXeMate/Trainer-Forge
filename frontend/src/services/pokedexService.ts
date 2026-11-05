import { ApiError, apiRequest } from "./api";
import { BASE_STAT_MIN, BASE_STAT_MAX, findPokemonType } from "../constants/pokedex";
import type { PokedexEntry, PokedexStatFilter, PokedexTextFilters } from "../types/pokedexTypes";

const GENERATION_NUMERALS = ["I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX"];

function normalize(value: string): string {
  return value.trim().normalize("NFD").replace(/[\u0300-\u036f]/g, "").toUpperCase();
}

export function filterPokedexEntries(entries: PokedexEntry[], filters: PokedexTextFilters): PokedexEntry[] {
  const name = normalize(filters.name);
  const generation = normalize(filters.generation).replace(/^GENERATION\s+/, "");
  const region = normalize(filters.region);
  const ability = normalize(filters.ability);

  return entries.filter(entry =>
    normalize(entry.name).includes(name) &&
    (!generation || generation === String(entry.generation) || generation === GENERATION_NUMERALS[entry.generation - 1]) &&
    normalize(entry.region).includes(region) &&
    (!ability || [entry.ability1, entry.ability2, entry.hiddenAbility].some(value => value && normalize(value).includes(ability)))
  );
}

async function requestEntries(endpoint: string, signal?: AbortSignal): Promise<PokedexEntry[]> {
  const entries = await apiRequest<PokedexEntry[]>("GET", endpoint, undefined, signal);

  if (!Array.isArray(entries)) {
    throw new Error("Invalid PokeDex list response");
  }

  // Order the entries by their ID to ensure a consistent display order.
  return entries.toSorted((left, right) => left.id - right.id);
}

export function getPokedexEntries(signal?: AbortSignal): Promise<PokedexEntry[]> {
  return requestEntries("/pokedex", signal);
}

export function getPokedexEntryById(id: number, signal?: AbortSignal): Promise<PokedexEntry> {
  return apiRequest<PokedexEntry>("GET", `/pokedex/${id}`, undefined, signal);
}

/** Combine the existing statistic and type endpoints and text filters with AND semantics. */
export async function searchPokedexEntries(
  filters: readonly PokedexStatFilter[],
  signal?: AbortSignal,
  typeFilters: readonly string[] = [],
  textFilters: PokedexTextFilters = { name: "", generation: "", region: "", ability: "" },
): Promise<PokedexEntry[]> {
  const active = filters.filter(filter => filter.enabled);

  for (const filter of active) {
    if (!Number.isInteger(filter.value) || filter.value < BASE_STAT_MIN || filter.value > BASE_STAT_MAX) {
      throw new Error(`Invalid ${filter.stat} filter value`);
    }
  }

  const selectedTypes = typeFilters.map(value => {
    const type = findPokemonType(value);
    if (!type) throw new Error(`Invalid Pokémon type: ${value}`);
    return type;
  });
  if (selectedTypes.length > 2 || new Set(selectedTypes.map(type => type.id)).size !== selectedTypes.length) {
    throw new Error("Choose at most two different Pokémon types");
  }

  const endpoints = active.map(filter => {
    const min = filter.mode === "maximum" ? BASE_STAT_MIN : filter.value;
    const max = filter.mode === "minimum" ? BASE_STAT_MAX : filter.value;
    const query = new URLSearchParams({ min: String(min), max: String(max) });
    return `/pokedex/stats/${filter.stat}?${query}`;
  });
  endpoints.push(...selectedTypes.map(type => `/pokedex/type/${type.id}`));
  if (endpoints.length === 0) return filterPokedexEntries(await getPokedexEntries(signal), textFilters);

  const results = await Promise.all(endpoints.map(async endpoint => {
    try {
      return await requestEntries(endpoint, signal);
    } catch (error) {
      // The existing backend reports an empty filtered result as HTTP 404.
      if (error instanceof ApiError && error.status === 404) return [];
      throw error;
    }
  }));

  const [first, ...rest] = results;
  const matchingIds = rest.map(entries => new Set(entries.map(entry => entry.id)));
  return filterPokedexEntries(first.filter(entry => matchingIds.every(ids => ids.has(entry.id))), textFilters);
}
