import { useId, useState } from "react";
import type { MouseEvent, SubmitEvent } from "react";
import "./pagination.css";

export type PaginationChangeSource = "pointer" | "keyboard";

export interface PaginationProps {
  currentPage: number;
  totalPages: number;
  onPageChange: (page: number, source?: PaginationChangeSource) => void;
  ariaLabel?: string;
  controlsId?: string;
}

function PageArrow({ forward = false, double = false }: { forward?: boolean; double?: boolean }) {
  return (
    <svg
      className={`pagination__arrow${forward ? " pagination__arrow--forward" : ""}`}
      viewBox="0 0 24 24"
      fill="none"
      stroke="currentColor"
      strokeWidth="2.5"
      strokeLinecap="round"
      strokeLinejoin="round"
      aria-hidden="true"
      focusable="false"
    >
      {double ? <path d="m11 6-6 6 6 6m8-12-6 6 6 6" /> : <path d="m15 6-6 6 6 6" />}
    </svg>
  );
}

function PageIndicator({
  page,
  totalPages,
  onPageChange,
  controlsId,
}: Omit<PaginationProps, "currentPage" | "ariaLabel"> & { page: number }) {
  const [value, setValue] = useState(String(page));
  const [invalid, setInvalid] = useState(false);
  const errorId = useId();

  function reset() {
    setValue(String(page));
    setInvalid(false);
  }

  function jumpToPage(event: SubmitEvent<HTMLFormElement>) {
    event.preventDefault();
    const text = value.trim();
    const nextPage = Number(text);
    if (!/^\d+$/.test(text) || !Number.isSafeInteger(nextPage) || nextPage < 1 || nextPage > totalPages) {
      setInvalid(true);
      return;
    }
    setValue(String(nextPage));
    setInvalid(false);
    onPageChange(nextPage, "keyboard");
  }

  return (
    <form className="pagination__jump" onSubmit={jumpToPage} noValidate>
      <input
        className="pagination__current"
        type="text"
        inputMode="numeric"
        autoComplete="off"
        aria-label="Current page"
        aria-current="page"
        aria-controls={controlsId}
        aria-invalid={invalid || undefined}
        aria-describedby={invalid ? errorId : undefined}
        title={`Enter a page from 1 to ${totalPages} and press Enter`}
        style={{ width: `${Math.max(2, String(totalPages).length) + 1}ch` }}
        value={value}
        onChange={event => { setValue(event.target.value); setInvalid(false); }}
        onBlur={reset}
        onKeyDown={event => {
          if (event.key === "Escape") { event.preventDefault(); reset(); }
        }}
      />
      {invalid && (
        <span id={errorId} className="pagination__error" role="alert">
          Enter a page from 1 to {totalPages}.
        </span>
      )}
    </form>
  );
}

/** Controlled pagination: the parent owns the data and the selected page. */
export default function Pagination({
  currentPage,
  totalPages,
  onPageChange,
  ariaLabel = "Pagination",
  controlsId,
}: PaginationProps) {
  if (totalPages <= 1) return null;

  const page = Math.max(1, Math.min(currentPage, totalPages));
  const pages = [page - 1, page, page + 1].filter(number => number >= 1 && number <= totalPages);

  function selectPage(nextPage: number, source: PaginationChangeSource = "keyboard") {
    if (nextPage >= 1 && nextPage <= totalPages && nextPage !== page) onPageChange(nextPage, source);
  }

  function focusButtonWithoutScrolling(event: MouseEvent<HTMLElement>) {
    if (event.button !== 0 || !window.matchMedia("(min-width: 75rem)").matches) return;
    const button = event.target instanceof Element ? event.target.closest("button") : null;
    if (!button || button.disabled) return;
    // Native mouse focus can scroll a button near the viewport edge before onClick.
    event.preventDefault();
    button.focus({ preventScroll: true });
  }

  return (
    <nav className="pagination" aria-label={ariaLabel} onMouseDown={focusButtonWithoutScrolling}>
      <ul className="pagination__list">
        <li>
          <button className="pagination__button" type="button" aria-label="First page" aria-controls={controlsId} disabled={page === 1} onClick={event => selectPage(1, event.detail === 0 ? "keyboard" : "pointer")}>
            <PageArrow double />
          </button>
        </li>
        <li>
          <button className="pagination__button" type="button" aria-label="Previous page" aria-controls={controlsId} disabled={page === 1} onClick={event => selectPage(page - 1, event.detail === 0 ? "keyboard" : "pointer")}>
            <PageArrow />
          </button>
        </li>
        {page > 2 && <li className="pagination__ellipsis" aria-hidden="true">…</li>}
        {pages.map(number => (
          <li key={number}>
            {number === page ? (
              <PageIndicator
                key={page}
                page={page}
                totalPages={totalPages}
                onPageChange={selectPage}
                controlsId={controlsId}
              />
            ) : (
              <button
                className="pagination__button"
                type="button"
                aria-label={`Page ${number}`}
                aria-controls={controlsId}
                onClick={event => selectPage(number, event.detail === 0 ? "keyboard" : "pointer")}
              >
                {number}
              </button>
            )}
          </li>
        ))}
        {page < totalPages - 1 && <li className="pagination__ellipsis" aria-hidden="true">…</li>}
        <li>
          <button className="pagination__button" type="button" aria-label="Next page" aria-controls={controlsId} disabled={page === totalPages} onClick={event => selectPage(page + 1, event.detail === 0 ? "keyboard" : "pointer")}>
            <PageArrow forward />
          </button>
        </li>
        <li>
          <button className="pagination__button" type="button" aria-label="Last page" aria-controls={controlsId} disabled={page === totalPages} onClick={event => selectPage(totalPages, event.detail === 0 ? "keyboard" : "pointer")}>
            <PageArrow forward double />
          </button>
        </li>
      </ul>
    </nav>
  );
}
