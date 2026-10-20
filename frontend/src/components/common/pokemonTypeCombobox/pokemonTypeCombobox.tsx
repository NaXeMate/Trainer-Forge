import { useEffect, useId, useRef, useState } from "react";
import type { CSSProperties, KeyboardEvent } from "react";
import { useSelectionFeedback } from "../../../hooks/useSelectionFeedback";
import { findPokemonType, POKEMON_TYPES } from "../../../constants/pokedex";
import { getTypeColor } from "../../../utils/pokemonPresentation";
import { uppercaseInput } from "../../../utils/uppercaseInput";

import "./pokemonTypeCombobox.css";

interface PokemonTypeComboboxProps {
  name: string;
  label: string;
  value: string;
  excludedValue: string;
  onChange: (value: string) => void;
}

export default function PokemonTypeCombobox({ name, label, value, excludedValue, onChange }: PokemonTypeComboboxProps) {
  const id = useId();
  const inputRef = useRef<HTMLInputElement>(null);
  const listRef = useRef<HTMLUListElement>(null);
  const [open, setOpen] = useState(false);
  const [editing, setEditing] = useState(false);
  const [activeId, setActiveId] = useState<number | null>(null);
  const [error, setError] = useState("");
  const { pendingSelection, selectWithFeedback, cancelSelection, getFeedbackProps } = useSelectionFeedback();
  const selectedType = findPokemonType(value);
  const excludedType = findPokemonType(excludedValue);
  const query = editing ? value.trim().toLowerCase() : "";
  const options = POKEMON_TYPES.filter(type => type.id !== excludedType?.id && type.name.toLowerCase().includes(query));
  const activeType = options.find(type => type.id === activeId);

  useEffect(() => {
    if (open && activeType) {
      listRef.current?.querySelector(`[data-type-id="${activeType.id}"]`)?.scrollIntoView({ block: "nearest" });
    }
  }, [open, activeType]);

  function select(typeName: string) {
    onChange(typeName.toUpperCase());
    setError("");
    setOpen(false);
    setEditing(false);
    setActiveId(null);
  }

  function choose(type: (typeof POKEMON_TYPES)[number]) {
    if (pendingSelection) return;
    setActiveId(type.id);
    void selectWithFeedback(type.name, () => select(type.name));
  }

  function commit() {
    cancelSelection();
    const type = findPokemonType(value);
    if (!value.trim()) {
      select("");
    } else if (type && type.id !== excludedType?.id) {
      select(type.name);
    } else {
      onChange("");
      setError(type ? "Already selected in the other field." : "Choose one of the 18 types.");
      setOpen(false);
      setEditing(false);
      setActiveId(null);
    }
  }

  function handleKeyDown(event: KeyboardEvent<HTMLInputElement>) {
    if (event.nativeEvent.isComposing) return;
    if (pendingSelection && event.key !== "Escape") {
      if (event.key === "Enter" || event.key === "ArrowDown" || event.key === "ArrowUp") event.preventDefault();
      return;
    }

    if (event.key === "ArrowDown" || event.key === "ArrowUp") {
      event.preventDefault();
      setOpen(true);
      if (options.length === 0) {
        setActiveId(null);
        return;
      }
      const current = options.findIndex(type => type.id === activeId);
      const next = event.key === "ArrowDown"
        ? (current + 1) % options.length
        : (current < 0 ? options.length - 1 : (current - 1 + options.length) % options.length);
      setActiveId(options[next]?.id ?? null);
    } else if (event.key === "Enter" && open) {
      event.preventDefault();
      const option = activeType ?? options.find(type => type.id === selectedType?.id);
      if (option) choose(option);
      else commit();
    } else if (event.key === "Escape") {
      event.preventDefault();
      commit();
    }
  }

  return (
    <div
      className="pokedex-type-filter"
      onBlur={event => {
        if (!event.currentTarget.contains(event.relatedTarget)) commit();
      }}>
      <label className="visually-hidden" htmlFor={id}>{label}</label>
      <div className="pokedex-type-filter__control">
        <input
          ref={inputRef}
          id={id}
          name={name}
          type="text"
          role="combobox"
          aria-autocomplete="list"
          aria-expanded={open}
          aria-controls={open ? `${id}-options` : undefined}
          aria-activedescendant={open && activeType ? `${id}-option-${activeType.id}` : undefined}
          aria-describedby={error ? `${id}-error` : undefined}
          placeholder="TYPE"
          autoComplete="off"
          autoCapitalize="characters"
          spellCheck={false}
          readOnly={pendingSelection !== null}
          value={value}
          style={selectedType ? { color: getTypeColor(selectedType.name) } : undefined}
          onFocus={() => setOpen(true)}
          onClick={() => setOpen(true)}
          onChange={event => {
            onChange(uppercaseInput(event.currentTarget));
            setOpen(true);
            setEditing(true);
            setActiveId(null);
            setError("");
          }}
          onKeyDown={handleKeyDown}
        />
        <button
          className="pokedex-type-filter__toggle"
          type="button"
          tabIndex={-1}
          aria-label={`${open ? "Close" : "Open"} ${label.toLowerCase()} options`}
          aria-controls={open ? `${id}-options` : undefined}
          aria-expanded={open}
          onPointerDown={event => event.preventDefault()}
          onClick={() => {
            if (open) commit();
            else {
              inputRef.current?.focus();
              setOpen(true);
              setEditing(false);
              setActiveId(null);
            }
          }}>
          <svg width="12" height="8" viewBox="0 0 12 8" aria-hidden="true">
            <path d="m1 1 5 5 5-5" fill="none" stroke="currentColor" strokeWidth="2" />
          </svg>
        </button>
      </div>
      {open && (
        <div className="pokedex-type-filter__dropdown">
          <ul ref={listRef} id={`${id}-options`} role="listbox" aria-label={`${label} options`}>
            {options.map(type => {
              const feedback = getFeedbackProps(type.name);
              return (
                <li
                  {...feedback}
                  key={type.id}
                  id={`${id}-option-${type.id}`}
                  data-type-id={type.id}
                  role="option"
                  style={{ "--pokedex-option-color": getTypeColor(type.name), ...feedback.style } as CSSProperties}
                  aria-selected={selectedType?.id === type.id}
                  className={[feedback.className, activeType?.id === type.id && "pokedex-type-filter__option--active"].filter(Boolean).join(" ")}
                  onPointerDown={event => event.preventDefault()}
                  onClick={() => choose(type)}>
                  {type.name}
                </li>
              );
            })}
          </ul>
          {options.length === 0 && <p role="status">No matching types.</p>}
        </div>
      )}
      {error && <p id={`${id}-error`} className="pokedex-type-filter__error" role="status">{error}</p>}
    </div>
  );
}
