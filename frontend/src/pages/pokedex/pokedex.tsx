// PokéDex page — filterable, sortable grid of Pokémon cards

import { useEffect, useMemo, useState } from "react";
import Button from "../../components/common/button";
import StatSlicer from "../../components/common/StatSlicer";
import { getPokedex } from "../../services/pokemonService";
import type { PokedexEntry, PokemonCard } from "../../types/pokemon";
import "./pokedex.css";

// ===== POKEMON TYPE COLORS (official, used as card backgrounds at 20% opacity) =====
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

// Solid color (single type) or diagonal gradient (dual type), both at 20% opacity
function getPokemonCardBackground(types: string[]): string {
  if (types.length === 1) {
    const hex = TYPE_COLORS[types[0]] ?? "#A8A878";
    return `${hex}33`; // 33 = 20% in hex alpha
  }
  const c1 = TYPE_COLORS[types[0]] ?? "#A8A878";
  const c2 = TYPE_COLORS[types[1]] ?? "#A8A878";
  return `linear-gradient(135deg, ${c1}33 0%, ${c2}33 100%)`;
}

// ===== FILTER OPTIONS (TODO: replace with API calls once endpoints are available) =====
const TYPES = [
  "Normal", "Fire", "Water", "Electric", "Grass", "Ice", "Fighting",
  "Poison", "Ground", "Flying", "Psychic", "Bug", "Rock", "Ghost",
  "Dragon", "Dark", "Steel", "Fairy",
];

const GENERATIONS = ["I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX"];

// Regions match generation for simplicity
const REGIONS = [
  "Kanto", "Johto", "Hoenn", "Sinnoh", "Unova", "Kalos", "Alola", "Galar", "Paldea",
];

// Tooltip describing how the stat slicers work
const SLICER_TOOLTIP =
  "Click a handle to cycle its filter mode:\n" +
  "● Exact — keeps Pokémon whose stat equals the value.\n" +
  "▶ Minimum — keeps Pokémon whose stat is the value or higher.\n" +
  "◀ Maximum — keeps Pokémon whose stat is the value or lower.\n" +
  "Drag the handle to change the value (0–255).";

type SlicerMode = 0 | 1 | 2;

interface SlicerState {
  value: number; // current position, default 127 (midpoint)
  mode: SlicerMode; // 0=exact, 1=minimum, 2=maximum
}

// Each stat maps to its min/max keys in PokedexFilters
const STATS = [
  { key: "Hp", label: "HP" },
  { key: "Attack", label: "ATTACK" },
  { key: "Defense", label: "DEFENSE" },
  { key: "SpAtk", label: "SP. ATK." },
  { key: "SpDef", label: "SP. DEF." },
  { key: "Speed", label: "SPEED" },
] as const;

type StatKey = (typeof STATS)[number]["key"];

const DEFAULT_SLICER: SlicerState = { value: 127, mode: 0 };

function makeDefaultSlicers(): Record<StatKey, SlicerState> {
  return {
    Hp: { ...DEFAULT_SLICER },
    Attack: { ...DEFAULT_SLICER },
    Defense: { ...DEFAULT_SLICER },
    SpAtk: { ...DEFAULT_SLICER },
    SpDef: { ...DEFAULT_SLICER },
    Speed: { ...DEFAULT_SLICER },
  };
}

// Translates a slicer's value+mode into [min, max] bounds for the backend
function slicerBounds(state: SlicerState): { min: number; max: number } {
  if (state.mode === 1) return { min: state.value, max: 255 }; // minimum
  if (state.mode === 2) return { min: 0, max: state.value }; // maximum
  return { min: state.value, max: state.value }; // exact
}

function PokedexLogo() {
  return (
    <img
      src="/header-logo.png"
      alt="TrainerForge logo"
      className="pokedex__title-logo"
    />
  );
}

function SortIcon() {
  return (
    <svg width="16" height="16" viewBox="0 0 24 24" fill="none" aria-hidden="true">
      <path d="M7 4v16M7 20l-3-3M7 20l3-3M17 20V4M17 4l-3 3M17 4l3 3"
        stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" />
    </svg>
  );
}

function SearchIcon() {
  return (
    <svg width="16" height="16" viewBox="0 0 24 24" fill="none" aria-hidden="true">
      <circle cx="11" cy="11" r="7" stroke="currentColor" strokeWidth="2" />
      <path d="M20 20l-3.5-3.5" stroke="currentColor" strokeWidth="2" strokeLinecap="round" />
    </svg>
  );
}

function CleanIcon() {
  return (
    <svg width="16" height="16" viewBox="0 0 24 24" fill="none" aria-hidden="true">
      <path d="M6 6l12 12M18 6L6 18" stroke="currentColor" strokeWidth="2" strokeLinecap="round" />
    </svg>
  );
}

// Maps which stat key on each entry a slicer constrains
const STAT_FIELD: Record<StatKey, keyof PokedexEntry> = {
  Hp: "hpBase",
  Attack: "attackBase",
  Defense: "defenseBase",
  SpAtk: "specialAttackBase",
  SpDef: "specialDefenseBase",
  Speed: "speedBase",
};

