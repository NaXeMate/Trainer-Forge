import { POKEMON_TYPES } from "../constants/pokedex";

export type PokemonType = typeof POKEMON_TYPES[number]["name"];
type TypeProfile = { strong: readonly PokemonType[]; resisted: readonly PokemonType[]; immune?: readonly PokemonType[] };

const TYPE_CHART: Record<PokemonType, TypeProfile> = {
  NORMAL: { strong: [], resisted: ["ROCK", "STEEL"], immune: ["GHOST"] },
  FIRE: { strong: ["GRASS", "ICE", "BUG", "STEEL"], resisted: ["FIRE", "WATER", "ROCK", "DRAGON"] },
  WATER: { strong: ["FIRE", "GROUND", "ROCK"], resisted: ["WATER", "GRASS", "DRAGON"] },
  ELECTRIC: { strong: ["WATER", "FLYING"], resisted: ["ELECTRIC", "GRASS", "DRAGON"], immune: ["GROUND"] },
  GRASS: { strong: ["WATER", "GROUND", "ROCK"], resisted: ["FIRE", "GRASS", "POISON", "FLYING", "BUG", "DRAGON", "STEEL"] },
  ICE: { strong: ["GRASS", "GROUND", "FLYING", "DRAGON"], resisted: ["FIRE", "WATER", "ICE", "STEEL"] },
  FIGHTING: { strong: ["NORMAL", "ICE", "ROCK", "DARK", "STEEL"], resisted: ["POISON", "FLYING", "PSYCHIC", "BUG", "FAIRY"], immune: ["GHOST"] },
  POISON: { strong: ["GRASS", "FAIRY"], resisted: ["POISON", "GROUND", "ROCK", "GHOST"], immune: ["STEEL"] },
  GROUND: { strong: ["FIRE", "ELECTRIC", "POISON", "ROCK", "STEEL"], resisted: ["GRASS", "BUG"], immune: ["FLYING"] },
  FLYING: { strong: ["GRASS", "FIGHTING", "BUG"], resisted: ["ELECTRIC", "ROCK", "STEEL"] },
  PSYCHIC: { strong: ["FIGHTING", "POISON"], resisted: ["PSYCHIC", "STEEL"], immune: ["DARK"] },
  BUG: { strong: ["GRASS", "PSYCHIC", "DARK"], resisted: ["FIRE", "FIGHTING", "POISON", "FLYING", "GHOST", "STEEL", "FAIRY"] },
  ROCK: { strong: ["FIRE", "ICE", "FLYING", "BUG"], resisted: ["FIGHTING", "GROUND", "STEEL"] },
  GHOST: { strong: ["PSYCHIC", "GHOST"], resisted: ["DARK"], immune: ["NORMAL"] },
  DRAGON: { strong: ["DRAGON"], resisted: ["STEEL"], immune: ["FAIRY"] },
  DARK: { strong: ["PSYCHIC", "GHOST"], resisted: ["FIGHTING", "DARK", "FAIRY"] },
  STEEL: { strong: ["ICE", "ROCK", "FAIRY"], resisted: ["FIRE", "WATER", "ELECTRIC", "STEEL"] },
  FAIRY: { strong: ["FIGHTING", "DRAGON", "DARK"], resisted: ["FIRE", "POISON", "STEEL"] },
};

export function getTypeEffectiveness(attacker: PokemonType, defender: PokemonType): number {
  const row = TYPE_CHART[attacker];
  if (row.immune?.includes(defender)) return 0;
  if (row.strong.includes(defender)) return 2;
  return row.resisted.includes(defender) ? 0.5 : 1;
}

export function getPokemonTypes(pokemon: { type1: string; type2: string | null }): PokemonType[] {
  return [...new Set([pokemon.type1, pokemon.type2].flatMap(value => {
    const type = POKEMON_TYPES.find(type => type.name === value?.trim().toUpperCase());
    return type ? [type.name] : [];
  }))];
}

export function analyzePokemonTypes(pokemon: { type1: string; type2: string | null }, target: PokemonType) {
  const types = getPokemonTypes(pokemon);
  return {
    superEffective: types.some(type => getTypeEffectiveness(type, target) > 1),
    weak: types.reduce((multiplier, type) => multiplier * getTypeEffectiveness(target, type), 1) > 1,
  };
}

export function analyzeTeamTypes(
  team: readonly ({ type1: string; type2: string | null; attackTypes?: readonly PokemonType[] } | undefined)[],
  target: PokemonType,
) {
  return team.reduce((totals, pokemon) => {
    if (!pokemon) return totals;
    const result = analyzePokemonTypes(pokemon, target);
    const covered = result.weak && (pokemon.attackTypes?.some(type => getTypeEffectiveness(type, target) > 1) ?? false);
    return {
      weak: totals.weak + Number(result.weak),
      covered: totals.covered + Number(covered),
      superEffective: totals.superEffective + Number(result.superEffective),
    };
  }, { weak: 0, covered: 0, superEffective: 0 });
}
