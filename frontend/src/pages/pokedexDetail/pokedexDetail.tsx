import { useEffect, useState } from "react";
import type { CSSProperties } from "react";
import { Link, useNavigate, useParams } from "react-router";
import Footer from "../../components/layout/footer";
import Header from "../../components/layout/header";
import TypeBadge from "../../components/common/pokemonTypeBadge";
import { getTypeColor } from "../../utils/pokemonPresentation";
import { ApiError } from "../../services/api";
import { getPokedexEntryById } from "../../services/pokedexService";
import type { PokedexEntry } from "../../types/pokedexTypes";
import "./pokedexDetail.css";

const GENERATION_NUMERALS = ["I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX"];
const BASE_STAT_ROWS = [
  { label: "HP", key: "hpBase" },
  { label: "ATTACK", key: "attackBase" },
  { label: "DEFENSE", key: "defenseBase" },
  { label: "SP. ATTACK", key: "specialAttackBase" },
  { label: "SP. DEFENSE", key: "specialDefenseBase" },
  { label: "SPEED", key: "speedBase" },
] as const;

type BaseStatTone = "lowest" | "lower" | "low" | "medium" | "high" | "higher" | "highest";

type DetailLoadState =
  | { entryId: number; status: "loading" }
  | { entryId: number; status: "success"; pokemon: PokedexEntry }
  | { entryId: number; status: "not-found" }
  | { entryId: number; status: "error" };

type AdjacentPokemon = Pick<PokedexEntry, "id" | "name">;

interface AdjacentPokemonState {
  entryId: number;
  previous: AdjacentPokemon | null;
  next: AdjacentPokemon | null;
}

function getBaseStatTone(value: number): BaseStatTone {
  if (value <= 40) return "lowest";
  if (value <= 60) return "lower";
  if (value <= 80) return "low";
  if (value <= 100) return "medium";
  if (value <= 120) return "high";
  if (value <= 150) return "higher";
  return "highest";
}

function formatMeasurement(value: number, unit: string): string {
  const formattedValue = new Intl.NumberFormat("es-ES", {
    minimumFractionDigits: 1,
    maximumFractionDigits: 1,
  }).format(value);

  return `${formattedValue} ${unit}`;
}

const STAT_ANIMATION_DURATION = 1450;

function BaseStatsChart({ pokemon }: { pokemon: PokedexEntry }) {
  const [animation, setAnimation] = useState({ pokemonId: pokemon.id, progress: 0 });

  useEffect(() => {
    const prefersReducedMotion = window.matchMedia("(prefers-reduced-motion: reduce)").matches;
    let startTime: number | undefined;
    let frameId = 0;

    function animate(timestamp: number) {
      if (prefersReducedMotion) {
        setAnimation({ pokemonId: pokemon.id, progress: 1 });
        return;
      }

      startTime ??= timestamp;
      const linearProgress = Math.min((timestamp - startTime) / STAT_ANIMATION_DURATION, 1);
      const easedProgress = 1 - (1 - linearProgress) ** 4;

      setAnimation({ pokemonId: pokemon.id, progress: easedProgress });

      if (linearProgress < 1) {
        frameId = window.requestAnimationFrame(animate);
      }
    }

    frameId = window.requestAnimationFrame(animate);
    return () => window.cancelAnimationFrame(frameId);
  }, [pokemon.id]);

  const progress = animation.pokemonId === pokemon.id ? animation.progress : 0;

  return (
    <section className="pokedex-detail__stats" aria-labelledby="pokedex-detail-stats-title">
      <h2 id="pokedex-detail-stats-title">Base Stats</h2>
      <ul className="pokedex-detail__stat-list">
        {BASE_STAT_ROWS.map(({ label, key }) => {
          const value = pokemon[key];
          const tone = getBaseStatTone(value);
          const hasHighStatGlow = tone === "high" || tone === "higher" || tone === "highest";
          const shouldShowGlow = hasHighStatGlow && progress >= 1;
          const animatedValue = Math.round(value * progress);
          const percentage = Math.max(0, Math.min(100, (value / 255) * 100 * progress));
          const style = {
            "--stat-color": `var(--color-stat-${tone})`,
            "--stat-width": `${percentage}%`,
          } as CSSProperties;

          return (
            <li className="pokedex-detail__stat-row" key={key}>
              <span className="pokedex-detail__stat-label">{label}</span>
              <div className="pokedex-detail__stat-track" style={style}>
                <div
                  className={shouldShowGlow
                    ? "pokedex-detail__stat-bar pokedex-detail__stat-bar--glow"
                    : "pokedex-detail__stat-bar"}
                  role="progressbar"
                  aria-label={label}
                  aria-valuemin={0}
                  aria-valuemax={255}
                  aria-valuenow={animatedValue}/>
                <span className="pokedex-detail__stat-value">{animatedValue}</span>
              </div>
            </li>
          );
        })}
        <li className="pokedex-detail__stat-row" aria-hidden="true">
          <span />
          <div className="pokedex-detail__stat-axis">
            <span>0</span>
            <span>63</span>
            <span>127</span>
            <span>191</span>
            <span>255</span>
          </div>
        </li>
      </ul>
    </section>
  );
}

