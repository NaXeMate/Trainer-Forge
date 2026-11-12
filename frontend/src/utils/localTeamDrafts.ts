import { convertLegacyEvs, EMPTY_STAT_POINTS, isValidStatPoints } from "./championsStatPoints";
import type { StatPoints } from "./championsStatPoints";

export const TEAM_SIZE = 6;

const STORAGE_KEY = "trainerforge:team-draft:v2";
const LEGACY_STORAGE_KEY = "trainerforge:team-draft:v1";

export type Member = { pokedexId: number; moves: [string, string, string, string]; ability: string; item: string; statPoints: StatPoints };
export type Draft = { name: string; modality: string; game: string; publicTeam: boolean; members: (Member | null)[] };
type StoredMember = Omit<Member, "statPoints"> & { statPoints?: unknown; evs?: unknown };
type StoredDraft = Omit<Draft, "members"> & { members: (StoredMember | null)[] };

// These Pokédex IDs refer to the public catalogue seeded by the migrations.
const EXAMPLES = {
  vanguard: {
    name: "VANGUARD",
    members: [[153, "Blaze"], [136, "Steadfast"], [130, "Sand-Veil"], [134, "Pressure"], [89, "Synchronize"], [33, "Intimidate"]],
  },
  rivals: {
    name: "RIVALS",
    members: [[235, "Overgrow"], [241, "Healer"], [242, "Tera-Shift"], [244, "Flash-Fire"], [211, "Torrent"], [187, "Frisk"]],
  },
} as const;

export type ExampleTeamId = keyof typeof EXAMPLES;
export type LocalTeamId = "draft" | ExampleTeamId;

export function isExampleTeamId(value: string | null): value is ExampleTeamId {
  return value === "vanguard" || value === "rivals";
}

function exampleDraft(id: ExampleTeamId): Draft {
  const example = EXAMPLES[id];
  return {
    ...emptyDraft(),
    name: example.name,
    modality: "SINGLES",
    members: example.members.map(([pokedexId, ability]) => ({
      pokedexId, ability, moves: ["", "", "", ""], item: "", statPoints: { ...EMPTY_STAT_POINTS },
    })),
  };
}

function storageKey(id: LocalTeamId): string {
  return id === "draft" ? STORAGE_KEY : `trainerforge:simulator-team:v1:${id}`;
}

export function writeTeamDraft(id: LocalTeamId, draft: Draft): void {
  try {
    localStorage.setItem(storageKey(id), JSON.stringify(draft));
  } catch {
    // The editor remains usable if browser storage is unavailable.
  }
}

export function emptyDraft(): Draft {
  return { name: "", modality: "", game: "", publicTeam: false, members: Array.from({ length: TEAM_SIZE }, () => null) };
}

export function readTeamDraft(id: LocalTeamId = "draft"): Draft {
  const fallback = id === "draft" ? emptyDraft() : exampleDraft(id);
  try {
    const saved = localStorage.getItem(storageKey(id)) ?? (id === "draft" ? localStorage.getItem(LEGACY_STORAGE_KEY) : null);
    const draft = JSON.parse(saved ?? "null") as StoredDraft | null;
    if (!draft || typeof draft.name !== "string" || typeof draft.modality !== "string" || typeof draft.game !== "string" || typeof draft.publicTeam !== "boolean" || !Array.isArray(draft.members) || draft.members.length !== TEAM_SIZE) return fallback;
    const valid = draft.members.every(member => member === null || (
      typeof member === "object" && Number.isSafeInteger(member.pokedexId) && member.pokedexId > 0 &&
      Array.isArray(member.moves) && member.moves.length === 4 && member.moves.every(move => typeof move === "string") &&
      typeof member.ability === "string" && typeof member.item === "string"
    ));
    if (!valid) return fallback;
    return {
      ...draft,
      members: draft.members.map(member => {
        if (!member) return null;
        const { pokedexId, moves, ability, item } = member;
        const statPoints = member.statPoints ?? convertLegacyEvs(member.evs);
        return {
          pokedexId, moves, ability, item,
          statPoints: isValidStatPoints(statPoints) ? statPoints : { ...EMPTY_STAT_POINTS },
        };
      }),
    };
  } catch { return fallback; }
}
