import type { SortCriterion } from "../../../utils/multiSort";
import "./sortOptions.css";

type SortGroup<Key extends string> = {
  label: string;
  options: readonly { key: Key; label: string; kind: "number" | "text" }[];
};

type SortOptionsProps<Key extends string> = {
  groups: readonly SortGroup<Key>[];
  criteria: readonly SortCriterion<Key>[];
  onChange: (criteria: SortCriterion<Key>[]) => void;
};

export default function SortOptions<Key extends string>({ groups, criteria, onChange }: SortOptionsProps<Key>) {
  function toggleCriterion(key: Key) {
    onChange(criteria.some(criterion => criterion.key === key)
      ? criteria.filter(criterion => criterion.key !== key)
      : [...criteria, { key, direction: "ascending" }]);
  }

  function toggleDirection(key: Key) {
    onChange(criteria.map(criterion => criterion.key === key
      ? { ...criterion, direction: criterion.direction === "ascending" ? "descending" : "ascending" }
      : criterion));
  }

  return (
    <>
      <button className="pokedex-sort__reset" type="button" disabled={criteria.length === 0} onClick={() => onChange([])}>Reset order</button>
      <div className="pokedex-sort__content">
        {groups.map(group => (
          <fieldset className="pokedex-sort__group" key={group.label}>
            <legend>{group.label}</legend>
            <div className="pokedex-sort__options">
              {group.options.map(option => {
                const index = criteria.findIndex(criterion => criterion.key === option.key);
                const selected = index !== -1;
                const direction = selected ? criteria[index].direction : "ascending";
                const directionLabel = option.kind === "text"
                  ? direction === "ascending" ? "A → Z" : "Z → A"
                  : direction === "ascending" ? "Low → High" : "High → Low";
                const directionText = option.kind === "text"
                  ? direction === "ascending" ? "A-Z" : "Z-A"
                  : direction === "ascending" ? "LOW-HIGH" : "HIGH-LOW";
                return (
                  <div className={`pokedex-sort__option${selected ? " pokedex-sort__option--selected" : ""}`} key={option.key}>
                    <label className="pokedex-sort__label">
                      <input type="checkbox" checked={selected} onChange={() => toggleCriterion(option.key)} />
                      <span>{option.label}</span>
                    </label>
                    {selected && (
                      <div className="pokedex-sort__selection">
                        <span className="pokedex-sort__priority" aria-label={`Priority ${index + 1}`}>{index + 1}</span>
                        <button
                          type="button"
                          className="pokedex-sort__direction"
                          aria-label={`${option.label}: ${directionLabel}. Change direction`}
                          title={`${directionLabel} · Change direction`}
                          onClick={() => toggleDirection(option.key)}>
                          <span>{directionText}</span>
                          <svg aria-hidden="true" viewBox="0 0 24 24" fill="none">
                            <path d="M4 8h15m0 0-4-4m4 4-4 4M20 16H5m0 0 4-4m-4 4 4 4" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" />
                          </svg>
                        </button>
                      </div>
                    )}
                  </div>
                );
              })}
            </div>
          </fieldset>
        ))}
      </div>
    </>
  );
}
