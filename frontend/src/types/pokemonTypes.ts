// Basic Pokémon data as returned by the API
export type PokemonResponse = {
  id: number;
  name: string;
  types: string[];
  sprite: string;
};
