import { apiRequest } from "./api";
import type { MoveOption } from "../types/moveTypes";

export async function getMoves(signal?: AbortSignal): Promise<MoveOption[]> {
  const moves = await apiRequest<MoveOption[]>("GET", "/simulator/moves", undefined, signal);
  if (!Array.isArray(moves)) throw new Error("Invalid move list response");
  return moves.sort((first, second) => first.name.localeCompare(second.name));
}
