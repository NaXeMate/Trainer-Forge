import { apiRequest } from "./api";
import type { PokedexEntry, PokemonDetail } from "../types/pokemon";

// Fetch the full Pokédex (public reference data). Filtering and sorting are
// applied on the client because the backend exposes only single-criterion
// endpoints, not a combined query.
export async function getPokedex(): Promise<PokedexEntry[]> {
  return apiRequest<PokedexEntry[]>("GET", `/pokedex`);
}

// Get full detail of one Pokémon by its internal DB id
export async function getPokemonById(id: number): Promise<PokemonDetail> {
  return apiRequest<PokemonDetail>("GET", `/pokedex/${id}`);
}
