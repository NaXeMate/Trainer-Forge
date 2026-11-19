import { POKEDEX_STATS } from "../constants/pokedex";
import type { Game } from "../constants/teamCatalogue";
import type { PokedexEntry } from "../types/pokedexTypes";

import {
  EMPTY_STAT_POINTS,
  hasValidStatPointValues,
  MAX_STAT_POINTS,
  type StatPoints,
} from "./championsStatPoints";
import { getPokemonTypes, type PokemonType } from "./typeEffectiveness";

export const TEAM_SIZE = 6;
export const TEAM_SCHEMA_VERSION = 1;
export const MODALITIES = ["NORMAL", "NUZLOCKE", "MONOTYPE", "THEMED"] as const;
export type Modality = typeof MODALITIES[number];

export type Member = {
  pokedexId: number;
  moves: [string, string, string, string];
  ability: string;
  item: string;
  statPoints: StatPoints;
  level?: number;
  nature?: string;
};

export type Draft = {
  name: string;
  modality: Modality;
  gameId: number | null;
  publicTeam: boolean;
  members: (Member | null)[];
};

export function emptyDraft(): Draft {
  return {
    name: "",
    modality: "NORMAL",
    gameId: null,
    publicTeam: false,
    members: Array.from({ length: TEAM_SIZE }, () => null),
  };
}

export function newMember(pokemon: PokedexEntry): Member {
  return {
    pokedexId: pokemon.id,
    moves: ["", "", "", ""],
    ability: pokemon.ability1,
    item: "",
    statPoints: { ...EMPTY_STAT_POINTS },
  };
}

export function commonMonotypeTypes(
  members: Draft["members"],
  pokedex: Map<number, PokedexEntry>,
  skip?: number,
): PokemonType[] | null {
  let shared: PokemonType[] | null = null;

  for (const [index, member] of members.entries()) {
    if (!member || index === skip) continue;

    const entry = pokedex.get(member.pokedexId);
    if (!entry) return [];

    const types = getPokemonTypes(entry);
    shared = shared === null ? types : shared.filter(type => types.includes(type));
  }

  return shared;
}

export function incompleteSlot(draft: Draft) {
  return draft.members.findIndex(member => member && (!member.ability.trim() || !member.moves[0].trim()));
}

type ImportCatalogue = {
  pokedex: Map<number, PokedexEntry>;
  moves?: ReadonlySet<string>;
  items?: ReadonlySet<string>;
  games: readonly Game[];
  natures: string[];
};

function requireRecord(value: unknown, message: string): Record<string, unknown> {
  if (!value || typeof value !== "object" || Array.isArray(value)) throw new Error(message);
  return value as Record<string, unknown>;
}

export function readTeamFile(source: unknown, catalogue: ImportCatalogue): {
  draft: Draft;
  modalityChanged: boolean
} {
  const data = requireRecord(source, "The file must contain a team object.");
  const legacy = data.schemaVersion === undefined;

  if (!legacy && data.schemaVersion !== TEAM_SCHEMA_VERSION) {
    throw new Error("This team file version is not supported.");
  }
  if (typeof data.name !== "string" || data.name.length > 100) {
    throw new Error("The team name must have up to 100 characters.");
  }
  if (typeof data.publicTeam !== "boolean") {
    throw new Error("The public team field must be true or false.");
  }

  const modalityChanged = legacy
    && typeof data.modality === "string"
    && ["SINGLES", "DOUBLES", ""].includes(data.modality);
  const modality = modalityChanged ? "NORMAL" : MODALITIES.find(value => value === data.modality);
  if (!modality) throw new Error("Choose a valid team modality.");

  let gameId: number | null = null;
  if (legacy && data.gameId == null) {
    if (typeof data.game !== "string") throw new Error("The game field is invalid.");
    const name = data.game.trim().toLowerCase();
    if (name) {
      const game = catalogue.games.find(game => game.name.toLowerCase() === name);
      if (!game) throw new Error(`Game not found: ${data.game}.`);
      gameId = game.id;
    }
  } else if (data.gameId !== null) {
    const game = catalogue.games.find(game => game.id === data.gameId);
    if (!game) throw new Error("The selected game is missing from the catalogue.");
    gameId = game.id;
  }

  if (!Array.isArray(data.members) || data.members.length !== TEAM_SIZE) {
    throw new Error("The file must contain exactly six slots.");
  }

  const { moves: knownMoves, items: knownItems } = catalogue;
  const members = data.members.map((value: unknown, index): Member | null => {
    if (value === null) return null;

    const prefix = `Slot ${index + 1}: `;
    const member = requireRecord(value, prefix + "expected a Pokémon object.");
    const pokemon = typeof member.pokedexId === "number" ? catalogue.pokedex.get(member.pokedexId) : undefined;
    if (!pokemon) throw new Error(prefix + "Pokédex entry not found.");

    if (!Array.isArray(member.moves) || member.moves.length !== 4 || !member.moves.every(move => typeof move === "string")) {
      throw new Error(prefix + "expected four move slots.");
    }
    const moves = member.moves as Member["moves"];
    const selectedMoves = moves.filter(Boolean);
    const unknownMove = knownMoves && selectedMoves.find(move => !knownMoves.has(move));
    if (unknownMove) throw new Error(prefix + `move not found: ${unknownMove}.`);
    if (new Set(selectedMoves).size !== selectedMoves.length) throw new Error(prefix + "moves must be different.");
    const { ability, item, level, nature } = member;
    if (typeof ability !== "string" || (ability && ![pokemon.ability1, pokemon.ability2, pokemon.hiddenAbility].includes(ability))) {
      throw new Error(prefix + "ability unavailable for this Pokémon.");
    }
    if (typeof item !== "string" || (item && knownItems && !knownItems.has(item))) {
      throw new Error(prefix + "item not found in the catalogue.");
    }
    if (level !== undefined && (typeof level !== "number" || !Number.isInteger(level) || level < 1 || level > 100)) {
      throw new Error(prefix + "level must be between 1 and 100.");
    }
    if (nature !== undefined && (typeof nature !== "string" || !catalogue.natures.includes(nature))) {
      throw new Error(prefix + "nature not found in the catalogue.");
    }

    let stats = member.statPoints;
    if (legacy && stats === undefined && Array.isArray(member.evs) && member.evs.length === POKEDEX_STATS.length) {
      const evs = member.evs;
      stats = Object.fromEntries(POKEDEX_STATS.map(({ stat }, index) => [stat, evs[index]]));
    }
    if (!hasValidStatPointValues(stats)) {
      throw new Error(prefix + `each of the six EV values must be an integer between 0 and ${MAX_STAT_POINTS}.`);
    }

    return { pokedexId: pokemon.id, moves, ability, item, statPoints: stats, level, nature };
  });

  if (modality === "MONOTYPE" && commonMonotypeTypes(members, catalogue.pokedex)?.length === 0) {
    throw new Error("All Pokémon in a Monotype team must share a type.");
  }

  return {
    draft: { name: data.name, modality, gameId, publicTeam: data.publicTeam, members },
    modalityChanged,
  };
}
