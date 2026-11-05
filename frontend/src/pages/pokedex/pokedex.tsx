import { useEffect, useMemo, useRef, useState } from "react";
import pokedexIcon from "../../assets/brand/sections/pokedex_icon.png";
import cleanIcon from "../../assets/icons/pokedex/clean.svg";
import searchIcon from "../../assets/icons/pokedex/search.svg";
import sortIcon from "../../assets/icons/pokedex/sort.svg";
import Footer from "../../components/layout/footer";
import Header from "../../components/layout/header";
import Pagination from "../../components/common/pagination";
import type { PaginationChangeSource } from "../../components/common/pagination/pagination";
import StatisticSlider from "../../components/common/statisticSlider";

import { searchPokedexEntries } from "../../services/pokedexService";
import { BASE_STAT_DEFAULT, POKEDEX_STATS } from "../../constants/pokedex";
import type { PokedexEntry, PokedexStatFilter, PokedexTextFilters } from "../../types/pokedexTypes";
import { uppercaseInput } from "../../utils/uppercaseInput";
import "./pokedex-colors.css";
import "./pokedex.css";
import PokemonTypeCombobox from "../../components/common/pokemonTypeCombobox";
import PokedexCard from "../../components/cards/pokedexCard";
import { POKEDEX_SORT_GROUPS, sortPokedexEntries } from "../../utils/pokedexSort";
import type { PokedexSortCriterion, PokedexSortKey } from "../../utils/pokedexSort";

const POKEMON_PER_PAGE = 16;

const textFilters = [
  { name: "name", label: "Name", placeholder: "NAME", wide: true },
  { name: "type1", label: "Primary type", placeholder: "TYPE" },
  { name: "type2", label: "Secondary type", placeholder: "TYPE" },
  { name: "generation", label: "Generation", placeholder: "GENER." },
  { name: "region", label: "Region", placeholder: "REGION" },
  { name: "ability", label: "Ability", placeholder: "ABILITY", wide: true },
] as const;

function createStatFilters(): PokedexStatFilter[] {
  return POKEDEX_STATS.map(({ stat }) => ({ stat, value: BASE_STAT_DEFAULT, mode: "equal", enabled: false }));
}

function createTextFilters(): PokedexTextFilters {
  return { name: "", generation: "", region: "", ability: "" };
}

type PokedexLoadState =
  | { status: "loading" }
  | { status: "success"; entries: PokedexEntry[] }
  | { status: "error" };

