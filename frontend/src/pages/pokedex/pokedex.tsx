import { useEffect, useState } from "react";
import pokedexIcon from "../../assets/brand/sections/pokédex_icon.png";
import cardRing from "../../assets/icons/pokedex/card-ring.svg";
import cleanIcon from "../../assets/icons/pokedex/clean.svg";
import searchIcon from "../../assets/icons/pokedex/search.svg";
import sortIcon from "../../assets/icons/pokedex/sort.svg";
import statCircle from "../../assets/icons/pokedex/stat-circle.svg";
import statHelp from "../../assets/icons/pokedex/stat-help.svg";
import selectorCircle from "../../assets/icons/pokedex/selector-circle.svg";
import selectorLeft from "../../assets/icons/pokedex/selector-left.svg";
import selectorRight from "../../assets/icons/pokedex/selector-right.svg";
import Footer from "../../components/layout/footer";
import Header from "../../components/layout/header";

import { getPokedexEntries } from "../../services/pokedexService";
import type { PokedexEntry } from "../../types/pokedexTypes";
import { getPokedexCardPresentation } from "./pokedexPresentation";
import "./pokedex-colors.css";
import "./pokedex.css";

const textFilters = [
  { name: "name", label: "Name", placeholder: "NAME", wide: true },
  { name: "type1", label: "Primary type", placeholder: "TYPE" },
  { name: "type2", label: "Secondary type", placeholder: "TYPE" },
  { name: "generation", label: "Generation", placeholder: "GEN." },
  { name: "region", label: "Region", placeholder: "REGION" },
  { name: "ability", label: "Ability", placeholder: "ABILITY", wide: true },
] as const;

const statistics = [
  { label: "HP", mode: "equal", icon: selectorCircle },
  { label: "ATTACK", mode: "minimum", icon: selectorRight },
  { label: "DEFENSE", mode: "equal", icon: selectorCircle },
  { label: "SP. ATK.", mode: "equal", icon: selectorCircle },
  { label: "SP. DEF.", mode: "equal", icon: selectorCircle },
  { label: "SPEED", mode: "maximum", icon: selectorLeft },
] as const;

/** Fixed visual preview: statistics do not participate in filtering yet. */
function StatisticPreview({ label, mode, icon }: (typeof statistics)[number]) {
  return (
    <div className={`pokedex-stat pokedex-stat--${mode}`}>
      <img className="pokedex-stat__indicator" src={statCircle} alt="" />
      <span className="pokedex-stat__label">{label}</span>
      {label === "HP" && (
        <img
          className="pokedex-stat__help"
          src={statHelp}
          alt=""
          title="Stat filters will be available later"
        />
      )}
      <div className="pokedex-stat__scale" aria-hidden="true">
        <span className="pokedex-stat__minimum">0</span>
        <div className="pokedex-stat__track">
          <img className="pokedex-stat__selector" src={icon} alt="" />
        </div>
        <span className="pokedex-stat__maximum">255</span>
        <span className="pokedex-stat__value">127</span>
      </div>
    </div>
  );
}

type PokedexLoadState =
  | { status: "loading" }
  | { status: "success"; entries: PokedexEntry[] }
  | { status: "error" };

/** General PokéDex: server data with loading, retry and empty states. */
export default function Pokedex() {
  const [loadState, setLoadState] = useState<PokedexLoadState>({ status: "loading" });
  const [attempt, setAttempt] = useState(0);

  useEffect(() => {
    const controller = new AbortController();

    getPokedexEntries(controller.signal)
      .then((entries) => {
        if (!controller.signal.aborted) {
          setLoadState({ status: "success", entries });
        }
      })
      .catch(() => {
        if (!controller.signal.aborted) {
          setLoadState({ status: "error" });
        }
      });

    return () => controller.abort();
  }, [attempt]);

  function retry() {
    setLoadState({ status: "loading" });
    setAttempt((previous) => previous + 1);
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
            onSubmit={(event) => event.preventDefault()}>
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

            <div className="pokedex-filters__statistics" aria-label="Stat filter design preview">
              {statistics.map((statistic) => (
                <StatisticPreview key={statistic.label} {...statistic} />
              ))}
            </div>

            <div className="pokedex-filters__actions">
              <button className="pokedex-button pokedex-button--clean" type="reset">
                <span>CLEAN</span>
                <img src={cleanIcon} alt="" />
              </button>
              <button
                className="pokedex-button pokedex-button--search"
                type="button"
                aria-disabled="true"
                title="Search will be available later">
                <span>SEARCH</span>
                <img src={searchIcon} alt="" />
              </button>
            </div>
          </form>

          <section
            className="pokedex-results"
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
              <p className="pokedex-results__message" role="status">No Pokémon available.</p>
            )}

            {loadState.status === "success" && loadState.entries.length > 0 && (
              <ul className="pokedex-results__grid">
                {loadState.entries.map((pokemon) => {
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
            )}
          </section>
        </div>
      </main>

      <Footer />
    </div>
  );
}
