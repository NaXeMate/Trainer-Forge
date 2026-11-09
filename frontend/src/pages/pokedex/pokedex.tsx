import { useEffect, useLayoutEffect, useMemo, useRef, useState } from "react";
import { useLocation } from "react-router";
import pokedexIcon from "../../assets/brand/sections/pokedex_icon.png";
import cleanIcon from "../../assets/icons/pokedex/clean.svg";
import searchIcon from "../../assets/icons/pokedex/search.svg";
import sortIcon from "../../assets/icons/pokedex/sort.svg";
import Footer from "../../components/layout/footer";
import Header from "../../components/layout/header";
import Pagination from "../../components/common/pagination";
import type { PaginationChangeSource } from "../../components/common/pagination/pagination";
import StatisticSlider from "../../components/common/statisticSlider";
import SortOptions from "../../components/common/sortOptions";

import { searchPokedexEntries } from "../../services/pokedexService";
import { BASE_STAT_DEFAULT, BASE_STAT_MIN, BASE_STAT_MAX, POKEDEX_STATS } from "../../constants/pokedex";
import type { PokedexEntry, PokedexStatFilter, PokedexTextFilters } from "../../types/pokedexTypes";
import { uppercaseInput } from "../../utils/uppercaseInput";
import "./pokedex-colors.css";
import "./pokedex.css";
import PokemonTypeCombobox from "../../components/common/pokemonTypeCombobox";
import PokedexCard from "../../components/cards/pokedexCard";
import { POKEDEX_SORT_GROUPS, sortPokedexEntries } from "../../utils/pokedexSort";
import type { PokedexSortCriterion } from "../../utils/pokedexSort";

const POKEMON_PER_PAGE = 16;
const EMPTY_POKEDEX_ENTRIES: PokedexEntry[] = [];
const POKEDEX_VIEW_STATE_KEY = "trainerforge:pokedex-view:";
const POKEDEX_SORT_KEYS = new Set<string>(
  POKEDEX_SORT_GROUPS.flatMap(group => group.options.map(option => option.key)),
);

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

interface PokedexViewState {
  currentPage: number;
  draftFilters: PokedexStatFilter[];
  appliedFilters: PokedexStatFilter[];
  draftTextFilters: PokedexTextFilters;
  appliedTextFilters: PokedexTextFilters;
  typeFilters: { type1: string; type2: string };
  appliedTypeFilters: string[];
  sortCriteria: PokedexSortCriterion[];
}

function isRecord(value: unknown): value is Record<string, unknown> {
  return typeof value === "object" && value !== null && !Array.isArray(value);
}

function isPokedexStatFilter(value: unknown): value is PokedexStatFilter {
  return isRecord(value) &&
    POKEDEX_STATS.some(({ stat }) => stat === value.stat) &&
    typeof value.value === "number" && Number.isFinite(value.value) && value.value >= 0 && value.value <= 255 &&
    (value.mode === "equal" || value.mode === "minimum" || value.mode === "maximum") &&
    typeof value.enabled === "boolean";
}

function readStatFilters(value: unknown): PokedexStatFilter[] | undefined {
  if (!Array.isArray(value) || !value.every(isPokedexStatFilter)) return undefined;

  const filtersByStat = new Map(value.map(filter => [filter.stat, filter]));
  if (POKEDEX_STATS.some(({ stat }) => !filtersByStat.has(stat))) return undefined;

  return POKEDEX_STATS.flatMap(({ stat }) => {
    const filter = filtersByStat.get(stat);
    return filter ? [filter] : [];
  });
}

function isPokedexTextFilters(value: unknown): value is PokedexTextFilters {
  return isRecord(value) &&
    typeof value.name === "string" &&
    typeof value.generation === "string" &&
    typeof value.region === "string" &&
    typeof value.ability === "string";
}

function isSortCriterion(value: unknown): value is PokedexSortCriterion {
  return isRecord(value) && typeof value.key === "string" && POKEDEX_SORT_KEYS.has(value.key) &&
    (value.direction === "ascending" || value.direction === "descending");
}

