// Raw Pokédex entry as returned by GET /api/pokedex (matches backend PokedexDto)
export interface PokedexEntry {
  nationalPokedex: number;
  name: string;
  imageUrl: string;
  generation: number;
  region: string;
  pokemonClass: string;
  type1: string | null;
  type2: string | null;
  ability1: string | null;
  ability2: string | null;
  hiddenAbility: string | null;
  description: string | null;
  category: string | null;
  weight: number | null;
  height: number | null;
  hpBase: number;
  attackBase: number;
  defenseBase: number;
  specialAttackBase: number;
  specialDefenseBase: number;
  speedBase: number;
}

// One Pokémon card as displayed in the grid
export interface PokemonCard {
  id: number;
  name: string; // display name, e.g. "Chikorita"
  pokedexNumber: number; // national number, e.g. 152
  imageUrl: string; // sprite/image URL provided by the API
  types: string[]; // 1 or 2 type names, e.g. ["Grass"] or ["Fire","Flying"]
}

// Ability entry — regular abilities + one optional hidden ability
export interface PokemonAbility {
  name: string;
  isHidden: boolean; // true if this is the hidden ability
}

// Full detail object returned by GET /pokedex/:id
export interface PokemonDetail {
  // Identification
  id: number; // internal DB id (shown next to the name as "#187")
  pokedexNumber: number; // national Pokédex number (shown in the data table)
  name: string; // display name, uppercase in UI

  // Visual
  imageUrl: string; // sprite URL provided by the API
  types: string[]; // 1 or 2 type names

  // Flavor text and classification
  description: string; // from pokemonespecies.descripcion
  category: string; // e.g. "Sound Wave Pokemon"

  // Physical data
  height: number; // in decimetres from DB → display as metres (÷ 10), e.g. "1,5 m"
  weight: number; // in hectograms from DB → display as kg (÷ 10), e.g. "85,0 kg"

  // Taxonomy
  generation: string; // e.g. "VI Generation"

  // Abilities — list, last one may be hidden
  abilities: PokemonAbility[];

  // Base stats (values 0–255)
  hp: number;
  attack: number;
  defense: number;
  spAttack: number;
  spDefense: number;
  speed: number;
}

// Query params sent to the backend
export interface PokedexFilters {
  name?: string;
  type1?: string;
  type2?: string;
  generation?: string;
  region?: string;
  ability?: string;
  minHp?: number;
  maxHp?: number;
  minAttack?: number;
  maxAttack?: number;
  minDefense?: number;
  maxDefense?: number;
  minSpAtk?: number;
  maxSpAtk?: number;
  minSpDef?: number;
  maxSpDef?: number;
  minSpeed?: number;
  maxSpeed?: number;
  sortBy?: string;
  sortDir?: "ASC" | "DESC";
}
