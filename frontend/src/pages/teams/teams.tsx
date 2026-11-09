import { useEffect, useMemo, useRef, useState } from "react";
import type { CSSProperties, FormEvent } from "react";
import teamsIcon from "../../assets/brand/sections/teams_icon.png";
import physicalMoveIcon from "../../assets/icons/movesClasses/physical.svg";
import specialMoveIcon from "../../assets/icons/movesClasses/special.svg";
import statusMoveIcon from "../../assets/icons/movesClasses/status.svg";
import sortIcon from "../../assets/icons/pokedex/sort.svg";
import Header from "../../components/layout/header";
import Footer from "../../components/layout/footer";
import TeamCard from "../../components/cards/teamCard";
import type { ItemCatalogue } from "../../components/cards/teamCard";
import AccentCheckbox from "../../components/common/accentCheckbox";
import Combobox from "../../components/common/combobox";
import PokemonTypeBadge from "../../components/common/pokemonTypeBadge";
import PokemonTypeCombobox from "../../components/common/pokemonTypeCombobox";
import SortOptions from "../../components/common/sortOptions";
import StatisticSlider from "../../components/common/statisticSlider";
import { findPokemonType, POKEDEX_STATS } from "../../constants/pokedex";
import { getEquippableItems } from "../../services/itemService";
import { getPokedexEntries } from "../../services/pokedexService";
import { getMoves } from "../../services/moveService";
import type { MoveOption } from "../../types/moveTypes";
import type { PokedexEntry, PokedexStat } from "../../types/pokedexTypes";
import {
  convertLegacyEvs,
  EMPTY_STAT_POINTS,
  getStatPointLimit,
  getStatPointTotal,
  isValidStatPoints,
  STAT_POINT_BUDGET,
} from "../../utils/championsStatPoints";
import type { StatPoints } from "../../utils/championsStatPoints";
import type { PokedexSortCriterion } from "../../utils/pokedexSort";
import { getTypeColor } from "../../utils/pokemonPresentation";
import {
  EMPTY_MOVE_FILTERS,
  EMPTY_POKEMON_FILTERS,
  getTeamPickerMoves,
  getTeamPickerPokemon,
  MOVE_RANGES,
  TEAM_MOVE_SORT_GROUPS,
  TEAM_POKEMON_SORT_GROUPS,
} from "../../utils/teamPicker";
import type { MoveSortCriterion } from "../../utils/teamPicker";
import { uppercaseInput } from "../../utils/uppercaseInput";
import { analyzeTeamTypes, getPokemonTypes } from "../../utils/typeEffectiveness";
import type { PokemonType } from "../../utils/typeEffectiveness";
import "../../styles/pokemonTypeColors.css";
import "../../styles/teamControl.css";
import "./teams.css";

const TEAM_SIZE = 6;

const MODALITY_OPTIONS = [
  { value: "SINGLES", label: "Singles" },
  { value: "DOUBLES", label: "Doubles (VGC)" },
];

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

const STORAGE_KEY = "trainerforge:team-draft:v2";
const LEGACY_STORAGE_KEY = "trainerforge:team-draft:v1";
const ANALYZER_TYPES: readonly PokemonType[] = ["GRASS", "FIRE", "WATER", "NORMAL", "ELECTRIC", "GHOST", "FAIRY", "FLYING", "ICE", "ROCK", "GROUND", "PSYCHIC", "FIGHTING", "DARK", "BUG", "DRAGON", "POISON", "STEEL"];
const TYPE_ICONS = import.meta.glob<string>("../../assets/icons/pokemonTypes/*.svg", { eager: true, query: "?url", import: "default" });

type Member = { pokedexId: number; moves: [string, string, string, string]; ability: string; item: string; statPoints: StatPoints };
type Draft = { name: string; modality: string; game: string; publicTeam: boolean; members: (Member | null)[] };
type StoredMember = Omit<Member, "statPoints"> & { statPoints?: unknown; evs?: unknown };
type StoredDraft = Omit<Draft, "members"> & { members: (StoredMember | null)[] };
type DialogState = { kind: "pokemon"; slot: number } | { kind: "stat-points"; slot: number } | { kind: "move"; slot: number; index: number; pokedexId: number } | { kind: "erase" } | null;
type MoveLoadState = { status: "loading" | "error" } | { status: "success"; moves: MoveOption[] };
type PickerPanel = "sort" | "filter";

