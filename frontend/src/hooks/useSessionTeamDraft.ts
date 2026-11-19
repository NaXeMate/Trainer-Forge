import { useCallback, useMemo, useSyncExternalStore } from "react";

import { useAuth } from "../context/useAuth";
import {
  getSessionTeamDraftRevision,
  getSessionTeamDraftSnapshot,
  readSessionTeamDraft,
  subscribeToSessionTeamDraft,
  writeSessionTeamDraft,
  type WorkingTeam,
} from "../services/sessionTeamDraft";
import type { Draft } from "../utils/teamDraft";

export function useSessionTeamDraftRevision() {
  return useSyncExternalStore(subscribeToSessionTeamDraft, getSessionTeamDraftRevision);
}

export function useSessionTeamDraft() {
  const { session } = useAuth();
  const ownerId = session.status === "authenticated" ? session.profile.id : null;
  const { serialized, storageAvailable, revision } = useSyncExternalStore(
    subscribeToSessionTeamDraft,
    getSessionTeamDraftSnapshot,
  );
  const working = useMemo(() =>
    ownerId === null ? null : readSessionTeamDraft(serialized, ownerId), [serialized, ownerId]);

  const replace = useCallback((next: WorkingTeam) =>
    ownerId !== null && writeSessionTeamDraft(ownerId, next, revision), [ownerId, revision]);

  function update(draft: Draft) {
    replace({ draft, origin: working?.origin ?? { kind: "new" }, dirty: true });
  }

  return { working, storageAvailable, replace, update };
}
