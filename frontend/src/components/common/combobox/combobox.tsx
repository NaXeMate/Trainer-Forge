import { useEffect, useId, useRef, useState } from "react";
import type { CSSProperties, KeyboardEvent, ReactNode } from "react";
import { useSelectionFeedback } from "../../../hooks/useSelectionFeedback";
import { uppercaseInput } from "../../../utils/uppercaseInput";
import "./combobox.css";

export type ComboboxOption = {
  value: string;
  label: string;
  color?: string;
};

type ComboboxProps = {
  inputId?: string;
  name?: string;
  className?: string;
  inputClassName?: string;
  label: string;
  placeholder?: string;
  value: string;
  options: readonly ComboboxOption[];
  readOnly?: boolean;
  leadingAdornment?: ReactNode;
  clearLabel?: string;
  excludedValue?: string;
  emptyMessage?: ReactNode;
  invalidMessage?: string;
  excludedMessage?: string;
  onChange: (value: string) => void;
  onInputChange?: (value: string) => void;
};

function normalize(text: string) {
  return text.trim().toLowerCase();
}

export default function Combobox({
  inputId,
  name,
  className,
  inputClassName = "combobox__input--default",
  label,
  placeholder,
  value,
  options,
  readOnly = false,
  leadingAdornment,
  clearLabel,
  excludedValue,
  emptyMessage = "No matching options.",
  invalidMessage = "Choose an option from the list.",
  excludedMessage = invalidMessage,
  onChange,
  onInputChange,
}: ComboboxProps) {
  const generatedId = useId();
  const id = inputId ?? generatedId;
  const inputRef = useRef<HTMLInputElement>(null);
  const listRef = useRef<HTMLUListElement>(null);
  const [open, setOpen] = useState(false);
  const [draftText, setDraftText] = useState<string | null>(null);
  const [activeValue, setActiveValue] = useState<string | null>(null);
  const [error, setError] = useState("");
  const { pendingSelection, selectWithFeedback, cancelSelection, getFeedbackProps } = useSelectionFeedback();

  const inputValue = draftText ?? value;
  const normalizedInput = normalize(inputValue);
  const query = draftText === null ? "" : normalizedInput;
  const selected = options.find(option => normalize(option.value) === normalizedInput ||
    normalize(option.label) === normalizedInput);
  const excluded = excludedValue === undefined ? undefined : normalize(excludedValue);
  const visibleOptions = options.filter(option => normalize(option.value) !== excluded && normalize(option.label).includes(query));
  const activeIndex = visibleOptions.findIndex(option => option.value === activeValue);

  useEffect(() => {
    if (open && activeIndex >= 0) {
      listRef.current?.children[activeIndex]?.scrollIntoView({ block: "nearest" });
    }
  }, [open, activeIndex]);

  function close() {
    setOpen(false);
    setDraftText(null);
    setActiveValue(null);
  }

  function select(optionValue: string) {
    if (optionValue !== value) onChange(optionValue);
    setError("");
    close();
  }

  function choose(option: ComboboxOption) {
    if (pendingSelection) return;
    setActiveValue(option.value);
    void selectWithFeedback(option.value, () => select(option.value));
  }

  function commit() {
    cancelSelection();
    if (draftText === null) {
      close();
    } else if (!inputValue.trim()) {
      select("");
    } else if (selected && normalize(selected.value) !== excluded) {
      select(selected.value);
    } else {
      // A consumer that keeps the live query also needs to discard it on invalid input.
      if (onInputChange) onChange("");
      setError(selected ? excludedMessage : invalidMessage);
      close();
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
      const count = visibleOptions.length;
      if (!count) {
        setActiveValue(null);
        return;
      }
      const next = event.key === "ArrowDown"
        ? (activeIndex + 1) % count
        : (activeIndex < 0 ? count - 1 : (activeIndex - 1 + count) % count);
      setActiveValue(visibleOptions[next].value);
    } else if (event.key === "Enter" && open) {
      event.preventDefault();
      const option = visibleOptions[activeIndex] ?? visibleOptions.find(entry => entry.value === selected?.value);
      if (option) choose(option);
      else commit();
    } else if (event.key === "Escape") {
      event.preventDefault();
      commit();
    }
  }

  return (
    <div
      className={className ? `combobox ${className}` : "combobox"}
      onBlur={event => {
        if (!event.currentTarget.contains(event.relatedTarget)) commit();
      }}
    >
      <div className="combobox__control">
        {leadingAdornment}
        <input
          ref={inputRef}
          id={id}
          className={`combobox__input ${inputClassName}`}
          name={name}
          type="text"
          role="combobox"
          aria-label={label}
          aria-autocomplete="list"
          aria-expanded={open}
          aria-controls={open ? `${id}-options` : undefined}
          aria-activedescendant={open && activeIndex >= 0 ? `${id}-option-${activeIndex}` : undefined}
          aria-describedby={error ? `${id}-error` : undefined}
          placeholder={placeholder}
          autoComplete="off"
          autoCapitalize="characters"
          spellCheck={false}
          readOnly={readOnly || pendingSelection !== null}
          value={inputValue}
          style={selected?.color ? { color: selected.color } : undefined}
          onFocus={() => setOpen(true)}
          onClick={() => setOpen(true)}
          onChange={event => {
            const text = uppercaseInput(event.currentTarget);
            setDraftText(text);
            onInputChange?.(text);
            setOpen(true);
            setActiveValue(null);
            setError("");
          }}
          onKeyDown={handleKeyDown}
        />
        {value && clearLabel && (
          <button
            className="combobox__clear"
            type="button"
            aria-label={clearLabel}
            onPointerDown={event => event.preventDefault()}
            onClick={() => {
              cancelSelection();
              inputRef.current?.focus();
              select("");
            }}
          >
            <svg aria-hidden="true" width="16" height="16" viewBox="0 0 16 16" fill="none" stroke="currentColor" strokeWidth="1.5" strokeLinecap="round">
              <path d="m4 4 8 8M12 4l-8 8" />
            </svg>
          </button>
        )}
        <button
          className="combobox__toggle"
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
              setDraftText(null);
              setActiveValue(null);
            }
          }}
        >
          <svg width="12" height="8" viewBox="0 0 12 8" aria-hidden="true">
            <path d="m1 1 5 5 5-5" fill="none" stroke="currentColor" strokeWidth="2" />
          </svg>
        </button>
      </div>
      {open && (
        <div className="combobox__dropdown">
          <ul ref={listRef} id={`${id}-options`} role="listbox" aria-label={`${label} options`}>
            {visibleOptions.map((option, index) => {
              const feedback = getFeedbackProps(option.value);
              return (
                <li
                  {...feedback}
                  key={option.value}
                  id={`${id}-option-${index}`}
                  role="option"
                  style={option.color ? { "--combobox-option-color": option.color, ...feedback.style } as CSSProperties : feedback.style}
                  aria-selected={selected?.value === option.value}
                  className={index === activeIndex ? `${feedback.className} combobox__option--active` : feedback.className}
                  onPointerDown={event => event.preventDefault()}
                  onClick={() => choose(option)}
                >
                  {option.label}
                </li>
              );
            })}
          </ul>
          {visibleOptions.length === 0 && <p role="status">{emptyMessage}</p>}
        </div>
      )}
      {error && <p id={`${id}-error`} className="combobox__error" role="status">{error}</p>}
    </div>
  );
}
