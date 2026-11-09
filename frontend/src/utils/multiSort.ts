export type SortDirection = "ascending" | "descending";
export type SortCriterion<Key extends string> = { key: Key; direction: SortDirection };

type SortValue = string | number | null;
const collator = new Intl.Collator(undefined, { sensitivity: "base", numeric: true });

export function sortByCriteria<Entry, Key extends string>(
  entries: readonly Entry[],
  criteria: readonly SortCriterion<Key>[],
  getValue: (entry: Entry, key: Key) => SortValue,
  compareTies?: (left: Entry, right: Entry) => number,
): Entry[] {
  return [...entries].sort((left, right) => {
    for (const { key, direction } of criteria) {
      const a = getValue(left, key);
      const b = getValue(right, key);
      if (a == null || b == null) {
        if (a !== b) return a == null ? 1 : -1;
        continue;
      }

      const comparison = typeof a === "number" && typeof b === "number"
        ? a - b
        : collator.compare(String(a), String(b));
      if (comparison !== 0) return direction === "ascending" ? comparison : -comparison;
    }

    return compareTies?.(left, right) ?? 0;
  });
}
