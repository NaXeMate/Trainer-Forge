import { POKEMON_TYPES } from "../constants/pokedex";
import type { MoveOption } from "../types/moveTypes";
import type { PokedexEntry } from "../types/pokedexTypes";
import { commonMonotypeTypes, TEAM_SIZE, type Draft } from "../utils/teamDraft";
import {
  analyzeTeamTypes,
  getAttackingTypesByMove,
  getDefensiveMultiplier,
  getPokemonTypes,
  getTeamTypeProfiles,
} from "../utils/typeEffectiveness";

import { apiRequest } from "./api";
import { getEquippableItems } from "./itemService";
import { getMoves } from "./moveService";
import { getPokedexEntries } from "./pokedexService";
import type { BattleResult } from "./teamService";

export type AchievementProgress = {
  id: number;
  name: string;
  description: string;
  unlocked: boolean;
};

type TeamSaved = {
  type: "teamSaved";
  draft: Draft;
  pokedex: Map<number, PokedexEntry>;
  moves?: readonly MoveOption[];
};

type BattleFinished = {
  type: "battleFinished";
  result: BattleResult;
  player: Draft;
  opponent: Draft;
  opponentKey: string;
  pokedex: Map<number, PokedexEntry>;
};

type AchievementEvent = TeamSaved | BattleFinished;
const endpoint = "/trainers/me/achievements";

// Catalogue IDs from Flyway V4 and V15.
const ids = {
  routeOne: 1,
  readyForBattle: 2,
  gymLeaderFan: 3,
  oldDays: 4,
  risingVoltTackler: 5,
  trainYourDragon: 6,
  megaEvolutionHeir: 7,
  eiscuesHair: 8,
  oddTypeOut: 9,
  pokemonJourney: 10,
  letItGo: 11,
  gotYouCovered: 12,
  perfectlyBalanced: 13,
  shinySpectrum: 14,
  gottaPickEmAll: 15,
  tourDeForge: 16,
  holdPlease: 17,
  takeAStabAtIt: 18,
  jackOfFourTrades: 19,
  eeveeLeague: 20,
  oneMonArmy: 21,
  noItems: 22,
  mirrorMirror: 23,
  theUnderdex: 24,
};

const journeySpecies = [6, 448, 282, 359, 445, 718];
const kantoStarters = [3, 6, 9];
const eeveeEvolutions = new Set([134, 135, 136, 196, 197, 470, 471, 700]);

function getSpecies(draft: Draft, pokedex: Map<number, PokedexEntry>) {
  return draft.members.flatMap(member => {
    const pokemon = member && pokedex.get(member.pokedexId);
    return pokemon ? [pokemon] : [];
  });
}

function hasUniqueTypeCombination(species: PokedexEntry[], pokedex: Map<number, PokedexEntry>) {
  const counts = new Map<string, number>();
  const typeCombination = (pokemon: PokedexEntry) => getPokemonTypes(pokemon).sort().join("/");

  for (const pokemon of pokedex.values()) {
    const key = typeCombination(pokemon);
    counts.set(key, (counts.get(key) ?? 0) + 1);
  }

  return species.some(pokemon => counts.get(typeCombination(pokemon)) === 1);
}

function teamBaseStatTotal(species: PokedexEntry[]) {
  return species.reduce((total, pokemon) =>
    total + pokemon.hpBase + pokemon.attackBase + pokemon.defenseBase
    + pokemon.specialAttackBase + pokemon.specialDefenseBase + pokemon.speedBase, 0);
}