// Snapshot of the filters actually applied to the list (set on SEARCH)
interface AppliedFilters {
  name: string;
  ability: string;
  type1: string;
  type2: string;
  generation: string;
  region: string;
  slicers: Record<StatKey, SlicerState>;
}

const EMPTY_APPLIED: AppliedFilters = {
  name: "",
  ability: "",
  type1: "",
  type2: "",
  generation: "",
  region: "",
  slicers: makeDefaultSlicers(),
};

// Collects an entry's non-null types into an array
function entryTypes(entry: PokedexEntry): string[] {
  return [entry.type1, entry.type2].filter((t): t is string => Boolean(t));
}

function entryToCard(entry: PokedexEntry, index: number): PokemonCard {
  return {
    id: index + 1, // local grid index (#001, #002, …)
    name: entry.name,
    pokedexNumber: entry.nationalPokedex,
    imageUrl: entry.imageUrl,
    types: entryTypes(entry),
  };
}

function matchesFilters(entry: PokedexEntry, f: AppliedFilters): boolean {
  if (f.name && !entry.name.toLowerCase().includes(f.name.toLowerCase().trim())) {
    return false;
  }

  if (f.ability) {
    const needle = f.ability.toLowerCase().trim();
    const abilities = [entry.ability1, entry.ability2, entry.hiddenAbility]
      .filter(Boolean)
      .map((a) => (a as string).toLowerCase());
    if (!abilities.some((a) => a.includes(needle))) return false;
  }

  const types = entryTypes(entry);
  if (f.type1 && !types.includes(f.type1)) return false;
  if (f.type2 && !types.includes(f.type2)) return false;

  if (f.generation) {
    const genNumber = GENERATIONS.indexOf(f.generation) + 1;
    if (entry.generation !== genNumber) return false;
  }

  if (f.region && entry.region !== f.region) return false;

  // Stat slicers
  for (const { key } of STATS) {
    const { min, max } = slicerBounds(f.slicers[key]);
    const statValue = entry[STAT_FIELD[key]] as number;
    if (statValue < min || statValue > max) return false;
  }

  return true;
}