function emptyDraft(): Draft {
  return { name: "", modality: "", game: "", publicTeam: false, members: Array.from({ length: TEAM_SIZE }, () => null) };
}

function readDraft(): Draft {
  try {
    const saved = localStorage.getItem(STORAGE_KEY) ?? localStorage.getItem(LEGACY_STORAGE_KEY);
    const draft = JSON.parse(saved ?? "null") as StoredDraft | null;
    if (!draft || typeof draft.name !== "string" || typeof draft.modality !== "string" || typeof draft.game !== "string" || typeof draft.publicTeam !== "boolean" || !Array.isArray(draft.members) || draft.members.length !== TEAM_SIZE) return emptyDraft();
    const valid = draft.members.every(member => member === null || (
      typeof member === "object" && Number.isSafeInteger(member.pokedexId) && member.pokedexId > 0 &&
      Array.isArray(member.moves) && member.moves.length === 4 && member.moves.every(move => typeof move === "string") &&
      typeof member.ability === "string" && typeof member.item === "string"
    ));
    if (!valid) return emptyDraft();
    return {
      ...draft,
      members: draft.members.map(member => {
        if (!member) return null;
        const { pokedexId, moves, ability, item } = member;
        const statPoints = member.statPoints ?? convertLegacyEvs(member.evs);
        return {
          pokedexId, moves, ability, item,
          statPoints: isValidStatPoints(statPoints) ? statPoints : { ...EMPTY_STAT_POINTS },
        };
      }),
    };
  } catch { return emptyDraft(); }
}

