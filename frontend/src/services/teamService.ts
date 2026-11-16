import { gameCatalogue } from "../constants/teamCatalogue";
import { TEAM_SIZE } from "../utils/teamDraft";
import type { Draft, Member, Modality } from "../utils/teamDraft";
import { ApiError, apiRequest } from "./api";

export type SavedTeam = {
  id: number;
  name: string;
  modality: Modality;
  hidden: boolean;
  trainerUsername: string;
  videogame: string;
};

type ApiMember = Omit<Member, "statPoints"> & {
  stats: { hp: number; attack: number; defense: number; specialAttack: number; specialDefense: number; speed: number };
};
type ApiLineup = Omit<Draft, "members"> & { members: (ApiMember | null)[] };
type ApiTeam = { id: number; trainerUsername: string; lineup: ApiLineup };
type LinkedMember = { pokemonId: string; position: number };
type LegacyPokemon = {
  species: number;
  ability: string;
  move1: string;
  move2: string | null;
  move3: string | null;
  move4: string | null;
  equippedItem: string | null;
  hpEv: number;
  attackEv: number;
  defenseEv: number;
  specialAttackEv: number;
  specialDefenseEv: number;
  speedEv: number;
  level: number;
  nature: string;
};

export type CompleteTeam = { id: number; trainerUsername: string; draft: Draft };

export type BattleSelection = { team: SavedTeam } | { draft: Draft };
export type BattleResult = {
  summary: string;
  outcome: "TEAM_ONE" | "TEAM_TWO" | "DRAW";
  teamOneScore: number;
  teamTwoScore: number;
  confidencePercentage: number;
};

type EligibleTeams = { own: SavedTeam[]; demo: SavedTeam[] };

// The same team/owner pairs that the battle API accepts from migration V4.
const demoOwners = new Map([[1, "red"], [2, "leaf"], [3, "liko"], [4, "may"], [5, "rod"]]);

function toLineup(draft: Draft): ApiLineup {
  return {
    ...draft,
    members: draft.members.map(member => {
      if (!member) return null;
      const { statPoints, ...data } = member;
      const { hp, attack, defense, speed } = statPoints;
      return {
        ...data,
        stats: { hp, attack, defense, speed, specialAttack: statPoints["special-attack"], specialDefense: statPoints["special-defense"] },
      };
    }),
  };
}

function fromTeam({ id, trainerUsername, lineup }: ApiTeam): CompleteTeam {
  const members = lineup.members.map(member => {
    if (!member) return null;
    const { stats, ...data } = member;
    return {
      ...data,
      statPoints: {
        hp: stats.hp, attack: stats.attack, defense: stats.defense, speed: stats.speed,
        "special-attack": stats.specialAttack, "special-defense": stats.specialDefense,
      },
    };
  });
  return { id, trainerUsername, draft: { ...lineup, members } };
}

export async function getMyTeams(username: string, token: string, signal: AbortSignal) {
  const teams = await apiRequest<SavedTeam[]>("GET", "/teams", undefined, signal, token);
  return teams.filter(team => team.trainerUsername === username);
}

async function getLinkedSlots(id: number, token: string, signal: AbortSignal) {
  try {
    return await apiRequest<LinkedMember[]>("GET", `/pokemon-teams/team/${id}`, undefined, signal, token);
  } catch (error) {
    // Access has already been checked by reading the team. An empty roster returns 404.
    if (!(error instanceof ApiError) || error.status !== 404) throw error;
    return [];
  }
}

export async function getCompleteTeam(id: number, token: string, signal: AbortSignal, options?: {
  expectedOwner?: string;
  onRequest?: () => void;
}) {
  const { expectedOwner, onRequest } = options ?? {};
  onRequest?.();
  const team = await apiRequest<SavedTeam>("GET", `/teams/${id}`, undefined, signal, token);
  assertOwner(team.trainerUsername, expectedOwner);
  onRequest?.();
  const slots = await getLinkedSlots(id, token, signal);
  const members = Array.from({ length: TEAM_SIZE }, () => null as Member | null);
  await Promise.all(slots.map(async ({ pokemonId, position }) => {
    if (position < 1 || position > TEAM_SIZE) return;
    onRequest?.();
    const pokemon = await apiRequest<LegacyPokemon>("GET", `/pokemon/${encodeURIComponent(pokemonId)}`, undefined, signal, token);
    members[position - 1] = {
      pokedexId: pokemon.species,
      moves: [pokemon.move1 ?? "", pokemon.move2 ?? "", pokemon.move3 ?? "", pokemon.move4 ?? ""],
      ability: pokemon.ability,
      item: pokemon.equippedItem ?? "",
      statPoints: {
        hp: pokemon.hpEv, attack: pokemon.attackEv, defense: pokemon.defenseEv,
        "special-attack": pokemon.specialAttackEv, "special-defense": pokemon.specialDefenseEv, speed: pokemon.speedEv,
      },
      level: pokemon.level,
      nature: pokemon.nature,
    };
  }));
  return {
    id, trainerUsername: team.trainerUsername,
    draft: {
      name: team.name, modality: team.modality, publicTeam: !team.hidden, members,
      gameId: gameCatalogue.find(game => game.name === team.videogame)?.id ?? null,
    },
  };
}

function assertOwner(actual: string, expected?: string) {
  if (expected && actual !== expected) throw new Error("You can only open your own saved teams.");
}

export async function saveCompleteTeam(draft: Draft, id: number | null, token: string, signal: AbortSignal) {
  const method = id === null ? "POST" : "PUT";
  const endpoint = id === null ? "/teams/complete" : `/teams/${id}/complete`;
  return fromTeam(await apiRequest<ApiTeam>(method, endpoint, toLineup(draft), signal, token));
}

export function deleteSavedTeam(id: number, token: string, signal: AbortSignal) {
  return apiRequest<void>("DELETE", `/teams/${id}`, undefined, signal, token);
}

export async function getEligibleTeams(username: string, token: string, signal: AbortSignal): Promise<EligibleTeams> {
  const teams = await apiRequest<SavedTeam[]>("GET", "/simulator/available-teams", undefined, signal, token);
  return {
    own: teams.filter(team => team.trainerUsername === username),
    demo: teams.filter(team => !team.hidden && team.trainerUsername !== username && demoOwners.get(team.id) === team.trainerUsername),
  };
}

export function simulateBattle(one: BattleSelection, two: BattleSelection, token: string, signal: AbortSignal) {
  const toSide = (side: BattleSelection) => "draft" in side ? { draft: toLineup(side.draft) } : { teamId: side.team.id };
  return apiRequest<BattleResult>("POST", "/simulator/battle", { one: toSide(one), two: toSide(two) }, signal, token);
}
