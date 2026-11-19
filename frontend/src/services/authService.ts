import type { AuthResponse, LoginInput, RegisterInput } from "../types/authTypes";

import { apiRequest } from "./api";

export function login(input: LoginInput) {
  return apiRequest<AuthResponse>("POST", "/auth/login", input);
}

export function register(input: RegisterInput) {
  return apiRequest<AuthResponse>("POST", "/auth/register", { ...input, trainerClass: "NOVICE" });
}
