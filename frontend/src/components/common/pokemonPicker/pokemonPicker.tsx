import { useId, useMemo, useState } from "react";

import sortIcon from "../../../assets/icons/pokedex/sort.svg";
import type { PokedexEntry } from "../../../types/pokedexTypes";
import type { PokedexSortCriterion } from "../../../utils/pokedexSort";
import { EMPTY_POKEMON_FILTERS, getTeamPickerPokemon, TEAM_POKEMON_SORT_GROUPS } from "../../../utils/teamPicker";
import { getPokemonTypes } from "../../../utils/typeEffectiveness";
import Combobox from "../combobox";
import PokemonTypeBadge from "../pokemonTypeBadge";
import PokemonTypeCombobox from "../pokemonTypeCombobox";
import SortOptions from "../sortOptions";

import "../../../styles/pokemonTypeColors.css";
import "./pokemonPicker.css";

export type PickerPanel = "filter" | "sort";

type PokemonPickerProps = {
  active?: boolean;
  entries: PokedexEntry[];
  status: "loading" | "success" | "error";
  query: string;
  onQueryChange: (query: string) => void;
  panel: PickerPanel | null;
  onPanelChange: (panel: PickerPanel | null) => void;
  onRetry: () => void;
  onSelect: (pokemon: PokedexEntry) => void;
};

export default function PokemonPicker({
  active = true, entries, status, query, onQueryChange, panel, onPanelChange, onRetry, onSelect,
}: PokemonPickerProps) {
  const id = useId();
  const [sort, setSort] = useState<PokedexSortCriterion[]>([]);
  const [filters, setFilters] = useState(EMPTY_POKEMON_FILTERS);
  const abilityOptions = useMemo(() => [...new Set(entries.flatMap(pokemon =>
    [pokemon.ability1, pokemon.ability2, pokemon.hiddenAbility].filter((ability): ability is string => Boolean(ability)),
  ))].sort((a, b) => a.localeCompare(b)).map(ability => ({ value: ability, label: ability })), [entries]);

  if (!active) return null;

  const matches = getTeamPickerPokemon(entries, query, filters, sort);

  return (
    <>
      <div className="team-dialog__search">
        <label htmlFor={id + "-search"}>Search Pokémon by name or ID</label>
        <div className="team-dialog__search-row--tools">
          <input id={id + "-search"} value={query} onChange={event => onQueryChange(event.target.value)} placeholder="Search Pokémon…" />
          <button className="team-dialog__tool" type="button" aria-label="Filter Pokémon"
            aria-expanded={panel === "filter"} aria-controls={panel === "filter" ? id + "-filter" : undefined}
            onClick={() => onPanelChange(panel === "filter" ? null : "filter")}>
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" aria-hidden="true">
              <path d="M3 4h18l-7 8v5l-4 3v-8L3 4Z" />
            </svg>
          </button>
          <button className="team-dialog__tool" type="button" aria-label="Sort Pokémon"
            aria-expanded={panel === "sort"} aria-controls={panel === "sort" ? id + "-sort" : undefined}
            onClick={() => onPanelChange(panel === "sort" ? null : "sort")}>
            <img src={sortIcon} alt="" />
          </button>
        </div>
      </div>

      {panel === "sort" && (
        <div className="team-dialog__picker-panel team-dialog__sort" data-picker="pokemon" id={id + "-sort"} role="region" aria-label="Sort options">
          <SortOptions groups={TEAM_POKEMON_SORT_GROUPS} criteria={sort} onChange={setSort} />
        </div>
      )}
      {panel === "filter" && (
        <div className="team-dialog__picker-panel" id={id + "-filter"} role="region" aria-label="Filter options">
          <div className="team-dialog__filters">
            <div className="team-dialog__filter-select">
              <label htmlFor={id + "-type"}>Type</label>
              <PokemonTypeCombobox inputId={id + "-type"} name={id + "-type"} label="Type" value={filters.type}
                clearLabel="Clear type filter" liveSearch={false}
                onChange={type => setFilters(previous => ({ ...previous, type }))} />
            </div>
            <div className="team-dialog__filter-select">
              <label htmlFor={id + "-ability"}>Ability</label>
              <Combobox inputId={id + "-ability"} label="Ability" value={filters.ability} placeholder="Any ability"
                options={abilityOptions} clearLabel="Clear ability filter"
                onChange={ability => setFilters(previous => ({ ...previous, ability }))} />
            </div>
          </div>
          <button className="team-dialog__reset-filters" type="button" onClick={() => setFilters(EMPTY_POKEMON_FILTERS)}>Reset filters</button>
        </div>
      )}

      {status === "loading" && <p role="status">Loading Pokémon…</p>}
      {status === "error" && (
        <div role="alert">
          <p>Could not load the Pokédex. Check that the server is running.</p>
          <button type="button" className="team-dialog__primary" onClick={onRetry}>Retry</button>
        </div>
      )}
      {status === "success" && (
        <>
          <p className="team-dialog__count" role="status">{matches.length} Pokémon found</p>
          <div className="team-dialog__results team-dialog__pokemon">
            {matches.map(pokemon => {
              const types = getPokemonTypes(pokemon);

              return (
                <button type="button" key={pokemon.id} aria-label={[pokemon.name, ...types].join(", ")} onClick={() => onSelect(pokemon)}>
                  <img className="team-dialog__artwork" src={pokemon.imageUrl} alt="" loading="lazy" />
                  {types.length > 0 && (
                    <span className="team-dialog__types">
                      {types.map(type => <PokemonTypeBadge key={type} type={type} as="span" />)}
                    </span>
                  )}
                  <strong>{pokemon.name}</strong>
                </button>
              );
            })}
          </div>
          {!matches.length && <p>No Pokémon match your search.</p>}
        </>
      )}
    </>
  );
}
