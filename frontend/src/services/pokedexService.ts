import { ApiError, apiRequest } from "./api";
import { BASE_STAT_MIN, BASE_STAT_MAX, findPokemonType } from "../constants/pokedex";
import type { PokedexEntry, PokedexStatFilter } from "../types/pokedexTypes";

async function requestEntries(endpoint: string, signal?: AbortSignal): Promise<PokedexEntry[]> {
  const entries = await apiRequest<PokedexEntry[]>("GET", endpoint, undefined, signal);

  if (!Array.isArray(entries)) {
    throw new Error("Invalid PokéDex list response");
  }

  // Order the entries by their ID to ensure a consistent display order.
  return entries.toSorted((left, right) => left.id - right.id);
}

export function getPokedexEntries(signal?: AbortSignal): Promise<PokedexEntry[]> {
  return requestEntries("/pokedex", signal);
}

/** Combine the existing statistic and type endpoints with AND semantics. */
export async function searchPokedexEntries(
  filters: readonly PokedexStatFilter[],
  signal?: AbortSignal,
  typeFilters: readonly string[] = [],
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
  if (endpoints.length === 0) return getPokedexEntries(signal);

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
  return first.filter(entry => matchingIds.every(ids => ids.has(entry.id)));
}
