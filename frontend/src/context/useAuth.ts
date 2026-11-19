import { createContext, useContext } from "react";

import type { AuthContextValue } from "../types/authTypes";

export const AuthContext = createContext<AuthContextValue | null>(null);

export function useAuth() {
  const context = useContext(AuthContext);

  if (!context) throw new Error("useAuth must be used inside AuthProvider.");

  return context;
}
