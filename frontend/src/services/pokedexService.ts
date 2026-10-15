import { apiRequest } from "./api";
import type { PokedexEntry } from "../types/pokedexTypes";

export async function getPokedexEntries(signal?: AbortSignal): Promise<PokedexEntry[]> {
  const entries = await apiRequest<PokedexEntry[]>("GET", "/pokedex", undefined, signal);

  if (!Array.isArray(entries)) {
    throw new Error("Invalid PokéDex list response");
  }

  // Order the entries by their ID to ensure a consistent display order.
  return entries.toSorted((left, right) => left.id - right.id);
}
