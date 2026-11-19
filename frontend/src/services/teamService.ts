import type { Draft, Member, Modality } from "../utils/teamDraft";

import { apiRequest } from "./api";

export type SavedTeam = {
  id: number;
  name: string;
  modality: Modality;
  hidden: boolean;
  trainerUsername: string;
  videogame: string;
};

type ApiMember = Omit<Member, "statPoints"> & {
  stats: {
    hp: number;
    attack: number;
    defense: number;
    specialAttack: number;
    specialDefense: number;
    speed: number
  };
};
type ApiLineup = Omit<Draft, "members"> & { members: (ApiMember | null)[] };
type ApiTeam = { id: number; trainerUsername: string; lineup: ApiLineup };

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

// Public seeded teams shown as P2 opponents.
const demoOwners = new Map([[1, "red"], [3, "liko"], [5, "rod"]]);

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

export async function getCompleteTeam(id: number, token: string, signal: AbortSignal, expectedOwner?: string) {
  const team = await apiRequest<ApiTeam>("GET", `/teams/${id}/complete`, undefined, signal, token);
  if (expectedOwner && team.trainerUsername !== expectedOwner) {
    throw new Error("You can only open your own saved teams.");
  }
  return fromTeam(team);
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
