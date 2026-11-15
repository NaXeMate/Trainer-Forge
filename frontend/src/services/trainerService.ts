import type { TrainerProfile, TrainerProfileChanges } from "../types/trainerTypes";
import { apiRequest } from "./api";

export function getCurrentTrainer(token: string, signal: AbortSignal) {
  return apiRequest<TrainerProfile>("GET", "/trainers/me", undefined, signal, token);
}

export function updateCurrentTrainer(token: string, changes: TrainerProfileChanges) {
  return apiRequest<TrainerProfile>("PATCH", "/trainers/me", changes, undefined, token);
}
