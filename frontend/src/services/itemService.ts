import { apiRequest } from "./api";
import type { EquippableItem } from "../types/itemTypes";

export async function getEquippableItems(signal?: AbortSignal): Promise<EquippableItem[]> {
  const items = await apiRequest<EquippableItem[]>("GET", "/simulator/items", undefined, signal);
  if (!Array.isArray(items) || !items.every(item =>
    typeof item?.name === "string" && item.name.trim() !== "" &&
    (item.type === "HELD_ITEM" || item.type === "MEGASTONE" || item.type === "Z_CRYSTAL")
  )) throw new Error("Invalid item catalogue response");
  return items.sort((first, second) => first.name.localeCompare(second.name));
}