function Pokedex() {
  // Text / select filters (draft — applied on SEARCH)
  const [name, setName] = useState("");
  const [ability, setAbility] = useState("");
  const [type1, setType1] = useState("");
  const [type2, setType2] = useState("");
  const [generation, setGeneration] = useState("");
  const [region, setRegion] = useState("");

  // Stat slicers
  const [slicers, setSlicers] = useState<Record<StatKey, SlicerState>>(
    makeDefaultSlicers
  );

  // Data + applied filters + sort
  const [entries, setEntries] = useState<PokedexEntry[]>([]);
  const [applied, setApplied] = useState<AppliedFilters>(EMPTY_APPLIED);
  const [sortDir, setSortDir] = useState<"ASC" | "DESC">("ASC");
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  // Load the full Pokédex once on mount
  useEffect(() => {
    let cancelled = false;
    (async () => {
      try {
        const data = await getPokedex();
        if (!cancelled) setEntries(data);
      } catch {
        if (!cancelled) {
          setError(
            "Could not load the Pokédex. The API requires an active session — please log in and try again."
          );
        }
      } finally {
        if (!cancelled) setLoading(false);
      }
    })();
    return () => {
      cancelled = true;
    };
  }, []);

  // Filter + sort whenever the data, applied filters or sort direction change
  const pokemon = useMemo<PokemonCard[]>(() => {
    const filtered = entries
      .filter((entry) => matchesFilters(entry, applied))
      .sort((a, b) =>
        sortDir === "ASC"
          ? a.nationalPokedex - b.nationalPokedex
          : b.nationalPokedex - a.nationalPokedex
      );
    return filtered.map(entryToCard);
  }, [entries, applied, sortDir]);

  const updateSlicerValue = (key: StatKey, value: number) => {
    setSlicers((prev) => ({ ...prev, [key]: { ...prev[key], value } }));
  };

  const updateSlicerMode = (key: StatKey, mode: SlicerMode) => {
    setSlicers((prev) => ({ ...prev, [key]: { ...prev[key], mode } }));
  };

  const handleSearch = () => {
    setApplied({
      name,
      ability,
      type1,
      type2,
      generation,
      region,
      slicers: {
        Hp: { ...slicers.Hp },
        Attack: { ...slicers.Attack },
        Defense: { ...slicers.Defense },
        SpAtk: { ...slicers.SpAtk },
        SpDef: { ...slicers.SpDef },
        Speed: { ...slicers.Speed },
      },
    });
  };

  const handleClean = () => {
    setName("");
    setAbility("");
    setType1("");
    setType2("");
    setGeneration("");
    setRegion("");
    setSlicers(makeDefaultSlicers());
    setApplied(EMPTY_APPLIED);
  };

  const handleSort = () => {
    setSortDir((dir) => (dir === "ASC" ? "DESC" : "ASC"));
  };

  return (
    <main className="pokedex">
      {/* Page header */}
      <header className="pokedex__header">
        <PokedexLogo />
        <h1 className="pokedex__title">
          <span className="pokedex__title--black">Poké</span>
          <span className="pokedex__title--purple">Dex</span>
        </h1>
      </header>

      <div className="pokedex__layout">
        {/* LEFT — filters */}
        <aside className="pokedex__filters" aria-label="Pokédex filters">
          <div className="pokedex__field">
            <label className="pokedex__label" htmlFor="filter-name">Name</label>
            <input
              id="filter-name"
              type="text"
              className="pokedex__input"
              value={name}
              onChange={(e) => setName(e.target.value)}
            />
          </div>

          <div className="pokedex__field-row">
            <div className="pokedex__field">
              <label className="pokedex__label" htmlFor="filter-type1">Type</label>
              <select
                id="filter-type1"
                className="pokedex__select"
                value={type1}
                onChange={(e) => setType1(e.target.value)}
              >
                <option value="">All</option>
                {TYPES.map((t) => (
                  <option key={t} value={t}>{t}</option>
                ))}
              </select>
            </div>
            <div className="pokedex__field">
              <label className="pokedex__label" htmlFor="filter-type2">Type</label>
              <select
                id="filter-type2"
                className="pokedex__select"
                value={type2}
                onChange={(e) => setType2(e.target.value)}
              >
                <option value="">All</option>
                {TYPES.map((t) => (
                  <option key={t} value={t}>{t}</option>
                ))}
              </select>
            </div>
          </div>

          <div className="pokedex__field-row">
            <div className="pokedex__field">
              <label className="pokedex__label" htmlFor="filter-gen">Gen.</label>
              <select
                id="filter-gen"
                className="pokedex__select"
                value={generation}
                onChange={(e) => setGeneration(e.target.value)}
              >
                <option value="">All</option>
                {GENERATIONS.map((g) => (
                  <option key={g} value={g}>{g}</option>
                ))}
              </select>
            </div>
            <div className="pokedex__field">
              <label className="pokedex__label" htmlFor="filter-region">Region</label>
              <select
                id="filter-region"
                className="pokedex__select"
                value={region}
                onChange={(e) => setRegion(e.target.value)}
              >
                <option value="">All</option>
                {REGIONS.map((r) => (
                  <option key={r} value={r}>{r}</option>
                ))}
              </select>
            </div>
          </div>

          <div className="pokedex__field">
            <label className="pokedex__label" htmlFor="filter-ability">Ability</label>
            <input
              id="filter-ability"
              type="text"
              className="pokedex__input"
              value={ability}
              onChange={(e) => setAbility(e.target.value)}
            />
          </div>

          {/* Stat slicers */}
          <div className="pokedex__slicers">
            <div className="pokedex__slicers-header">
              <span className="pokedex__slicers-title">Stats</span>
              <span className="pokedex__tooltip" tabIndex={0} aria-label="How stat filters work">
                ?
                <span className="pokedex__tooltip-text" role="tooltip">
                  {SLICER_TOOLTIP}
                </span>
              </span>
            </div>

            {STATS.map(({ key, label }) => (
              <StatSlicer
                key={key}
                label={label}
                value={slicers[key].value}
                mode={slicers[key].mode}
                onChange={(v) => updateSlicerValue(key, v)}
                onModeChange={(m) => updateSlicerMode(key, m)}
              />
            ))}
          </div>

          <div className="pokedex__filter-actions">
            <Button label="CLEAN" variant="secondary" trailingIcon={<CleanIcon />} uppercase onClick={handleClean} />
            <Button label="SEARCH" variant="secondary" trailingIcon={<SearchIcon />} uppercase onClick={handleSearch} />
          </div>
        </aside>

        {/* RIGHT — results */}
        <section className="pokedex__results" aria-label="Pokémon results">
          <div className="pokedex__results-header">
            <Button label="SORT" variant="secondary" trailingIcon={<SortIcon />} uppercase onClick={handleSort} />
          </div>

          {loading && <p className="pokedex__status">Loading Pokémon…</p>}
          {error && <p className="pokedex__status pokedex__status--error">{error}</p>}
          {!loading && !error && pokemon.length === 0 && (
            <p className="pokedex__status">No Pokémon to show. Adjust the filters and search.</p>
          )}

          <div className="pokedex__grid">
            {pokemon.map((p) => (
              <article
                key={p.id}
                className="pokemon-card"
                style={{ background: getPokemonCardBackground(p.types) }}
              >
                <span className="pokemon-card__index">
                  #{String(p.id).padStart(3, "0")}
                </span>
                <div className="pokemon-card__image-wrap">
                  <img
                    src={p.imageUrl}
                    alt={p.name}
                    className="pokemon-card__image"
                    loading="lazy"
                  />
                </div>
                <div className="pokemon-card__footer">
                  <span className="pokemon-card__dex">
                    #{String(p.pokedexNumber).padStart(4, "0")}
                  </span>
                  <span className="pokemon-card__name">{p.name.toUpperCase()}</span>
                </div>
              </article>
            ))}
          </div>
        </section>
      </div>
    </main>
  );
}

export default Pokedex;
