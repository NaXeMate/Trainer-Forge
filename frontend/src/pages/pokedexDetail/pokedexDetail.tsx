import { useEffect, useState } from "react";
import type { CSSProperties } from "react";
import { Link, useParams } from "react-router";
import bugTypeIcon from "../../assets/icons/pokemonTypes/bug.svg";
import darkTypeIcon from "../../assets/icons/pokemonTypes/dark.svg";
import dragonTypeIcon from "../../assets/icons/pokemonTypes/dragon.svg";
import electricTypeIcon from "../../assets/icons/pokemonTypes/electric.svg";
import fairyTypeIcon from "../../assets/icons/pokemonTypes/fairy.svg";
import fightingTypeIcon from "../../assets/icons/pokemonTypes/fighting.svg";
import fireTypeIcon from "../../assets/icons/pokemonTypes/fire.svg";
import flyingTypeIcon from "../../assets/icons/pokemonTypes/flying.svg";
import ghostTypeIcon from "../../assets/icons/pokemonTypes/ghost.svg";
import grassTypeIcon from "../../assets/icons/pokemonTypes/grass.svg";
import groundTypeIcon from "../../assets/icons/pokemonTypes/ground.svg";
import iceTypeIcon from "../../assets/icons/pokemonTypes/ice.svg";
import normalTypeIcon from "../../assets/icons/pokemonTypes/normal.svg";
import poisonTypeIcon from "../../assets/icons/pokemonTypes/poison.svg";
import psychicTypeIcon from "../../assets/icons/pokemonTypes/psychic.svg";
import rockTypeIcon from "../../assets/icons/pokemonTypes/rock.svg";
import steelTypeIcon from "../../assets/icons/pokemonTypes/steel.svg";
import waterTypeIcon from "../../assets/icons/pokemonTypes/water.svg";
import Footer from "../../components/layout/footer";
import Header from "../../components/layout/header";
import { getTypeColor } from "../../utils/pokemonPresentation";
import { ApiError } from "../../services/api";
import { getPokedexEntryById } from "../../services/pokedexService";
import type { PokedexEntry } from "../../types/pokedexTypes";
import "./pokedexDetail.css";

const GENERATION_NUMERALS = ["I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX"];
const TYPE_ICONS: Record<string, string> = {
  bug: bugTypeIcon,
  dark: darkTypeIcon,
  dragon: dragonTypeIcon,
  electric: electricTypeIcon,
  fairy: fairyTypeIcon,
  fighting: fightingTypeIcon,
  fire: fireTypeIcon,
  flying: flyingTypeIcon,
  ghost: ghostTypeIcon,
  grass: grassTypeIcon,
  ground: groundTypeIcon,
  ice: iceTypeIcon,
  normal: normalTypeIcon,
  poison: poisonTypeIcon,
  psychic: psychicTypeIcon,
  rock: rockTypeIcon,
  steel: steelTypeIcon,
  water: waterTypeIcon,
};

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

function TypeBadge({ type }: { type: string }) {
  const style = { "--type-color": getTypeColor(type) } as CSSProperties;
  const icon = TYPE_ICONS[type.trim().toLowerCase()] ?? normalTypeIcon;

  return (
    <li className="pokedex-detail__type" style={style}>
      <span className="pokedex-detail__type-icon" aria-hidden="true">
        <img src={icon} alt="" />
      </span>
      <span>{type.toUpperCase()}</span>
    </li>
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
  const entryId = id === undefined ? Number.NaN : Number(id);
  const [loadState, setLoadState] = useState<DetailLoadState>({ entryId, status: "loading" });
  const [retryKey, setRetryKey] = useState(0);

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

  function retry() {
    setLoadState({ entryId, status: "loading" });
    setRetryKey(previous => previous + 1);
  }

  const pokemonTypes = detailLoadState.status === "success"
    ? [detailLoadState.pokemon.type1, detailLoadState.pokemon.type2]
      .filter((type): type is string => Boolean(type?.trim()))
    : [];
  const pokemonForStats = detailLoadState.status === "success" ? detailLoadState.pokemon : undefined;
  const imageFrameStyle = detailLoadState.status === "success"
    ? {
      "--pokemon-type-start": getTypeColor(detailLoadState.pokemon.type1),
      "--pokemon-type-end": detailLoadState.pokemon.type2?.trim()
        ? getTypeColor(detailLoadState.pokemon.type2)
        : getTypeColor(detailLoadState.pokemon.type1),
    } as CSSProperties
    : undefined;

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
          <span className="pokedex-detail__favorite" aria-hidden="true">
            <svg viewBox="0 0 24 24">
              <path d="M20.8 4.6a5.5 5.5 0 0 0-7.8 0L12 5.7l-1.1-1.1a5.5 5.5 0 0 0-7.8 7.8l1.1 1.1L12 21l7.8-7.5 1.1-1.1a5.5 5.5 0 0 0-.1-7.8Z" fill="currentColor"/>
            </svg>
          </span>
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

        <section className="pokedex-detail__stats" aria-labelledby="pokedex-detail-stats-title">
          <h2 id="pokedex-detail-stats-title">Base Stats</h2>
          <ul className="pokedex-detail__stat-list">
            {BASE_STAT_ROWS.map(({ label, key }) => {
              if (!pokemonForStats) return null;
              const value = pokemonForStats[key];
              const percentage = Math.max(0, Math.min(100, (value / 255) * 100));
              const style = {
                "--stat-color": `var(--color-stat-${getBaseStatTone(value)})`,
                "--stat-width": `${percentage}%`,
              } as CSSProperties;

              return (
                <li className="pokedex-detail__stat-row" key={key}>
                  <span className="pokedex-detail__stat-label">{label}</span>
                  <div className="pokedex-detail__stat-track" style={style}>
                    <div
                      className="pokedex-detail__stat-bar"
                      role="progressbar"
                      aria-label={label}
                      aria-valuemin={0}
                      aria-valuemax={255}
                      aria-valuenow={value}/>
                    <span className="pokedex-detail__stat-value">{value}</span>
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
      </section>
    </article>
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
