import type { Draft } from "../utils/teamDraft";

type DraftOrigin = { kind: "new" | "import" } | { kind: "saved"; id: number };

export type WorkingTeam = { draft: Draft; origin: DraftOrigin; dirty: boolean };

type StoredDraft = { ownerId: number; working: WorkingTeam };

const storageKey = "trainerforge:team-session-draft:v1";
const listeners = new Set<() => void>();
let fallback: string | null = null;
let storageAvailable = true;
let revision = 0;
let snapshot = { serialized: null as string | null, storageAvailable, revision };

export function subscribeToSessionTeamDraft(listener: () => void) {
  listeners.add(listener);
  return () => { listeners.delete(listener); };
}

export function getSessionTeamDraftRevision() {
  return revision;
}

export function getSessionTeamDraftSnapshot() {
  let serialized = fallback;
  let available = storageAvailable;
  if (available) {
    try {
      serialized = sessionStorage.getItem(storageKey);
    } catch {
      available = false;
    }
  }

  // React expects the same snapshot while the stored value and session are unchanged.
  if (snapshot.serialized !== serialized || snapshot.storageAvailable !== available || snapshot.revision !== revision) {
    snapshot = { serialized, storageAvailable: available, revision };
  }
  return snapshot;
}

export function readSessionTeamDraft(serialized: string | null, ownerId: number): WorkingTeam | null {
  if (serialized === null) return null;
  try {
    const stored = JSON.parse(serialized) as StoredDraft | null;
    return stored?.ownerId === ownerId ? stored.working : null;
  } catch {
    return null;
  }
}

export function writeSessionTeamDraft(ownerId: number, working: WorkingTeam, expectedRevision: number) {
  // An in-flight request from a closed session must not recreate its draft.
  if (expectedRevision !== revision) return false;
  const serialized = JSON.stringify({ ownerId, working } satisfies StoredDraft);
  try {
    sessionStorage.setItem(storageKey, serialized);
    storageAvailable = true;
    fallback = null;
  } catch {
    storageAvailable = false;
    fallback = serialized;
  }
  listeners.forEach(listener => listener());
  return true;
}

export function clearSessionTeamDraft() {
  revision++;
  fallback = null;
  try {
    sessionStorage.removeItem(storageKey);
    storageAvailable = true;
  } catch {
    storageAvailable = false;
  }
  listeners.forEach(listener => listener());
}
