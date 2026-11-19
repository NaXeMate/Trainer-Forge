import { useState, type Dispatch, type SetStateAction } from "react";

import physicalMoveIcon from "../../assets/icons/movesClasses/physical.svg";
import specialMoveIcon from "../../assets/icons/movesClasses/special.svg";
import statusMoveIcon from "../../assets/icons/movesClasses/status.svg";
import sortIcon from "../../assets/icons/pokedex/sort.svg";
import Combobox from "../../components/common/combobox";
import type { PickerPanel } from "../../components/common/pokemonPicker";
import PokemonTypeBadge from "../../components/common/pokemonTypeBadge";
import PokemonTypeCombobox from "../../components/common/pokemonTypeCombobox";
import SortOptions from "../../components/common/sortOptions";
import StatisticSlider from "../../components/common/statisticSlider";
import type { MoveOption } from "../../types/moveTypes";
import {
  EMPTY_MOVE_FILTERS,
  getTeamPickerMoves,
  MOVE_RANGES,
  TEAM_MOVE_SORT_GROUPS,
  type MoveSortCriterion,
} from "../../utils/teamPicker";

const MOVE_CLASS_ICONS = new Map([
  ["PHYSICAL", physicalMoveIcon],
  ["SPECIAL", specialMoveIcon],
  ["STATUS", statusMoveIcon],
]);

const MOVE_CLASS_OPTIONS = [
  { value: "PHYSICAL", label: "Physical" },
  { value: "SPECIAL", label: "Special" },
  { value: "STATUS", label: "Status" },
];

export type MoveLoadState = { status: "loading" | "error" } | { status: "success"; moves: MoveOption[] };

type MovePickerProps = {
  pokemonName?: string;
  selectedMoves?: readonly string[];
  moveIndex?: number;
  moveState: MoveLoadState;
  query: string;
  onQueryChange: (query: string) => void;
  panel: PickerPanel | null;
  setPanel: Dispatch<SetStateAction<PickerPanel | null>>;
  onRetry: () => void;
  onSelect: (move: MoveOption | null) => void;
};