function DetailMessage({
  title,
  description,
}: {
  title: string;
  description: string;
}) {
  return (
    <section className="pokedex-detail__message" aria-live="polite">
      <h1>{title}</h1>
      <p>{description}</p>
      <div className="pokedex-detail__actions">
        <Link className="pokedex-detail__action pokedex-detail__action--secondary" to="/pokedex">
          Back to Pokédex
        </Link>
      </div>
    </section>
  );
}

export default function PokedexDetail() {
  const { id } = useParams();
  const navigate = useNavigate();
  const entryId = id === undefined ? Number.NaN : Number(id);
  const [loadState, setLoadState] = useState<DetailLoadState>({ entryId, status: "loading" });
  const [retryKey, setRetryKey] = useState(0);
  const [adjacentPokemonState, setAdjacentPokemonState] = useState<AdjacentPokemonState | null>(null);

  useEffect(() => {
    if (!Number.isSafeInteger(entryId) || entryId < 1) {
      return;
    }

    const controller = new AbortController();

    getPokedexEntryById(entryId, controller.signal)
      .then(pokemon => {
        if (!controller.signal.aborted) setLoadState({ entryId, status: "success", pokemon });
      })
      .catch((error: unknown) => {
        if (controller.signal.aborted) return;
        setLoadState(error instanceof ApiError && error.status === 404
          ? { entryId, status: "not-found" }
          : { entryId, status: "error" });
      });

    return () => controller.abort();
  }, [entryId, retryKey]);

  const isValidEntryId = Number.isSafeInteger(entryId) && entryId > 0;
  const detailLoadState = !isValidEntryId
    ? { status: "not-found" as const }
    : loadState.entryId === entryId
      ? loadState
      : { status: "loading" as const };
  const currentPokemonId = detailLoadState.status === "success" ? detailLoadState.pokemon.id : undefined;

  useEffect(() => {
    if (currentPokemonId === undefined) return;

    const controller = new AbortController();
    const getAdjacentPokemon = (pokemonId: number): Promise<AdjacentPokemon | null> =>
      getPokedexEntryById(pokemonId, controller.signal)
        .then(pokemon => ({ id: pokemon.id, name: pokemon.name }))
        .catch(() => null);

    Promise.all([
      currentPokemonId > 1 ? getAdjacentPokemon(currentPokemonId - 1) : Promise.resolve(null),
      getAdjacentPokemon(currentPokemonId + 1),
    ]).then(([previous, next]) => {
      if (!controller.signal.aborted) {
        setAdjacentPokemonState({ entryId: currentPokemonId, previous, next });
      }
    });

    return () => controller.abort();
  }, [currentPokemonId]);

  function retry() {
    setLoadState({ entryId, status: "loading" });
    setRetryKey(previous => previous + 1);
  }

  const pokemonTypes = detailLoadState.status === "success"
    ? [detailLoadState.pokemon.type1, detailLoadState.pokemon.type2]
      .filter((type): type is string => Boolean(type?.trim()))
    : [];
  const imageFrameStyle = detailLoadState.status === "success"
    ? {
      "--pokemon-type-start": getTypeColor(detailLoadState.pokemon.type1),
      "--pokemon-type-end": detailLoadState.pokemon.type2?.trim()
        ? getTypeColor(detailLoadState.pokemon.type2)
        : getTypeColor(detailLoadState.pokemon.type1),
    } as CSSProperties
    : undefined;
  const adjacentPokemon = adjacentPokemonState?.entryId === currentPokemonId ? adjacentPokemonState : null;

  const content = detailLoadState.status === "loading" ? (
    <p className="pokedex-detail__message" role="status">Loading Pokémon…</p>
  ) : detailLoadState.status === "not-found" ? (
    <DetailMessage title="Pokémon not found" description="This Pokédex entry is unavailable." />
  ) : detailLoadState.status === "error" ? (
    <section className="pokedex-detail__message" aria-live="polite">
      <h1>Could not load this Pokémon</h1>
      <p>Please check your connection and try again.</p>
      <div className="pokedex-detail__actions">
        <button className="pokedex-detail__action" type="button" onClick={retry}>Try again</button>
        <Link className="pokedex-detail__action pokedex-detail__action--secondary" to="/pokedex">
          Back to Pokédex
        </Link>
      </div>
    </section>
  ) : (
    <div className="pokedex-detail__content">
      <div className="pokedex-detail__back">
        <button
          className="pokedex-detail__back-button"
          type="button"
          aria-label="Back to PokeDex"
          onClick={() => navigate(-1)}>
          <svg viewBox="0 0 24 24" aria-hidden="true">
            <path d="M19 12H5m0 0 7-7m-7 7 7 7" />
          </svg>
        </button>
        <span>Back to PokeDex</span>
      </div>

      <article className="pokedex-detail__layout" aria-labelledby="pokedex-detail-title">
        <section className="pokedex-detail__visual" aria-label={`${detailLoadState.pokemon.name} artwork`}>
          <div className="pokedex-detail__image-frame" style={imageFrameStyle}>
            <img src={detailLoadState.pokemon.imageUrl} alt={detailLoadState.pokemon.name} />
          </div>
          <ul
            className={pokemonTypes.length === 1
              ? "pokedex-detail__types pokedex-detail__types--single"
              : "pokedex-detail__types"}
            aria-label="Pokémon types">
            {pokemonTypes.map(type => <TypeBadge key={type} type={type} />)}
          </ul>
        </section>

        <section className="pokedex-detail__information" aria-labelledby="pokedex-detail-title">
          <div className="pokedex-detail__heading">
            <h1 id="pokedex-detail-title">
              {detailLoadState.pokemon.name}
              <span className="pokedex-detail__number"> #{String(detailLoadState.pokemon.id).padStart(3, "0")}</span>
            </h1>
          </div>
          <p className="pokedex-detail__description">{detailLoadState.pokemon.description}</p>

          <dl className="pokedex-detail__facts">
            <div className="pokedex-detail__fact">
              <dt>National Dex Number</dt>
              <dd>#{String(detailLoadState.pokemon.nationalPokedex).padStart(3, "0")}</dd>
            </div>
            <div className="pokedex-detail__fact">
              <dt>Height</dt>
              <dd>{formatMeasurement(detailLoadState.pokemon.height, "m")}</dd>
            </div>
            <div className="pokedex-detail__fact">
              <dt>Generation</dt>
              <dd>{GENERATION_NUMERALS[detailLoadState.pokemon.generation - 1] ?? detailLoadState.pokemon.generation} Generation</dd>
            </div>
            <div className="pokedex-detail__fact">
              <dt>Weight</dt>
              <dd>{formatMeasurement(detailLoadState.pokemon.weight, "kg")}</dd>
            </div>
            <div className="pokedex-detail__fact">
              <dt>Category</dt>
              <dd>{detailLoadState.pokemon.category}</dd>
            </div>
            <div className="pokedex-detail__fact">
              <dt>Abilities</dt>
              <dd>
                <ul className="pokedex-detail__abilities">
                  {[
                    { name: detailLoadState.pokemon.ability1, hidden: false },
                    { name: detailLoadState.pokemon.ability2, hidden: false },
                    { name: detailLoadState.pokemon.hiddenAbility, hidden: true },
                  ]
                    .filter((ability): ability is { name: string; hidden: boolean } => Boolean(ability.name?.trim()))
                    .map(({ name, hidden }) => (
                      <li
                        className={hidden ? "pokedex-detail__ability--hidden" : undefined}
                        key={name}>
                        {name}
                      </li>
                    ))}
                </ul>
              </dd>
            </div>
          </dl>

          <BaseStatsChart pokemon={detailLoadState.pokemon} />
        </section>
      </article>
      <nav className="pokedex-detail__pokemon-navigation" aria-label="Adjacent Pokémon">
        {adjacentPokemon?.previous ? (
          <Link
            className="pokedex-detail__pokemon-link pokedex-detail__pokemon-link--previous"
            to={`/pokedex/${adjacentPokemon.previous.id}`}
            replace
            aria-label={`Previous Pokémon: ${adjacentPokemon.previous.name} #${String(adjacentPokemon.previous.id).padStart(3, "0")}`}>
            <svg viewBox="0 0 24 24" aria-hidden="true">
              <path d="M19 12H5m0 0 7-7m-7 7 7 7" />
            </svg>
            <span className="pokedex-detail__pokemon-link-label">
              <span className="pokedex-detail__pokemon-link-name">{adjacentPokemon.previous.name}</span>
              <span>#{String(adjacentPokemon.previous.id).padStart(3, "0")}</span>
            </span>
          </Link>
        ) : (
          <button
            className="pokedex-detail__pokemon-link pokedex-detail__pokemon-link--previous"
            type="button"
            disabled
            aria-label={adjacentPokemon ? "No previous Pokémon" : "Loading previous Pokémon"}>
            <svg viewBox="0 0 24 24" aria-hidden="true">
              <path d="M19 12H5m0 0 7-7m-7 7 7 7" />
            </svg>
            <span className="pokedex-detail__pokemon-link-label">
              {adjacentPokemon ? "First Pokémon" : "Loading…"}
            </span>
          </button>
        )}

        {adjacentPokemon?.next ? (
          <Link
            className="pokedex-detail__pokemon-link pokedex-detail__pokemon-link--next"
            to={`/pokedex/${adjacentPokemon.next.id}`}
            replace
            aria-label={`Next Pokémon: ${adjacentPokemon.next.name} #${String(adjacentPokemon.next.id).padStart(3, "0")}`}>
            <span className="pokedex-detail__pokemon-link-label">
              <span className="pokedex-detail__pokemon-link-name">{adjacentPokemon.next.name}</span>
              <span>#{String(adjacentPokemon.next.id).padStart(3, "0")}</span>
            </span>
            <svg viewBox="0 0 24 24" aria-hidden="true">
              <path d="M5 12h14m0 0-7-7m7 7-7 7" />
            </svg>
          </Link>
        ) : (
          <button
            className="pokedex-detail__pokemon-link pokedex-detail__pokemon-link--next"
            type="button"
            disabled
            aria-label={adjacentPokemon ? "No next Pokémon" : "Loading next Pokémon"}>
            <span className="pokedex-detail__pokemon-link-label">
              {adjacentPokemon ? "Last Pokémon" : "Loading…"}
            </span>
            <svg viewBox="0 0 24 24" aria-hidden="true">
              <path d="M5 12h14m0 0-7-7m7 7-7 7" />
            </svg>
          </button>
        )}
      </nav>
    </div>
  );

  return (
    <div className="pokedex-detail-page">
      <Header />
      <main className="pokedex-detail__main" aria-busy={detailLoadState.status === "loading"}>
        {content}
      </main>
      <Footer />
    </div>
  );
}
