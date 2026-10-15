import { useEffect, useRef, useState } from "react";
import pokedexIcon from "../../assets/brand/sections/pokédex_icon.png";
import cardRing from "../../assets/icons/pokedex/card-ring.svg";
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
import type { PokedexEntry, PokedexStatFilter } from "../../types/pokedexTypes";
import { getPokedexCardPresentation } from "./pokedexPresentation";
import "./pokedex-colors.css";
import "./pokedex.css";

const POKEMON_PER_PAGE = 16;

const textFilters = [
  { name: "name", label: "Name", placeholder: "NAME", wide: true },
  { name: "type1", label: "Primary type", placeholder: "TYPE" },
  { name: "type2", label: "Secondary type", placeholder: "TYPE" },
  { name: "generation", label: "Generation", placeholder: "GEN." },
  { name: "region", label: "Region", placeholder: "REGION" },
  { name: "ability", label: "Ability", placeholder: "ABILITY", wide: true },
] as const;

function createStatFilters(): PokedexStatFilter[] {
  return POKEDEX_STATS.map(({ stat }) => ({ stat, value: BASE_STAT_DEFAULT, mode: "equal", enabled: false }));
}

type PokedexLoadState =
  | { status: "loading" }
  | { status: "success"; entries: PokedexEntry[] }
  | { status: "error" };

/** General PokéDex: server data with loading, retry and empty states. */
export default function Pokedex() {
  const [loadState, setLoadState] = useState<PokedexLoadState>({ status: "loading" });
  const [attempt, setAttempt] = useState(0);
  const [currentPage, setCurrentPage] = useState(1);
  const [draftFilters, setDraftFilters] = useState(createStatFilters);
  const [appliedFilters, setAppliedFilters] = useState<PokedexStatFilter[]>([]);
  const resultsRef = useRef<HTMLElement>(null);

  const entries = loadState.status === "success" ? loadState.entries : [];
  const totalPages = Math.ceil(entries.length / POKEMON_PER_PAGE);
  const page = Math.max(1, Math.min(currentPage, totalPages));
  const firstEntryIndex = (page - 1) * POKEMON_PER_PAGE;
  const visibleEntries = entries.slice(firstEntryIndex, firstEntryIndex + POKEMON_PER_PAGE);

  useEffect(() => {
    const controller = new AbortController();

    searchPokedexEntries(appliedFilters, controller.signal)
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
  }, [attempt, appliedFilters]);

  function search() {
    setLoadState({ status: "loading" });
    setCurrentPage(1);
    setAppliedFilters(draftFilters.map(filter => ({ ...filter })));
  }

  function clean() {
    setDraftFilters(createStatFilters());
    setAppliedFilters([]);
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
    // Leave desktop mouse navigation in place; keep focus management for the keyboard.
    if (!isLargeScreen || source === "keyboard") {
      resultsRef.current?.focus({ preventScroll: true });
    }
  }

  return (
    <div className="pokedex-page">
      <Header />

      <main className="pokedex-page__main">
        <div className="pokedex-page__heading">
          <img className="pokedex-page__logo" src={pokedexIcon} alt="" />
          <h1>Poké<span>Dex</span></h1>
        </div>

        <div className="pokedex-page__workspace">
          <form
            className="pokedex-filters"
            aria-label="PokéDex search filters"
            onReset={clean}
            onSubmit={event => { event.preventDefault(); search(); }}>
            <div className="pokedex-filters__text">
              {textFilters.map(({ name, label, placeholder, ...filter }) => (
                <label
                  key={name}
                  className={"wide" in filter ? "pokedex-filters__field--wide" : undefined}>
                  <span className="visually-hidden">{label}</span>
                  <input name={name} type="text" placeholder={placeholder} autoComplete="off" />
                </label>
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
              <button
                className="pokedex-button pokedex-button--sort"
                type="button"
                aria-disabled="true"
                title="Sorting will be available later">
                <span>SORT</span>
                <img src={sortIcon} alt="" />
              </button>
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
                {appliedFilters.some(filter => filter.enabled) ? "No Pokémon match the selected statistics." : "No Pokémon available."}
              </p>
            )}

            {loadState.status === "success" && loadState.entries.length > 0 && (
              <>
                <p className="pokedex-results__summary" role="status">
                  Page {page} of {totalPages} · {firstEntryIndex + 1}–{firstEntryIndex + visibleEntries.length} of {entries.length} Pokémon
                </p>
                <ul className="pokedex-results__grid">
                  {visibleEntries.map((pokemon) => {
                    const presentation = getPokedexCardPresentation(pokemon);

                    return (
                      <li key={pokemon.id} className="pokedex-results__slot">
                        <article className={presentation.className} style={presentation.style}>
                          <img className="pokedex-card__decoration" src={cardRing} alt="" />
                          <span className="pokedex-card__regional-number">
                            #{String(pokemon.id).padStart(3, "0")}
                          </span>
                          <img
                            className="pokedex-card__image"
                            src={pokemon.imageUrl}
                            alt={pokemon.name}
                            loading="lazy"
                          />
                          <div className="pokedex-card__caption">
                            <span className="pokedex-card__national-number">
                              #{String(pokemon.nationalPokedex).padStart(4, "0")}
                            </span>
                            <h2 className={pokemon.name.length > 10 ? "pokedex-card__name--long" : undefined}>
                              {pokemon.name}
                            </h2>
                          </div>
                        </article>
                      </li>
                    );
                  })}
                </ul>
                <Pagination
                  currentPage={page}
                  totalPages={totalPages}
                  onPageChange={changePage}
                  ariaLabel="PokéDex pages"
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
