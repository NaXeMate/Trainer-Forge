import { useCallback, useEffect, useRef, useState, type ReactNode } from "react";

import { ApiError } from "../services/api";
import { login, register } from "../services/authService";
import { clearSessionTeamDraft } from "../services/sessionTeamDraft";
import { getCurrentTrainer, updateCurrentTrainer } from "../services/trainerService";
import type { AuthResponse, AuthSession, LoginInput, RegisterInput } from "../types/authTypes";
import type { TrainerProfileChanges } from "../types/trainerTypes";

import { AuthContext } from "./useAuth";

const TOKEN_KEY = "trainerforge:auth-token";

function expiresAt(token: string): number {
  try {
    const payload = token.split(".")[1].replace(/-/g, "+").replace(/_/g, "/");
    const { exp } = JSON.parse(atob(payload)) as { exp?: unknown };

    return typeof exp === "number" && Number.isFinite(exp) ? exp * 1000 : 0;
  } catch {
    return 0;
  }
}

function readToken() {
  try {
    return sessionStorage.getItem(TOKEN_KEY);
  } catch {
    return null;
  }
}

function persistToken(token: string | null) {
  try {
    if (token) sessionStorage.setItem(TOKEN_KEY, token);
    else sessionStorage.removeItem(TOKEN_KEY);
  } catch {
    // Storage is optional. The provider still manages the session in memory.
  }
}

export function AuthProvider({ children }: { children: ReactNode }) {
  const [token, setToken] = useState(readToken);
  const [session, setSession] = useState<AuthSession>(() => ({ status: token ? "checking" : "guest" }));
  const [attempt, setAttempt] = useState(0);
  const generation = useRef(0);

  const logout = useCallback(() => {
    generation.current++;
    persistToken(null);
    clearSessionTeamDraft();
    setToken(null);
    setSession({ status: "guest" });
  }, []);

  useEffect(() => {
    function onRestore(event: PageTransitionEvent) {
      if (event.persisted) clearSessionTeamDraft();
    }
    window.addEventListener("pagehide", clearSessionTeamDraft);
    window.addEventListener("pageshow", onRestore);
    return () => {
      window.removeEventListener("pagehide", clearSessionTeamDraft);
      window.removeEventListener("pageshow", onRestore);
    };
  }, []);

  function retry() {
    if (session.status !== "error") return;
    setSession({ status: "checking" });
    setAttempt(previous => previous + 1);
  }

  useEffect(() => {
    if (!token) return;

    const controller = new AbortController();
    const currentGeneration = generation.current;
    const expiration = expiresAt(token);
    let timer: number;
    const isCurrent = () => !controller.signal.aborted && generation.current === currentGeneration;

    function checkExpiration() {
      if (!isCurrent()) return;

      window.clearTimeout(timer);
      const remaining = expiration - Date.now();

      if (remaining <= 0) logout();
      else timer = window.setTimeout(checkExpiration, Math.min(remaining, 2_147_483_647));
    }

    checkExpiration();
    if (isCurrent()) {
      getCurrentTrainer(token, controller.signal).then(profile => {
        if (isCurrent()) setSession({ status: "authenticated", profile });
      }).catch(error => {
        if (!isCurrent()) return;

        if (error instanceof ApiError && error.status === 401) logout();
        else setSession({ status: "error" });
      });
      window.addEventListener("focus", checkExpiration);
      document.addEventListener("visibilitychange", checkExpiration);
    }

    return () => {
      controller.abort();
      window.clearTimeout(timer);
      window.removeEventListener("focus", checkExpiration);
      document.removeEventListener("visibilitychange", checkExpiration);
    };
  }, [token, attempt, logout]);

  async function authenticate(request: () => Promise<AuthResponse>) {
    const currentGeneration = ++generation.current;
    const response = await request();

    if (generation.current !== currentGeneration) return;
    clearSessionTeamDraft();
    persistToken(response.token);
    setSession({ status: "checking" });
    setToken(response.token);
    setAttempt(previous => previous + 1);
  }

  async function updateProfile(changes: TrainerProfileChanges) {
    if (!token || session.status !== "authenticated") throw new Error("Please log in to update your profile.");
    const currentGeneration = generation.current;

    try {
      const profile = await updateCurrentTrainer(token, changes);
      if (generation.current === currentGeneration) setSession({ status: "authenticated", profile });
    } catch (error) {
      if (generation.current === currentGeneration && error instanceof ApiError && error.status === 401) logout();
      throw error;
    }
  }

  return (
    <AuthContext.Provider value={{
      session, retry, logout, updateProfile,
      accessToken: session.status === "authenticated" ? token : null,
      login: (input: LoginInput) => authenticate(() => login(input)),
      register: (input: RegisterInput) => authenticate(() => register(input)),
    }}>
      {children}
    </AuthContext.Provider>
  );
}