/** General Pokedex: server data with loading, retry and empty states. */
export default function Pokedex() {
  const [loadState, setLoadState] = useState<PokedexLoadState>({ status: "loading" });
  const [attempt, setAttempt] = useState(0);
  const [currentPage, setCurrentPage] = useState(1);
  const [draftFilters, setDraftFilters] = useState(createStatFilters);
  const [appliedFilters, setAppliedFilters] = useState<PokedexStatFilter[]>([]);
  const [draftTextFilters, setDraftTextFilters] = useState(createTextFilters);
  const [appliedTextFilters, setAppliedTextFilters] = useState(createTextFilters);
  const [typeFilters, setTypeFilters] = useState({ type1: "", type2: "" });
  const [appliedTypeFilters, setAppliedTypeFilters] = useState<string[]>([]);
  const [typeFilterReset, setTypeFilterReset] = useState(0);
  const [sortCriteria, setSortCriteria] = useState<PokedexSortCriterion[]>([]);
  const [sortOpen, setSortOpen] = useState(false);
  const resultsRef = useRef<HTMLElement>(null);
  const sortContainerRef = useRef<HTMLDivElement>(null);
  const sortButtonRef = useRef<HTMLButtonElement>(null);

  const entries = loadState.status === "success" ? loadState.entries : [];
  const sortedEntries = useMemo(() => sortPokedexEntries(entries, sortCriteria), [entries, sortCriteria]);
  const totalPages = Math.ceil(entries.length / POKEMON_PER_PAGE);
  const page = Math.max(1, Math.min(currentPage, totalPages));
  const firstEntryIndex = (page - 1) * POKEMON_PER_PAGE;
  const visibleEntries = sortedEntries.slice(firstEntryIndex, firstEntryIndex + POKEMON_PER_PAGE);
  const hasAppliedFilters = appliedTypeFilters.length > 0 || appliedFilters.some(filter => filter.enabled) ||
    Object.values(appliedTextFilters).some(value => value.trim());

  useEffect(() => {
    const controller = new AbortController();

    searchPokedexEntries(appliedFilters, controller.signal, appliedTypeFilters, appliedTextFilters)
      .then((entries) => {
        if (!controller.signal.aborted) {
          setCurrentPage(1);
          setLoadState({ status: "success", entries });
        }
      })
      .catch(() => {
        if (!controller.signal.aborted) {
          setLoadState({ status: "error" });
        }
      });

    return () => controller.abort();
  }, [attempt, appliedFilters, appliedTypeFilters, appliedTextFilters]);

  useEffect(() => {
    if (!sortOpen) return;

    function closeOnOutsideClick(event: PointerEvent) {
      if (!sortContainerRef.current?.contains(event.target as Node)) setSortOpen(false);
    }
    function closeOnEscape(event: KeyboardEvent) {
      if (event.key === "Escape") {
        setSortOpen(false);
        sortButtonRef.current?.focus();
      }
    }
    document.addEventListener("pointerdown", closeOnOutsideClick);
    document.addEventListener("keydown", closeOnEscape);
    return () => {
      document.removeEventListener("pointerdown", closeOnOutsideClick);
      document.removeEventListener("keydown", closeOnEscape);
    };
  }, [sortOpen]);

  function toggleCriterion(key: PokedexSortKey) {
    setSortCriteria(previous => previous.some(criterion => criterion.key === key)
      ? previous.filter(criterion => criterion.key !== key)
      : [...previous, { key, direction: "ascending" }]);
    setCurrentPage(1);
  }

  function toggleDirection(key: PokedexSortKey) {
    setSortCriteria(previous => previous.map(criterion => criterion.key === key
      ? { ...criterion, direction: criterion.direction === "ascending" ? "descending" : "ascending" }
      : criterion));
    setCurrentPage(1);
  }

  function resetSort() {
    setSortCriteria([]);
    setCurrentPage(1);
  }

  function search() {
    setLoadState({ status: "loading" });
    setCurrentPage(1);
    setAppliedFilters(draftFilters.map(filter => ({ ...filter })));
    setAppliedTextFilters({ ...draftTextFilters });
    setAppliedTypeFilters(Object.values(typeFilters).filter(type => type.trim()));
  }

  function clean() {
    setDraftFilters(createStatFilters());
    setAppliedFilters([]);
    setDraftTextFilters(createTextFilters());
    setAppliedTextFilters(createTextFilters());
    setTypeFilters({ type1: "", type2: "" });
    setAppliedTypeFilters([]);
    setTypeFilterReset(previous => previous + 1);
    setLoadState({ status: "loading" });
    setCurrentPage(1);
  }

  function retry() {
    setLoadState({ status: "loading" });
    setAttempt((previous) => previous + 1);
  }

  function changePage(nextPage: number, source: PaginationChangeSource = "keyboard") {
    if (nextPage < 1 || nextPage > totalPages || nextPage === page) return;
    setCurrentPage(nextPage);

    const isLargeScreen = window.matchMedia("(min-width: 75rem)").matches;
    
    if (!isLargeScreen || source === "keyboard") {
      resultsRef.current?.focus({ preventScroll: true });
    }
  }

  return (
    <div className="pokedex-page">
      <Header />

      <main className="pokedex-page__main">
        <div className="pokedex-page__heading">
          <h1 aria-label="Pokedex">
            <button
              className="pokedex-page__reset"
              type="reset"
              form="pokedex-filters"
              aria-label="Reset Pokedex search"
              title="Reset Pokedex search">
              <img className="pokedex-page__logo" src={pokedexIcon} alt="" />
              Poké<span>Dex</span>
            </button>
          </h1>
        </div>

        <div className="pokedex-page__workspace">
          <form
            id="pokedex-filters"
            className="pokedex-filters"
            aria-label="Pokedex search filters"
            onReset={clean}
            onSubmit={event => { event.preventDefault(); search(); }}>
            <div className="pokedex-filters__text">
              {textFilters.map(({ name, label, placeholder, ...filter }) => (
                name === "type1" || name === "type2" ? (
                  <PokemonTypeCombobox
                    key={`${name}-${typeFilterReset}`}
                    name={name}
                    label={label}
                    value={typeFilters[name]}
                    excludedValue={typeFilters[name === "type1" ? "type2" : "type1"]}
                    onChange={value => setTypeFilters(previous => ({ ...previous, [name]: value }))}
                  />
                ) : (
                  <label
                    key={name}
                    className={"wide" in filter ? "pokedex-filters__field--wide" : undefined}>
                    <span className="visually-hidden">{label}</span>
                    <input
                      name={name}
                      type="text"
                      placeholder={placeholder}
                      autoComplete="off"
                      autoCapitalize="characters"
                      value={draftTextFilters[name]}
                      onChange={event => {
                        const value = uppercaseInput(event.currentTarget);
                        setDraftTextFilters(previous => ({ ...previous, [name]: value }));
                      }}
                    />
                  </label>
                )
              ))}
            </div>

            <div className="pokedex-filters__statistics" role="group" aria-label="Statistic filters">
              {draftFilters.map(filter => (
                <StatisticSlider
                  key={filter.stat}
                  label={POKEDEX_STATS.find(statistic => statistic.stat === filter.stat)!.label}
                  filter={filter}
                  onChange={updated => setDraftFilters(previous => previous.map(item => item.stat === updated.stat ? updated : item))}
                />
              ))}
            </div>

            <div className="pokedex-filters__actions">
              <button className="pokedex-button pokedex-button--clean" type="reset">
                <span>CLEAN</span>
                <img src={cleanIcon} alt="" />
              </button>
              <button
                className="pokedex-button pokedex-button--search"
                type="submit"
                disabled={loadState.status === "loading"}>
                <span>SEARCH</span>
                <img src={searchIcon} alt="" />
              </button>
            </div>
          </form>

          <section
            id="pokedex-results"
            ref={resultsRef}
            className="pokedex-results"
            tabIndex={-1}
            aria-label="Pokémon species"
            aria-busy={loadState.status === "loading"}>
            <div className="pokedex-results__toolbar">
              <div className="pokedex-sort" ref={sortContainerRef}>
                <button
                  ref={sortButtonRef}
                  className="pokedex-button pokedex-button--sort"
                  type="button"
                  aria-expanded={sortOpen}
                  aria-controls="pokedex-sort-panel"
                  onClick={() => setSortOpen(previous => !previous)}>
                  <span>SORT</span>
                  <img src={sortIcon} alt="" />
                </button>

                {sortOpen && (
                  <div className="pokedex-sort__panel" id="pokedex-sort-panel" role="region" aria-label="Sort Pokémon">
                    <button className="pokedex-sort__reset" type="button" disabled={sortCriteria.length === 0} onClick={resetSort}>Reset order</button>
                    <div className="pokedex-sort__content">
                      {POKEDEX_SORT_GROUPS.map(group => (
                        <fieldset className="pokedex-sort__group" key={group.label}>
                          <legend>{group.label}</legend>
                          <div className="pokedex-sort__options">
                            {group.options.map(option => {
                              const index = sortCriteria.findIndex(criterion => criterion.key === option.key);
                              const selected = index !== -1;
                              const direction = selected ? sortCriteria[index].direction : "ascending";
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
                  </div>
                )}
              </div>
            </div>

            {loadState.status === "loading" && (
              <p className="pokedex-results__message" role="status">Loading Pokémon…</p>
            )}

            {loadState.status === "error" && (
              <div className="pokedex-results__message">
                <p role="alert">Could not load the Pokémon. Please try again.</p>
                <button className="pokedex-button pokedex-button--retry" type="button" onClick={retry}>
                  RETRY
                </button>
              </div>
            )}

            {loadState.status === "success" && loadState.entries.length === 0 && (
              <p className="pokedex-results__message" role="status">
                {hasAppliedFilters ? "No Pokémon match the selected filters." : "No Pokémon available."}
              </p>
            )}

            {loadState.status === "success" && loadState.entries.length > 0 && (
              <>
                <p className="pokedex-results__summary" role="status">
                  {firstEntryIndex + 1}-{firstEntryIndex + visibleEntries.length} of {entries.length} Pokémon
                </p>
                <ul className="pokedex-results__grid">
                  {visibleEntries.map((pokemon) => (
                    <li key={pokemon.id} className="pokedex-results__slot">
                      <PokedexCard pokemon={pokemon} />
                    </li>
                  ))}
                </ul>
                <Pagination
                  currentPage={page}
                  totalPages={totalPages}
                  onPageChange={changePage}
                  ariaLabel="Pokedex pages"
                  controlsId="pokedex-results"
                />
              </>
            )}
          </section>
        </div>
      </main>
      <Footer />
    </div>
  );
}