async function teamCandidates(event: TeamSaved, signal: AbortSignal) {
  const { draft } = event;
  const candidates: number[] = [ids.routeOne];
  const count = draft.members.filter(Boolean).length;

  if (!count) return candidates;

  const pokedex = draft.members.every(member => !member || event.pokedex.has(member.pokedexId))
    ? event.pokedex : new Map((await getPokedexEntries(signal)).map(entry => [entry.id, entry]));
  const species = getSpecies(draft, pokedex);
  const numbers = new Set(species.map(pokemon => pokemon.nationalPokedex));

  if (commonMonotypeTypes(draft.members, pokedex)?.length) candidates.push(ids.gymLeaderFan);

  if (species.length === count && species.every(pokemon => pokemon.region.trim().toLowerCase() === "kanto")) {
    candidates.push(ids.oldDays);
  }

  if (hasUniqueTypeCombination(species, pokedex)) candidates.push(ids.oddTypeOut);
  if (kantoStarters.every(number => numbers.has(number))) candidates.push(ids.gottaPickEmAll);

  if (count !== TEAM_SIZE) return candidates;

  candidates.push(ids.readyForBattle);

  if (journeySpecies.every(number => numbers.has(number))) candidates.push(ids.pokemonJourney);

  const heldItems = new Set(draft.members.map(member => member?.item.trim().toLowerCase()).filter(Boolean));

  if (heldItems.size === TEAM_SIZE) candidates.push(ids.holdPlease);
  if (species.length !== count) return candidates;

  const regions = new Set(species.map(pokemon => pokemon.region.trim().toLowerCase()).filter(Boolean));

  if (regions.size === TEAM_SIZE) candidates.push(ids.tourDeForge);
  if ([...numbers].filter(number => eeveeEvolutions.has(number)).length === TEAM_SIZE) {
    candidates.push(ids.eeveeLeague);
  }

  const types = species.flatMap(getPokemonTypes);

  if (new Set(types).size === types.length) candidates.push(ids.shinySpectrum);

  const moves = event.moves ?? await getMoves(signal);
  const profiles = getTeamTypeProfiles(draft.members, pokedex, getAttackingTypesByMove(moves));

  if (profiles.every(pokemon => pokemon && getPokemonTypes(pokemon).some(type => pokemon.attackTypes.includes(type)))) {
    candidates.push(ids.takeAStabAtIt);
  }

  if (profiles.some(pokemon => new Set(pokemon?.attackTypes).size === 4)) candidates.push(ids.jackOfFourTrades);

  const coverage = POKEMON_TYPES.map(({ name }) => ({ type: name, ...analyzeTeamTypes(profiles, name) }));

  if (coverage.every(row => row.weak === row.covered)) {
    candidates.push(ids.gotYouCovered);
    if (coverage.every(row => !row.weak || species.some(pokemon => getDefensiveMultiplier(row.type, pokemon) < 1))) {
      candidates.push(ids.perfectlyBalanced);
    }
  }

  return candidates;
}

async function battleCandidates(event: BattleFinished, signal: AbortSignal) {
  if (event.result.outcome !== "TEAM_ONE") return [];

  const { player, opponent, opponentKey, pokedex } = event;
  const candidates: number[] = [];

  if (opponentKey === "3" || opponentKey === "5") candidates.push(ids.risingVoltTackler);
  if (event.result.confidencePercentage <= 10) candidates.push(ids.eiscuesHair);

  const playerMembers = player.members.filter(member => member !== null);
  const opponentMembers = opponent.members.filter(member => member !== null);

  if (playerMembers.length === 1 && opponentMembers.length === 1) candidates.push(ids.oneMonArmy);

  const opponentSpecies = getSpecies(opponent, pokedex);

  if (playerMembers.length === TEAM_SIZE && opponentMembers.length === TEAM_SIZE) {
    if (playerMembers.every(member => !member.item.trim())) candidates.push(ids.noItems);

    const playerLineup = playerMembers.map(member => member.pokedexId).sort((a, b) => a - b);
    const opponentLineup = opponentMembers.map(member => member.pokedexId).sort((a, b) => a - b);
    if (playerLineup.every((id, index) => id === opponentLineup[index])) candidates.push(ids.mirrorMirror);

    const playerSpecies = getSpecies(player, pokedex);
    if (playerSpecies.length === TEAM_SIZE && opponentSpecies.length === TEAM_SIZE
      && teamBaseStatTotal(playerSpecies) < teamBaseStatTotal(opponentSpecies)) {
      candidates.push(ids.theUnderdex);
    }
  }

  const shared = player.modality === "MONOTYPE" ? commonMonotypeTypes(player.members, pokedex) : null;

  if (opponentKey === "preset:LANCE" && shared?.includes("DRAGON")) candidates.push(ids.trainYourDragon);
  if (shared?.includes("ICE")) {
    const resistant = opponentSpecies.filter(pokemon => getDefensiveMultiplier("ICE", pokemon) < 1);
    if (resistant.length >= 3) candidates.push(ids.letItGo);
  }

  if (opponentKey === "preset:KORRINA" && player.members.some(member => member?.item)) {
    const items = await getEquippableItems(signal);
    const megaStones = new Set(items.filter(item => item.type === "MEGASTONE").map(item => item.name.trim().toLowerCase()));
    if (player.members.some(member => member && megaStones.has(member.item.trim().toLowerCase()))) {
      candidates.push(ids.megaEvolutionHeir);
    }
  }

  return candidates;
}

export function getAchievementProgress(token: string, signal: AbortSignal) {
  return apiRequest<AchievementProgress[]>("GET", endpoint, undefined, signal, token);
}

export async function checkAchievements(event: AchievementEvent, token: string, signal: AbortSignal) {
  const achievementIds = event.type === "teamSaved"
    ? await teamCandidates(event, signal) : await battleCandidates(event, signal);

    if (!achievementIds.length || signal.aborted) return [];

    return apiRequest<AchievementProgress[]>("POST", endpoint, { achievementIds }, signal, token);
}

export function achievementNotice(unlocked: AchievementProgress[]) {
  if (!unlocked.length) return "";

  const label = unlocked.length === 1 ? "Achievement unlocked" : "Achievements unlocked";

  return label + ": " + unlocked.map(achievement => achievement.name).join(", ");
}
