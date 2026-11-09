export type EquippableItemType = "HELD_ITEM" | "MEGASTONE" | "Z_CRYSTAL";

export type EquippableItem = {
  name: string;
  type: EquippableItemType;
};
