import type { PokemonResponse } from "./pokemonTypes";

// A trainer's team with up to 6 Pokémon
export type TeamResponse = {
  id: number;
  name: string;
  trainerId: number;
  pokemon: PokemonResponse[];
};

// Data required to create a new team
export type CreateTeam = {
  name: string;
  trainerId: number;
};