/** Team builder with a local draft and type coverage based on the supplied chart. */
export default function Teams() {
  const [draft, setDraftState] = useState(readDraft);
  const [entries, setEntries] = useState<PokedexEntry[]>([]);
  const [loadStatus, setLoadStatus] = useState<"loading" | "success" | "error">("loading");
  const [attempt, setAttempt] = useState(0);
  const [dialog, setDialog] = useState<DialogState>(null);
  const [query, setQuery] = useState("");
  const [pickerPanel, setPickerPanel] = useState<PickerPanel | null>(null);
  const [pokemonSort, setPokemonSort] = useState<PokedexSortCriterion[]>([]);
  const [moveSort, setMoveSort] = useState<MoveSortCriterion[]>([]);
  const [pokemonFilters, setPokemonFilters] = useState(EMPTY_POKEMON_FILTERS);
  const [moveFilters, setMoveFilters] = useState(EMPTY_MOVE_FILTERS);
  const [statPoints, setStatPoints] = useState<StatPoints>(EMPTY_STAT_POINTS);
  const [moveState, setMoveState] = useState<MoveLoadState>({ status: "loading" });
  const [moveAttempt, setMoveAttempt] = useState(0);
  const [itemCatalogue, setItemCatalogue] = useState<ItemCatalogue>({ status: "loading" });
  const [itemAttempt, setItemAttempt] = useState(0);
  const dialogRef = useRef<HTMLDialogElement>(null);
  const triggerRef = useRef<HTMLButtonElement | null>(null);
  const teamNameRef = useRef<HTMLInputElement>(null);
  const pokemonById = useMemo(() => new Map(entries.map(pokemon => [pokemon.id, pokemon])), [entries]);
  const abilityOptions = useMemo(() => [...new Set(entries.flatMap(pokemon =>
    [pokemon.ability1, pokemon.ability2, pokemon.hiddenAbility].filter((ability): ability is string => Boolean(ability)),
  ))].sort((a, b) => a.localeCompare(b)).map(ability => ({ value: ability, label: ability })), [entries]);
  const selected = draft.members.map(member => member ? pokemonById.get(member.pokedexId) : undefined);
  const memberCount = draft.members.filter(Boolean).length;
  const canErase = Boolean(memberCount || draft.name || draft.modality || draft.game || draft.publicTeam);

  useEffect(() => {
    const controller = new AbortController();
    getMoves(controller.signal).then(moves => {
      if (!controller.signal.aborted) setMoveState({ status: "success", moves });
    }).catch(() => {
      if (!controller.signal.aborted) setMoveState({ status: "error" });
    });
    return () => controller.abort();
  }, [moveAttempt]);

  useEffect(() => {
    const controller = new AbortController();
    getEquippableItems(controller.signal).then(items => {
      if (!controller.signal.aborted) {
        setItemCatalogue({
          status: "success",
          options: items.map(({ name, type }) => ({ value: name, label: name, type })),
        });
      }
    }).catch(() => {
      if (!controller.signal.aborted) setItemCatalogue({ status: "error" });
    });
    return () => controller.abort();
  }, [itemAttempt]);

  useEffect(() => {
    const controller = new AbortController();
    getPokedexEntries(controller.signal).then(pokemon => {
      if (!controller.signal.aborted) {
        setEntries(pokemon);
        setLoadStatus("success");
      }
    }).catch(() => { if (!controller.signal.aborted) setLoadStatus("error"); });
    return () => controller.abort();
  }, [attempt]);

  function setDraft(update: Draft | ((previous: Draft) => Draft)) {
    const next = typeof update === "function" ? update(draft) : update;
    setDraftState(next);
    try {
      localStorage.setItem(STORAGE_KEY, JSON.stringify(next));
    } catch {
      // The draft remains editable and downloadable if browser storage is unavailable.
    }
  }

  useEffect(() => {
    if (dialog) {
      dialogRef.current?.showModal();
      dialogRef.current?.querySelector("input")?.focus();
    }
    else if (dialogRef.current?.open) {
      dialogRef.current.close();
      if (triggerRef.current?.disabled) teamNameRef.current?.focus();
    }
  }, [dialog]);

  function openDialog(state: NonNullable<DialogState>, trigger: HTMLButtonElement) {
    triggerRef.current = trigger;
    setQuery("");
    setPickerPanel(null);
    if (state.kind === "stat-points") {
      setStatPoints(draft.members[state.slot]?.statPoints ?? EMPTY_STAT_POINTS);
    }
    setDialog(state);
  }

  function closeDialog() { setDialog(null); triggerRef.current?.focus(); }
  function updateMember(slot: number, update: Partial<Member>) {
    setDraft(previous => ({ ...previous, members: previous.members.map((member, index) => index === slot && member ? { ...member, ...update } : member) }));
  }
  function removeMember(slot: number) {
    setDraft(previous => ({ ...previous, members: previous.members.map((value, index) => index === slot ? null : value) }));
  }
  function retryItemCatalogue() {
    setItemCatalogue({ status: "loading" });
    setItemAttempt(previous => previous + 1);
  }
  function retryMoves() {
    setMoveState({ status: "loading" });
    setMoveAttempt(previous => previous + 1);
  }
  function choosePokemon(pokemon: PokedexEntry) {
    if (dialog?.kind !== "pokemon") return;
    setDraft(previous => ({ ...previous, members: previous.members.map((member, index) => index === dialog.slot
      ? { pokedexId: pokemon.id, moves: ["", "", "", ""], ability: pokemon.ability1, item: "", statPoints: { ...EMPTY_STAT_POINTS } }
      : member) }));
    closeDialog();
  }

  const availableMoves = moveState.status === "success" ? moveState.moves : [];
  const attackTypesByName = useMemo(() => {
    const types = new Map<string, PokemonType>();
    if (moveState.status === "success") {
      for (const move of moveState.moves) {
        if ((move.moveClass !== "PHYSICAL" && move.moveClass !== "SPECIAL") || (move.power ?? 0) <= 0) continue;
        const type = findPokemonType(move.type);
        if (type) types.set(move.name.trim().toLowerCase(), type.name);
      }
    }
    return types;
  }, [moveState]);
  const analyzedTeam = draft.members.map((member, slot) => {
    const pokemon = selected[slot];
    if (!member || !pokemon) return undefined;
    const attackTypes = member.moves.flatMap(name => {
      const type = attackTypesByName.get(name.trim().toLowerCase());
      return type ? [type] : [];
    });
    return { type1: pokemon.type1, type2: pokemon.type2, attackTypes };
  });
  const matchingMoves = dialog?.kind === "move" ? getTeamPickerMoves(availableMoves, query, moveFilters, moveSort) : [];
  function updateMove(slot: number, index: number, name: string) {
    const member = draft.members[slot];
    if (!member) return;
    const moves: Member["moves"] = [...member.moves];
    moves[index] = name;
    updateMember(slot, { moves });
  }

  function chooseMove(move: MoveOption | null) {
    if (dialog?.kind !== "move") return;
    const member = draft.members[dialog.slot];
    if (!member || member.pokedexId !== dialog.pokedexId) return;
    updateMove(dialog.slot, dialog.index, move?.name ?? "");
    closeDialog();
  }
  const isPokemonPicker = dialog?.kind === "pokemon";
  const isPicker = isPokemonPicker || dialog?.kind === "move";
  const matches = isPokemonPicker ? getTeamPickerPokemon(entries, query, pokemonFilters, pokemonSort) : [];
  const spentPoints = getStatPointTotal(statPoints);
  const remainingPoints = STAT_POINT_BUDGET - spentPoints;

  function changeStatPoints(stat: PokedexStat, requestedPoints: number) {
    setStatPoints(previous => {
      const limit = getStatPointLimit(previous, stat);
      const points = Math.min(limit, Math.max(0, Math.trunc(requestedPoints) || 0));
      return { ...previous, [stat]: points };
    });
  }

  function saveStatPoints(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    if (dialog?.kind !== "stat-points" || !isValidStatPoints(statPoints)) return;
    updateMember(dialog.slot, { statPoints });
    closeDialog();
  }
  function downloadTeam() {
    const data = { ...draft, members: draft.members.map((member, slot) => member ? { ...member, name: selected[slot]?.name } : null) };
    const url = URL.createObjectURL(new Blob([JSON.stringify(data, null, 2)], { type: "application/json" }));
    const link = document.createElement("a");
    link.href = url; link.download = `${draft.name.trim().replace(/[^a-z0-9_-]+/gi, "-") || "pokemon-team"}.json`;
    link.click(); setTimeout(() => URL.revokeObjectURL(url), 1000);
  }
  function eraseTeam() {
    setDraft(emptyDraft());
    closeDialog();
  }

  const dialogTitle = dialog?.kind === "move"
    ? `Choose move ${dialog.index + 1}`
    : { pokemon: "Choose a Pokémon", "stat-points": "Stat Points", erase: "Erase team?" }[dialog?.kind ?? "pokemon"];

  return <div className="teams-page pokedex-page">
    <Header />
    <main className="teams-page__main">
      <div className="teams-page__heading"><img src={teamsIcon} alt="" /><h1>Pokemon <span>Team Editor</span></h1></div>
      <div className="teams-page__workspace">
        <section className="team-roster" aria-label={`Team Pokémon, ${memberCount} of ${TEAM_SIZE}`}>
          {draft.members.map((member, slot) => {
            const pokemon = selected[slot];
            return <div className={`team-slot${member ? " team-slot--filled" : ""}`} key={slot}>
              {member && pokemon ? <TeamCard
                pokemon={pokemon}
                slotNumber={slot + 1}
                moves={member.moves}
                ability={member.ability}
                item={member.item}
                itemCatalogue={itemCatalogue}
                onRetryItems={retryItemCatalogue}
                statPoints={member.statPoints}
                onRemove={() => removeMember(slot)}
                onReplace={trigger => openDialog({ kind: "pokemon", slot }, trigger)}
                onChooseMove={(index, trigger) => openDialog({ kind: "move", slot, index, pokedexId: pokemon.id }, trigger)}
                onClearMove={index => updateMove(slot, index, "")}
                onAbilityChange={ability => updateMember(slot, { ability })}
                onItemChange={item => updateMember(slot, { item })}
                onEditStatPoints={trigger => openDialog({ kind: "stat-points", slot }, trigger)}
              /> : member ? <div className="team-slot__unavailable"><p>{loadStatus === "loading" ? "Loading Pokémon…" : "Pokémon unavailable"}</p><button type="button" onClick={event => openDialog({ kind: "pokemon", slot }, event.currentTarget)}>Replace Pokémon</button><button type="button" onClick={() => removeMember(slot)}>Remove</button></div>
              : <button className="team-slot__add" type="button" aria-label={`Add Pokémon to slot ${slot + 1}`} onClick={event => openDialog({ kind: "pokemon", slot }, event.currentTarget)}>
                <span aria-hidden="true">
                  <svg viewBox="0 0 64 64" fill="none" stroke="currentColor" strokeWidth="4">
                    <path d="M16 32h32M32 16v32" />
                  </svg>
                </span>
                <span className="team-slot__label">Add Pokémon</span>
              </button>}
            </div>;
          })}
        </section>
        <aside className="team-details">
          <section className="team-data" aria-labelledby="team-data-title"><h2 id="team-data-title">TEAM DATA</h2>
            <label><span className="visually-hidden">Team name</span><input ref={teamNameRef} className="team-control" placeholder="NAME" maxLength={80} value={draft.name} onChange={event => setDraft({ ...draft, name: uppercaseInput(event.currentTarget) })} /></label>
            <div className="team-data__last-row">
              <Combobox
                inputClassName="team-control"
                label="Modality"
                placeholder="MODALITY"
                value={draft.modality}
                options={MODALITY_OPTIONS}
                onChange={modality => setDraft(previous => ({ ...previous, modality }))}
              />
              <label className="team-data__public">
                <AccentCheckbox checked={draft.publicTeam} onCheckedChange={publicTeam => setDraft({ ...draft, publicTeam })} />
                PUBLIC TEAM
              </label>
            </div>
          </section>
          <section className="type-analyzer" aria-labelledby="type-analyzer-title"><h2 id="type-analyzer-title">TYPE VIABILITY</h2>
            <div className="type-analyzer__grid">{ANALYZER_TYPES.map(type => {
              const { weak, covered, superEffective } = analyzeTeamTypes(analyzedTeam, type);
              const uncovered = weak - covered;
              const marks = Array.from({ length: Math.max(TEAM_SIZE, weak + superEffective) }, (_, index) =>
                index < uncovered ? "weak" : index < weak ? "covered" : index < weak + superEffective ? "strong" : "neutral");
              return <div className="type-analyzer__row" key={type}>
                <span className="type-analyzer__icon" style={{ "--type-color": getTypeColor(type) } as CSSProperties} title={type}><img src={TYPE_ICONS[`../../assets/icons/pokemonTypes/${type.toLowerCase()}.svg`]} alt={type} /></span>
                <div className={`type-analyzer__marks${marks.length > TEAM_SIZE ? " type-analyzer__marks--dense" : ""}`} role="img"
                  aria-label={`${type}: ${uncovered} weak, ${covered} covered, ${superEffective} super effective`}
                  title={`${uncovered} weak · ${covered} covered · ${superEffective} super effective`}>
                  {marks.map((mark, index) => <span key={index} aria-hidden="true" className={`type-analyzer__mark type-analyzer__mark--${mark}`} />)}
                </div>
              </div>;
            })}</div>
            {moveState.status === "error" && draft.members.some(member => member?.moves.some(Boolean)) && (
              <p className="type-analyzer__note" role="status">Could not load move coverage. <button type="button" onClick={retryMoves}>Retry</button></p>
            )}
          </section>
          <div className="teams-page__actions">
            <button className="teams-page__action" type="button" disabled={!memberCount} onClick={downloadTeam}>
              <svg aria-hidden="true" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                <path d="M12 3v12m-4-4 4 4 4-4M4 16v3a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2v-3" />
              </svg>
              DOWNLOAD
            </button>
            <button
              className="teams-page__action teams-page__action--erase"
              type="button"
              disabled={!canErase}
              aria-haspopup="dialog"
              onClick={event => openDialog({ kind: "erase" }, event.currentTarget)}
            >
              <svg aria-hidden="true" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round">
                <path d="m6 6 12 12M18 6 6 18" />
              </svg>
              ERASE
            </button>
          </div>
        </aside>
      </div>
    </main>
    <Footer />
    <dialog
      className={isPicker ? "team-dialog team-dialog--picker" : dialog?.kind === "erase" ? "team-dialog team-dialog--erase" : "team-dialog"}
      ref={dialogRef}
      aria-labelledby="team-dialog-title"
      aria-describedby={dialog?.kind === "erase" ? "team-erase-description" : undefined}
      onCancel={event => {
        if (!isPicker || !pickerPanel) {
          closeDialog();
          return;
        }
        event.preventDefault();
        event.currentTarget.querySelector<HTMLButtonElement>(".team-dialog__tool[aria-expanded=true]")?.focus();
        setPickerPanel(null);
      }}
      onClose={closeDialog}
      onClick={event => { if (event.target === event.currentTarget) closeDialog(); }}
    >
      <div className="team-dialog__header">
        <h2 id="team-dialog-title">{dialogTitle}</h2>
        {dialog?.kind !== "erase" && (
          <button type="button" className={isPicker ? "team-dialog__picker-close" : undefined} aria-label="Close dialog" onClick={closeDialog}>
            {isPicker ? (
              <svg aria-hidden="true" width="16" height="16" viewBox="0 0 16 16" fill="none" stroke="currentColor" strokeWidth="1.5" strokeLinecap="round">
                <path d="m4 4 8 8M12 4l-8 8" />
              </svg>
            ) : "×"}
          </button>
        )}
      </div>
      {dialog?.kind === "erase" && <>
        <p id="team-erase-description">This will clear all Pokémon and team settings from your draft.</p>
        <div className="team-dialog__erase-actions">
          <button className="team-dialog__secondary" type="button" autoFocus onClick={closeDialog}>CANCEL</button>
          <button className="team-dialog__primary" type="button" onClick={eraseTeam}>ERASE TEAM</button>
        </div>
      </>}
      {isPicker && (
        <>
          <div className="team-dialog__search">
            <label htmlFor="team-picker-search">{isPokemonPicker ? "Search Pokémon by name or ID" : "Search moves by name"}</label>
            <div className="team-dialog__search-row">
              <input
                id="team-picker-search"
                value={query}
                onChange={event => setQuery(event.target.value)}
                placeholder={isPokemonPicker ? "Search Pokémon…" : "Search moves…"}
              />
              <button
                className="team-dialog__tool"
                type="button"
                aria-label={isPokemonPicker ? "Filter Pokémon" : "Filter moves"}
                aria-expanded={pickerPanel === "filter"}
                aria-controls={pickerPanel === "filter" ? "team-picker-filter" : undefined}
                onClick={() => setPickerPanel(previous => previous === "filter" ? null : "filter")}
              >
                <svg aria-hidden="true" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                  <path d="M3 4h18l-7 8v5l-4 3v-8L3 4Z" />
                </svg>
              </button>
              <button
                className="team-dialog__tool"
                type="button"
                aria-label={isPokemonPicker ? "Sort Pokémon" : "Sort moves"}
                aria-expanded={pickerPanel === "sort"}
                aria-controls={pickerPanel === "sort" ? "team-picker-sort" : undefined}
                onClick={() => setPickerPanel(previous => previous === "sort" ? null : "sort")}
              >
                <img src={sortIcon} alt="" />
              </button>
            </div>
          </div>
          {pickerPanel === "sort" && (
            <div
              className="team-dialog__picker-panel team-dialog__sort"
              data-picker={isPokemonPicker ? "pokemon" : "move"}
              id="team-picker-sort"
              role="region"
              aria-label="Sort options"
            >
              {isPokemonPicker
                ? <SortOptions<PokedexSortCriterion["key"]> groups={TEAM_POKEMON_SORT_GROUPS} criteria={pokemonSort} onChange={setPokemonSort} />
                : <SortOptions groups={TEAM_MOVE_SORT_GROUPS} criteria={moveSort} onChange={setMoveSort} />}
            </div>
          )}
          {pickerPanel === "filter" && (
            <div className="team-dialog__picker-panel" id="team-picker-filter" role="region" aria-label="Filter options">
              <div className="team-dialog__filters">
                <div className="team-dialog__filter-select">
                  <label htmlFor="team-picker-type">Type</label>
                  <PokemonTypeCombobox
                    inputId="team-picker-type"
                    name="team-picker-type"
                    label="Type"
                    value={isPokemonPicker ? pokemonFilters.type : moveFilters.type}
                    clearLabel="Clear type filter"
                    liveSearch={false}
                    onChange={type => {
                      if (isPokemonPicker) setPokemonFilters(previous => ({ ...previous, type }));
                      else setMoveFilters(previous => ({ ...previous, type }));
                    }}
                  />
                </div>
                {isPokemonPicker ? (
                  <div className="team-dialog__filter-select">
                    <label htmlFor="team-picker-ability">Ability</label>
                    <Combobox
                      inputId="team-picker-ability"
                      label="Ability"
                      value={pokemonFilters.ability}
                      placeholder="Any ability"
                      options={abilityOptions}
                      clearLabel="Clear ability filter"
                      onChange={ability => setPokemonFilters(previous => ({ ...previous, ability }))}
                    />
                  </div>
                ) : (
                  <>
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
                  </>
                )}
              </div>
              <button className="team-dialog__reset-filters" type="button" onClick={() => {
                if (isPokemonPicker) setPokemonFilters(EMPTY_POKEMON_FILTERS);
                else setMoveFilters(EMPTY_MOVE_FILTERS);
              }}>Reset filters</button>
            </div>
          )}
        </>
      )}
      {dialog?.kind === "move" && <>
        <p className="team-dialog__picker-note">Choose a move for {selected[dialog.slot]?.name}.</p>
        {moveState.status === "loading" && <p role="status">Loading moves…</p>}
        {moveState.status === "error" && <div role="alert"><p>Could not load moves.</p><button type="button" className="team-dialog__primary" onClick={retryMoves}>Retry</button></div>}
        {moveState.status === "success" && <>
          <p className="team-dialog__count" role="status">{matchingMoves.length} moves found</p>
          <div className="team-dialog__results team-dialog__moves">{matchingMoves.map(move => {
            const member = draft.members[dialog.slot];
            const alreadySelected = member?.moves.some((name, index) => index !== dialog.index && name === move.name);
            const classIcon = MOVE_CLASS_ICONS.get(move.moveClass.toUpperCase());
            return <button type="button" key={move.name} disabled={alreadySelected} onClick={() => chooseMove(move)}>
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
            </button>;
          })}</div>
          {!availableMoves.length ? <p>No moves have been recorded in the catalogue.</p> : !matchingMoves.length && <p>No moves match your search.</p>}
        </>}
        <button className="team-dialog__primary" type="button" onClick={() => chooseMove(null)}>Clear move</button>
      </>}
      {dialog?.kind === "pokemon" && <>
        {loadStatus === "loading" && <p role="status">Loading Pokémon…</p>}
        {loadStatus === "error" && <div role="alert"><p>Could not load the Pokédex. Check that the server is running.</p><button type="button" className="team-dialog__primary" onClick={() => { setLoadStatus("loading"); setAttempt(previous => previous + 1); }}>Retry</button></div>}
        {loadStatus === "success" && <>
          <p className="team-dialog__count" role="status">{matches.length} Pokémon found</p>
          <div className="team-dialog__results team-dialog__pokemon">
            {matches.map(pokemon => {
              const types = getPokemonTypes(pokemon);

              return (
                <button
                  type="button"
                  key={pokemon.id}
                  aria-label={[pokemon.name, ...types].join(", ")}
                  onClick={() => choosePokemon(pokemon)}
                >
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
        </>}
      </>}
      {dialog?.kind === "stat-points" && (
        <form onSubmit={saveStatPoints} className="team-dialog__stat-points">
          <div className="team-dialog__stat-budget">
            <p role="status"><strong>{remainingPoints} SP remaining</strong> <span>{spentPoints} / {STAT_POINT_BUDGET} used</span></p>
            <progress aria-label="Stat Points used" value={spentPoints} max={STAT_POINT_BUDGET} />
          </div>
          {POKEDEX_STATS.map(({ stat, label }) => {
            const value = statPoints[stat];
            const limit = getStatPointLimit(statPoints, stat);
            return (
              <div key={stat} className="team-dialog__stat-row">
                <label htmlFor={`team-stat-${stat}`}>{label}</label>
                <div className="team-dialog__stat-stepper">
                  <button type="button" aria-label={`Decrease ${label}`} disabled={value === 0}
                    onClick={() => changeStatPoints(stat, value - 1)}>-</button>
                  <input id={`team-stat-${stat}`} type="number" min={0} max={limit} step={1} required value={value}
                    onChange={event => changeStatPoints(stat, event.currentTarget.valueAsNumber)} />
                  <button type="button" aria-label={`Increase ${label}`} disabled={value >= limit}
                    onClick={() => changeStatPoints(stat, value + 1)}>+</button>
                </div>
              </div>
            );
          })}
          <div className="team-dialog__stat-actions">
            <button type="button" disabled={spentPoints === 0} onClick={() => setStatPoints(EMPTY_STAT_POINTS)}>Reset</button>
            <button className="team-dialog__primary" type="submit">SAVE</button>
          </div>
        </form>
      )}
    </dialog>
  </div>;
}
