import { ApiError, apiRequest } from "./api";
import { BASE_STAT_MIN, BASE_STAT_MAX } from "../constants/pokedex";
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

/** Reuse the individual stat endpoints and combine their results with AND semantics. */
export async function searchPokedexEntries(
  filters: readonly PokedexStatFilter[],
  signal?: AbortSignal,
): Promise<PokedexEntry[]> {
  const active = filters.filter(filter => filter.enabled);
  if (active.length === 0) return getPokedexEntries(signal);

  for (const filter of active) {
    if (!Number.isInteger(filter.value) || filter.value < BASE_STAT_MIN || filter.value > BASE_STAT_MAX) {
      throw new Error(`Invalid ${filter.stat} filter value`);
    }
  }

  const results = await Promise.all(active.map(async filter => {
    const min = filter.mode === "maximum" ? BASE_STAT_MIN : filter.value;
    const max = filter.mode === "minimum" ? BASE_STAT_MAX : filter.value;
    const query = new URLSearchParams({ min: String(min), max: String(max) });
    try {
      return await requestEntries(`/pokedex/stats/${filter.stat}?${query}`, signal);
    } catch (error) {
      // The existing backend reports an empty stat result as HTTP 404.
      if (error instanceof ApiError && error.status === 404) return [];
      throw error;
    }
  }));

  const [first, ...rest] = results;
  const matchingIds = rest.map(entries => new Set(entries.map(entry => entry.id)));
  return first.filter(entry => matchingIds.every(ids => ids.has(entry.id)));
}