export default function MovePicker({
  pokemonName,
  selectedMoves,
  moveIndex,
  moveState,
  query,
  onQueryChange,
  panel,
  setPanel,
  onRetry,
  onSelect,
}: MovePickerProps) {
  const [moveSort, setMoveSort] = useState<MoveSortCriterion[]>([]);
  const [moveFilters, setMoveFilters] = useState(EMPTY_MOVE_FILTERS);

  if (moveIndex === undefined) return null;

  const availableMoves = moveState.status === "success" ? moveState.moves : [];
  const matchingMoves = getTeamPickerMoves(availableMoves, query, moveFilters, moveSort);

  return (
    <>
      <div className="team-dialog__search">
        <label htmlFor="team-picker-search">Search moves by name</label>
        <div className="team-dialog__search-row--tools">
          <input
            id="team-picker-search"
            value={query}
            onChange={event => onQueryChange(event.target.value)}
            placeholder="Search moves…"
          />
          <button
            className="team-dialog__tool"
            type="button"
            aria-label="Filter moves"
            aria-expanded={panel === "filter"}
            aria-controls={panel === "filter" ? "team-picker-filter" : undefined}
            onClick={() => setPanel(previous => previous === "filter" ? null : "filter")}
          >
            <svg
              aria-hidden="true"
              viewBox="0 0 24 24"
              fill="none"
              stroke="currentColor"
              strokeWidth="2"
              strokeLinecap="round"
              strokeLinejoin="round"
            >
              <path d="M3 4h18l-7 8v5l-4 3v-8L3 4Z" />
            </svg>
          </button>
          <button
            className="team-dialog__tool"
            type="button"
            aria-label="Sort moves"
            aria-expanded={panel === "sort"}
            aria-controls={panel === "sort" ? "team-picker-sort" : undefined}
            onClick={() => setPanel(previous => previous === "sort" ? null : "sort")}
          >
            <img src={sortIcon} alt="" />
          </button>
        </div>
      </div>
      {panel === "sort" && (
        <div
          className="team-dialog__picker-panel team-dialog__sort"
          data-picker="move"
          id="team-picker-sort"
          role="region"
          aria-label="Sort options"
        >
          <SortOptions groups={TEAM_MOVE_SORT_GROUPS} criteria={moveSort} onChange={setMoveSort} />
        </div>
      )}
      {panel === "filter" && (
        <div
          className="team-dialog__picker-panel"
          id="team-picker-filter"
          role="region"
          aria-label="Filter options"
        >
          <div className="team-dialog__filters">
            <div className="team-dialog__filter-select">
              <label htmlFor="team-picker-type">Type</label>
              <PokemonTypeCombobox
                inputId="team-picker-type"
                name="team-picker-type"
                label="Type"
                value={moveFilters.type}
                clearLabel="Clear type filter"
                liveSearch={false}
                onChange={type => setMoveFilters(previous => ({ ...previous, type }))}
              />
            </div>
            <div className="team-dialog__filter-select">
              <label htmlFor="team-picker-class">Class</label>
              <Combobox
                inputId="team-picker-class"
                label="Class"
                placeholder="Any class"
                value={moveFilters.moveClass}
                options={MOVE_CLASS_OPTIONS}
                clearLabel="Clear class filter"
                onChange={moveClass => setMoveFilters(previous => ({ ...previous, moveClass }))}
              />
            </div>
            <div className="team-dialog__filter-sliders">
              {MOVE_RANGES.map(({ key, label, min, max }) => (
                <StatisticSlider
                  key={key}
                  label={label}
                  min={min}
                  max={max}
                  filter={moveFilters.ranges[key]}
                  onChange={filter => setMoveFilters(previous => ({
                    ...previous,
                    ranges: { ...previous.ranges, [key]: filter },
                  }))}
                />
              ))}
            </div>
          </div>
          <button
            className="team-dialog__reset-filters"
            type="button"
            onClick={() => setMoveFilters(EMPTY_MOVE_FILTERS)}
          >
            Reset filters
          </button>
        </div>
      )}

      <p className="team-dialog__picker-note">Choose a move for {pokemonName}.</p>
      {moveState.status === "loading" && <p role="status">Loading moves…</p>}
      {moveState.status === "error" && <div role="alert"><p>Could not load moves.</p><button type="button" className="team-dialog__primary" onClick={onRetry}>Retry</button></div>}
      {moveState.status === "success" && <>
        <p className="team-dialog__count" role="status">{matchingMoves.length} moves found</p>
        <div className="team-dialog__results team-dialog__moves">{matchingMoves.map(move => {
          const alreadySelected = selectedMoves?.some((name, index) => index !== moveIndex && name === move.name);
          const classIcon = MOVE_CLASS_ICONS.get(move.moveClass.toUpperCase());
          return (
            <button
              type="button"
              key={move.name}
              disabled={alreadySelected}
              onClick={() => onSelect(move)}
            >
              <strong className="team-dialog__move-name">{move.name}</strong>
              <PokemonTypeBadge type={move.type} as="span" />
              {classIcon && (
                <span
                  className="team-dialog__move-class"
                  role="img"
                  aria-label={move.moveClass.toLowerCase()}
                  style={{ maskImage: `url("${classIcon}")` }}
                />
              )}
              <small>Power: {move.power || "—"} · Accuracy: {move.accuracy || "—"} · PP: {move.pp}</small>
              {alreadySelected && <small>Already selected</small>}
            </button>
          );
        })}</div>
        {!availableMoves.length ? <p>No moves have been recorded in the catalogue.</p> : !matchingMoves.length && <p>No moves match your search.</p>}
      </>}
      <button className="team-dialog__primary" type="button" onClick={() => onSelect(null)}>Clear move</button>
    </>
  );
}