function readSavedViewState(entryKey: string): Partial<PokedexViewState> {
  try {
    const serialized = window.sessionStorage.getItem(`${POKEDEX_VIEW_STATE_KEY}${entryKey}`);
    if (!serialized) return {};

    const saved: unknown = JSON.parse(serialized);
    if (!isRecord(saved)) return {};

    const state: Partial<PokedexViewState> = {};
    if (typeof saved.currentPage === "number" && Number.isSafeInteger(saved.currentPage) && saved.currentPage > 0) {
      state.currentPage = saved.currentPage;
    }

    const draftFilters = readStatFilters(saved.draftFilters);
    const appliedFilters = readStatFilters(saved.appliedFilters);
    if (draftFilters) state.draftFilters = draftFilters;
    if (appliedFilters) state.appliedFilters = appliedFilters;
    if (isPokedexTextFilters(saved.draftTextFilters)) state.draftTextFilters = saved.draftTextFilters;
    if (isPokedexTextFilters(saved.appliedTextFilters)) state.appliedTextFilters = saved.appliedTextFilters;
    if (isRecord(saved.typeFilters) && typeof saved.typeFilters.type1 === "string" && typeof saved.typeFilters.type2 === "string") {
      state.typeFilters = { type1: saved.typeFilters.type1, type2: saved.typeFilters.type2 };
    }
    if (Array.isArray(saved.appliedTypeFilters) && saved.appliedTypeFilters.every(type => typeof type === "string")) {
      state.appliedTypeFilters = saved.appliedTypeFilters;
    }
    if (Array.isArray(saved.sortCriteria) && saved.sortCriteria.every(isSortCriterion)) {
      state.sortCriteria = saved.sortCriteria;
    }

    return state;
  } catch {
    return {};
  }
}

/** General Pokedex: server data with loading, retry and empty states. */
export default function Pokedex() {
  const location = useLocation();
  const [savedViewState] = useState(() => readSavedViewState(location.key));
  const [loadState, setLoadState] = useState<PokedexLoadState>({ status: "loading" });
  const [attempt, setAttempt] = useState(0);
  const [currentPage, setCurrentPage] = useState(savedViewState.currentPage ?? 1);
  const [draftFilters, setDraftFilters] = useState(() => savedViewState.draftFilters ?? createStatFilters());
  const [appliedFilters, setAppliedFilters] = useState<PokedexStatFilter[]>(() => savedViewState.appliedFilters ?? []);
  const [draftTextFilters, setDraftTextFilters] = useState(() => savedViewState.draftTextFilters ?? createTextFilters());
  const [appliedTextFilters, setAppliedTextFilters] = useState(() => savedViewState.appliedTextFilters ?? createTextFilters());
  const [typeFilters, setTypeFilters] = useState(() => savedViewState.typeFilters ?? { type1: "", type2: "" });
  const [appliedTypeFilters, setAppliedTypeFilters] = useState<string[]>(() => savedViewState.appliedTypeFilters ?? []);
  const [typeFilterReset, setTypeFilterReset] = useState(0);
  const [sortCriteria, setSortCriteria] = useState<PokedexSortCriterion[]>(() => savedViewState.sortCriteria ?? []);
  const [sortOpen, setSortOpen] = useState(false);
  const resultsRef = useRef<HTMLElement>(null);
  const sortContainerRef = useRef<HTMLDivElement>(null);
  const sortButtonRef = useRef<HTMLButtonElement>(null);
  const viewStateRef = useRef<PokedexViewState>({
    currentPage,
    draftFilters,
    appliedFilters,
    draftTextFilters,
    appliedTextFilters,
    typeFilters,
    appliedTypeFilters,
    sortCriteria,
  });

  useLayoutEffect(() => {
    viewStateRef.current = {
      currentPage,
      draftFilters,
      appliedFilters,
      draftTextFilters,
      appliedTextFilters,
      typeFilters,
      appliedTypeFilters,
      sortCriteria,
    };
  }, [currentPage, draftFilters, appliedFilters, draftTextFilters, appliedTextFilters, typeFilters, appliedTypeFilters, sortCriteria]);

  const entries = loadState.status === "success" ? loadState.entries : EMPTY_POKEDEX_ENTRIES;
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
          const lastPage = Math.max(1, Math.ceil(entries.length / POKEMON_PER_PAGE));
          setCurrentPage(previous => Math.min(previous, lastPage));
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

  useEffect(() => () => {
    try {
      window.sessionStorage.setItem(
        `${POKEDEX_VIEW_STATE_KEY}${location.key}`,
        JSON.stringify(viewStateRef.current),
      );
    } catch {
      // The page can still work when browser storage is unavailable.
    }
  }, [location.key]);

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
    setSortCriteria([]);
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
              Poke<span>Dex</span>
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
                  min={BASE_STAT_MIN}
                  max={BASE_STAT_MAX}
                  filter={filter}
                  showHelp={filter.stat === "hp"}
                  applyHint="Check to include this statistic in SEARCH."
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
                    <SortOptions
                      groups={POKEDEX_SORT_GROUPS}
                      criteria={sortCriteria}
                      onChange={criteria => {
                        setSortCriteria(criteria);
                        setCurrentPage(1);
                      }}
                    />
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
