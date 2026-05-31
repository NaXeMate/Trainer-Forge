// Pokémon detail page — shown when a card from the PokéDex is clicked (/pokedex/:id)

import { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import Button from "../../components/common/button";
import { getPokemonById } from "../../services/pokemonService";
import type { PokemonDetail as PokemonDetailData } from "../../types/pokemon";
import "./pokemonDetail.css";

// ===== POKEMON TYPE COLORS (official) — duplicated from pokedex.tsx =====
const TYPE_COLORS: Record<string, string> = {
  Normal: "#A8A878",
  Fire: "#F08030",
  Water: "#6890F0",
  Electric: "#F8D030",
  Grass: "#78C850",
  Ice: "#98D8D8",
  Fighting: "#C03028",
  Poison: "#A040A0",
  Ground: "#E0C068",
  Flying: "#A890F0",
  Psychic: "#F85888",
  Bug: "#A8B820",
  Rock: "#B8A038",
  Ghost: "#705898",
  Dragon: "#7038F8",
  Dark: "#705848",
  Steel: "#B8B8D0",
  Fairy: "#EE99AC",
};

// Height: decimetres → metres, one decimal, comma as separator
function formatHeight(dm: number): string {
  return (dm / 10).toFixed(1).replace(".", ",") + " m";
}

// Weight: hectograms → kg, one decimal, comma as separator
function formatWeight(hg: number): string {
  return (hg / 10).toFixed(1).replace(".", ",") + " kg";
}

// Pokédex number with "#" prefix, zero-padded to 3 digits
function formatDexNumber(n: number): string {
  return "#" + String(n).padStart(3, "0");
}

// Maps a stat value (0–255) onto a color gradient (red → teal)
function getStatBarColor(value: number): string {
  if (value <= 50) return "#E04040"; // low (0–50): red-orange
  if (value <= 80) return "#F8D030"; // medium-low (51–80): yellow
  if (value <= 110) return "#C8D830"; // medium (81–110): yellow-green
  if (value <= 150) return "#78C850"; // high (111–150): green
  return "#38C8A8"; // very high (151+): teal/blue-green
}

const MAX_STAT = 255;
const STAT_TICKS = [0, 63, 127, 191, 255];

// Heart icon — filled when favorite, outline otherwise
function HeartIcon({ filled }: { filled: boolean }) {
  return (
    <svg
      width="28"
      height="28"
      viewBox="0 0 24 24"
      fill={filled ? "currentColor" : "none"}
      aria-hidden="true"
    >
      <path
        d="M12 21s-7.5-4.6-10-9.3C.4 8.5 1.8 5 5 5c2 0 3.2 1.1 4 2.3C9.8 6.1 11 5 13 5c3.2 0 4.6 3.5 3 6.7C19.5 16.4 12 21 12 21z"
        stroke="currentColor"
        strokeWidth="2"
        strokeLinejoin="round"
      />
    </svg>
  );
}

interface StatRow {
  label: string;
  value: number;
}

function PokemonDetail() {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();

  const [pokemon, setPokemon] = useState<PokemonDetailData | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  // Visual-only favorite toggle for now.
  // TODO: wire up to the trainer's saved Pokémon endpoint in a future version
  const [isFavorite, setIsFavorite] = useState(false);

  useEffect(() => {
    if (!id) return;
    setLoading(true);
    getPokemonById(Number(id))
      .then((data) => {
        setPokemon(data);
        setLoading(false);
      })
      .catch(() => {
        setError("Could not load Pokémon data.");
        setLoading(false);
      });
  }, [id]);

  if (loading) {
    return (
      <main className="pokemon-detail-page">
        <p className="pokemon-detail-status">Loading...</p>
      </main>
    );
  }

  if (error || !pokemon) {
    return (
      <main className="pokemon-detail-page">
        <p className="pokemon-detail-status pokemon-detail-status--error">
          {error ?? "Could not load Pokémon data."}
        </p>
        <div className="pokemon-detail-error-actions">
          <Button label="BACK" variant="secondary" uppercase onClick={() => navigate(-1)} />
        </div>
      </main>
    );
  }

  const stats: StatRow[] = [
    { label: "HP", value: pokemon.hp },
    { label: "ATTACK", value: pokemon.attack },
    { label: "DEFENSE", value: pokemon.defense },
    { label: "SP. ATTACK", value: pokemon.spAttack },
    { label: "SP. DEFENSE", value: pokemon.spDefense },
    { label: "SPEED", value: pokemon.speed },
  ];

  return (
    <main className="pokemon-detail-page">
      <div className="pokemon-detail-layout">
        {/* ===== LEFT: image + type badges ===== */}
        <section className="pokemon-detail-left">
          <div className="pokemon-detail-image-card">
            <img
              src={pokemon.imageUrl}
              alt={pokemon.name}
              className="pokemon-detail-image"
            />
          </div>

          <div className="pokemon-detail-types">
            {pokemon.types.map((type) => (
              <span
                key={type}
                className="type-badge"
                style={{ backgroundColor: TYPE_COLORS[type] ?? "#A8A878" }}
              >
                {/* TODO: replace the dot with actual type icons when available */}
                <span className="type-badge__dot" />
                <span className="type-badge__label">{type.toUpperCase()}</span>
              </span>
            ))}
          </div>
        </section>

        {/* ===== RIGHT: data ===== */}
        <section className="pokemon-detail-right">
          {/* 1. Heading row */}
          <header className="pokemon-detail-heading">
            <h1 className="pokemon-detail-name">
              {pokemon.name.toUpperCase()}
              <span className="pokemon-detail-id">&nbsp;#{pokemon.id}</span>
            </h1>
            <button
              type="button"
              className={
                "pokemon-detail-fav" +
                (isFavorite ? " pokemon-detail-fav--active" : "")
              }
              aria-label={isFavorite ? "Remove from favorites" : "Add to favorites"}
              aria-pressed={isFavorite}
              onClick={() => setIsFavorite(!isFavorite)}
            >
              <HeartIcon filled={isFavorite} />
            </button>
          </header>

          {/* 2. Description */}
          <p className="pokemon-detail-description">{pokemon.description}</p>

          {/* 3. Data table */}
          <div className="pokemon-detail-table">
            <div className="pokemon-detail-cell pokemon-detail-cell--header">
              National Dex Number
            </div>
            <div className="pokemon-detail-cell pokemon-detail-cell--value">
              {formatDexNumber(pokemon.pokedexNumber)}
            </div>
            <div className="pokemon-detail-cell pokemon-detail-cell--header">
              Height
            </div>
            <div className="pokemon-detail-cell pokemon-detail-cell--value">
              {formatHeight(pokemon.height)}
            </div>

            <div className="pokemon-detail-cell pokemon-detail-cell--header">
              Generation
            </div>
            <div className="pokemon-detail-cell pokemon-detail-cell--value">
              {pokemon.generation}
            </div>
            <div className="pokemon-detail-cell pokemon-detail-cell--header">
              Weight
            </div>
            <div className="pokemon-detail-cell pokemon-detail-cell--value">
              {formatWeight(pokemon.weight)}
            </div>

            <div className="pokemon-detail-cell pokemon-detail-cell--header">
              Category
            </div>
            <div className="pokemon-detail-cell pokemon-detail-cell--value">
              {pokemon.category}
            </div>
            <div className="pokemon-detail-cell pokemon-detail-cell--header">
              Abilities
            </div>
            <div className="pokemon-detail-cell pokemon-detail-cell--value">
              <ul className="pokemon-detail-abilities">
                {pokemon.abilities.map((ability) => (
                  <li
                    key={ability.name}
                    className={
                      "pokemon-detail-ability" +
                      (ability.isHidden ? " pokemon-detail-ability--hidden" : "")
                    }
                  >
                    {ability.name}
                  </li>
                ))}
              </ul>
            </div>
          </div>

          {/* 4. Base stats */}
          <div className="pokemon-detail-stats">
            <span className="pokemon-detail-stats-title">BASE STATS</span>

            <div className="pokemon-detail-stats-chart">
              {stats.map((stat) => (
                <div key={stat.label} className="pokemon-detail-stat-row">
                  <span className="pokemon-detail-stat-label">{stat.label}</span>
                  <div className="pokemon-detail-stat-track">
                    <div
                      className="pokemon-detail-stat-bar"
                      style={{
                        width: `${(stat.value / MAX_STAT) * 100}%`,
                        backgroundColor: getStatBarColor(stat.value),
                      }}
                    />
                  </div>
                  <span className="pokemon-detail-stat-value">{stat.value}</span>
                </div>
              ))}

              {/* X axis ticks — aligned under the bar track column */}
              <div className="pokemon-detail-stat-axis">
                <div className="pokemon-detail-stat-axis-ticks">
                  {STAT_TICKS.map((tick) => (
                    <span key={tick} className="pokemon-detail-stat-tick">
                      {tick}
                    </span>
                  ))}
                </div>
              </div>
            </div>
          </div>
        </section>
      </div>
    </main>
  );
}

export default PokemonDetail;
