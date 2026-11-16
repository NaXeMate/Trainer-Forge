import { useEffect, useMemo, useRef, useState } from "react";
import type { CSSProperties, ReactNode, SubmitEvent } from "react";
import { Link, useLocation, useNavigate, useSearchParams } from "react-router";
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
import PokemonPicker from "../../components/common/pokemonPicker";
import type { PickerPanel } from "../../components/common/pokemonPicker";
import PokemonTypeBadge from "../../components/common/pokemonTypeBadge";
import PokemonTypeCombobox from "../../components/common/pokemonTypeCombobox";
import SortOptions from "../../components/common/sortOptions";
import StatisticSlider from "../../components/common/statisticSlider";
import { findPokemonType, POKEDEX_STATS } from "../../constants/pokedex";
import { gameCatalogue, teamNatures } from "../../constants/teamCatalogue";
import { useAuth } from "../../context/useAuth";
import { useSessionTeamDraft, useSessionTeamDraftRevision } from "../../hooks/useSessionTeamDraft";
import { ApiError } from "../../services/api";
import { getEquippableItems } from "../../services/itemService";
import { getPokedexEntries } from "../../services/pokedexService";
import { getMoves } from "../../services/moveService";
import { deleteSavedTeam, getCompleteTeam, getMyTeams, saveCompleteTeam } from "../../services/teamService";
import type { SavedTeam } from "../../services/teamService";
import type { MoveOption } from "../../types/moveTypes";
import type { PokedexEntry, PokedexStat } from "../../types/pokedexTypes";
import {
  EMPTY_STAT_POINTS,
  getStatPointLimit,
  getStatPointTotal,
  isValidStatPoints,
  STAT_POINT_BUDGET,
} from "../../utils/championsStatPoints";
import type { StatPoints } from "../../utils/championsStatPoints";
import { commonMonotypeTypes, emptyDraft, incompleteSlot, MODALITIES, newMember, readTeamFile, TEAM_SCHEMA_VERSION, TEAM_SIZE } from "../../utils/teamDraft";
import type { Draft, Member, Modality } from "../../utils/teamDraft";
import { getTypeColor } from "../../utils/pokemonPresentation";
import {
  EMPTY_MOVE_FILTERS,
  getTeamPickerMoves,
  MOVE_RANGES,
  TEAM_MOVE_SORT_GROUPS,
} from "../../utils/teamPicker";
import type { MoveSortCriterion } from "../../utils/teamPicker";
import { analyzeTeamTypes, getPokemonTypes } from "../../utils/typeEffectiveness";
import type { PokemonType } from "../../utils/typeEffectiveness";
import "../../styles/pokemonTypeColors.css";
import "../../styles/teamControl.css";
import "./teams.css";

const MODALITY_OPTIONS = MODALITIES.map(value => ({
  value, label: value[0] + value.slice(1).toLowerCase(),
}));
const GAME_OPTIONS = gameCatalogue.map(game => ({ value: String(game.id), label: game.name }));
const LOAD_OPTIONS = [
  { value: "profile", label: "From Profile..." },
  { value: "json", label: "From JSON..." },
];

const blankTeam = emptyDraft();

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

const ANALYZER_TYPES: readonly PokemonType[] = ["GRASS", "FIRE", "WATER", "NORMAL", "ELECTRIC", "GHOST", "FAIRY", "FLYING", "ICE", "ROCK", "GROUND", "PSYCHIC", "FIGHTING", "DARK", "BUG", "DRAGON", "POISON", "STEEL"];
const TYPE_ICONS = import.meta.glob<string>("../../assets/icons/pokemonTypes/*.svg", { eager: true, query: "?url", import: "default" });

type DialogState = { kind: "pokemon"; slot: number } | { kind: "stat-points"; slot: number }
  | { kind: "move"; slot: number; index: number; pokedexId: number }
  | { kind: "load"; source?: "profile" } | { kind: "clear" | "delete" } | null;
type MoveLoadState = { status: "loading" | "error" } | { status: "success"; moves: MoveOption[] };

/** Team builder backed by the current trainer's working draft. */
export default function Teams() {
  const { session, accessToken, logout, retry } = useAuth();
  const draftRevision = useSessionTeamDraftRevision();
  const { pathname, search } = useLocation();
  const [params] = useSearchParams();
  const requestedId = params.get("savedTeam");

  if (session.status === "authenticated" && accessToken) {
    const editorKey = `${session.profile.id}:${draftRevision}`;
    if (requestedId !== null) {
      const id = Number(requestedId);
      if (!/^[1-9]\d*$/.test(requestedId) || !Number.isSafeInteger(id)) {
        return <TeamStatus><p role="alert">Team not found.</p><Link to="/teams" replace>Open team editor</Link></TeamStatus>;
      }
      return <RequestedTeam key={`${editorKey}:${id}`} id={id} token={accessToken} username={session.profile.username} />;
    }
    return <TeamEditor key={editorKey} token={accessToken} username={session.profile.username} />;
  }

  return <TeamStatus>
    {session.status === "checking" && <p role="status">Checking your session…</p>}
    {session.status === "guest" && <Link to="/login" state={{ from: pathname + search }}>Log in to create or edit teams</Link>}
    {session.status === "error" && <>
      <p role="alert">Could not verify your session.</p>
      <button type="button" onClick={retry}>Retry</button>{" · "}
      <button type="button" onClick={logout}>Use another account</button>
    </>}
  </TeamStatus>;
}

function TeamStatus({ children }: { children: ReactNode }) {
  return <div className="teams-page">
    <Header />
    <main className="teams-page__main teams-page__saved-status">
      <h1>Team Editor</h1>
      {children}
    </main>
    <Footer />
  </div>;
}

function RequestedTeam({ id, token, username }: { id: number; token: string; username: string }) {
  const { working, replace } = useSessionTeamDraft();
  const { logout } = useAuth();
  const navigate = useNavigate();
  const { state } = useLocation();
  const [allowed, setAllowed] = useState(() => !working?.dirty);
  const [error, setError] = useState("");
  const alreadyOpen = working?.origin.kind === "saved" && working.origin.id === id;

  useEffect(() => {
    if (alreadyOpen) {
      void navigate("/teams", { replace: true, state });
      return;
    }
    if (!allowed) return;
    const controller = new AbortController();
    const { signal } = controller;
    getCompleteTeam(id, token, signal, username).then(team => {
      if (signal.aborted) return;
      if (!replace({ draft: team.draft, origin: { kind: "saved", id }, dirty: false })) return;
      void navigate("/teams", { replace: true, state });
    }).catch(failure => {
      if (signal.aborted) return;
      if (failure instanceof ApiError && failure.status === 401) logout();
      setError(failure instanceof Error ? failure.message : "Could not load the requested team.");
    });
    return () => controller.abort();
  }, [alreadyOpen, allowed, id, token, username, replace, navigate, state, logout]);

  return <TeamStatus>
    {!allowed && !alreadyOpen && <>
      <p role="alert">Loading this team will replace your unsaved changes.</p>
      <button type="button" onClick={() => setAllowed(true)}>Replace draft and open team</button>
    </>}
    {(allowed || alreadyOpen) && !error && <p role="status">Loading team…</p>}
    {error && <p role="alert">{error}</p>}
    <p><Link to="/teams" replace state={state}>Back to the editor</Link></p>
  </TeamStatus>;
}

function TeamEditor({ token, username }: { token: string; username: string }) {
  const { working, replace, update } = useSessionTeamDraft();
  const { logout } = useAuth();
  const draft = working?.draft ?? blankTeam;
  const [actionError, setActionError] = useState<string>();
  const [busy, setBusy] = useState(false);
  const operation = useRef<AbortController | null>(null);
  const fileInput = useRef<HTMLInputElement>(null);
  const [profileAttempt, setProfileAttempt] = useState(0);
  const [profile, setProfile] = useState<{ status: "loading" | "error" } |
    { status: "ready"; teams: SavedTeam[] }>({ status: "loading" });
  const [entries, setEntries] = useState<PokedexEntry[]>([]);
  const [loadStatus, setLoadStatus] = useState<"loading" | "success" | "error">("loading");
  const [attempt, setAttempt] = useState(0);
  const [dialog, setDialog] = useState<DialogState>(null);
  const [query, setQuery] = useState("");
  const [pickerPanel, setPickerPanel] = useState<PickerPanel | null>(null);
  const [moveSort, setMoveSort] = useState<MoveSortCriterion[]>([]);
  const [moveFilters, setMoveFilters] = useState(EMPTY_MOVE_FILTERS);
  const [statPoints, setStatPoints] = useState<StatPoints>(EMPTY_STAT_POINTS);
  const [moveState, setMoveState] = useState<MoveLoadState>({ status: "loading" });
  const [moveAttempt, setMoveAttempt] = useState(0);
  const [itemCatalogue, setItemCatalogue] = useState<ItemCatalogue>({ status: "loading" });
  const [itemAttempt, setItemAttempt] = useState(0);
  const dialogRef = useRef<HTMLDialogElement>(null);
  const triggerRef = useRef<HTMLButtonElement | null>(null);
  const pokemonById = useMemo(() => new Map(entries.map(pokemon => [pokemon.id, pokemon])), [entries]);
  const selected = draft.members.map(member => member ? pokemonById.get(member.pokedexId) : undefined);
  const memberCount = draft.members.filter(Boolean).length;
  const monotypeTypes = draft.modality === "MONOTYPE" && dialog?.kind === "pokemon"
    ? commonMonotypeTypes(draft.members, pokemonById, dialog.slot) : null;

  useEffect(() => {
    const controller = new AbortController();
    const { signal } = controller;
    getMyTeams(username, token, signal).then(teams => {
      if (!signal.aborted) setProfile({ status: "ready", teams });
    }).catch(error => {
      if (signal.aborted) return;
      if (error instanceof ApiError && error.status === 401) logout();
      setProfile({ status: "error" });
      controller.abort();
    });
    return () => controller.abort();
  }, [token, username, logout, profileAttempt]);

  useEffect(() => () => operation.current?.abort(), []);

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

  function setDraft(change: Draft | ((previous: Draft) => Draft)) {
    if (operation.current) return;
    update(typeof change === "function" ? change(draft) : change);
    setActionError(undefined);
  }

  function confirmReplacement(message = "Replace the team and discard your unsaved changes?") {
    return !working?.dirty || window.confirm(message);
  }

  function nameNewTeam(name: string) {
    if (operation.current) return;
    replace({ draft: { ...draft, name }, origin: { kind: "new" }, dirty: true });
    setActionError(undefined);
  }

  async function runOperation(action: (signal: AbortSignal) => Promise<void>) {
    if (operation.current) return;
    const controller = new AbortController();
    operation.current = controller;
    setBusy(true);
    setActionError(undefined);
    try {
      await action(controller.signal);
    } catch (error) {
      if (controller.signal.aborted) return;
      if (error instanceof ApiError && error.status === 401) logout();
      setActionError(error instanceof Error ? error.message : "The operation failed.");
    } finally {
      operation.current = null;
      if (!controller.signal.aborted) setBusy(false);
    }
  }

  function loadFromProfile(value: string) {
    if (profile.status !== "ready") return;
    const chosen = profile.teams.find(team => String(team.id) === value);
    if (!chosen || !confirmReplacement()) return;
    if (dialog?.kind === "load") closeDialog();
    void runOperation(async signal => {
      const { id } = chosen;
      const team = await getCompleteTeam(id, token, signal, username);
      if (signal.aborted) return;
      replace({ draft: team.draft, origin: { kind: "saved", id }, dirty: false });
    });
  }

  function importFile(file: File) {
    if (loadStatus !== "success" || moveState.status !== "success" || itemCatalogue.status !== "success") {
      setActionError("The Pokémon, move and item catalogues must be loaded before importing a team.");
      return;
    }
    const moves = new Set(moveState.moves.map(move => move.name));
    const items = new Set(itemCatalogue.options.map(item => item.value));
    void runOperation(async signal => {
      if (file.size > 256 * 1024) throw new Error("The team file is too large.");
      const raw = await file.text();
      if (signal.aborted) return;
      let data: unknown;
      try {
        data = JSON.parse(raw);
      } catch {
        throw new Error("The file does not contain valid JSON.");
      }
      const imported = readTeamFile(data, { pokedex: pokemonById, moves, items, games: gameCatalogue, natures: teamNatures });
      if (imported.modalityChanged) {
        const warning = "The file uses a legacy modality. It will open as Normal.";
        const question = working?.dirty ? " Replace the draft and discard your unsaved changes?" : " Open this team?";
        if (!window.confirm(warning + question)) return;
      } else if (!confirmReplacement()) return;
      replace({ draft: imported.draft, origin: { kind: "import" }, dirty: true });
    });
  }

  function saveTeam() {
    if (!working) return;
    let message = "";
    const slot = incompleteSlot(draft);
    if (!draft.name.trim()) message = "Enter a team name.";
    else if (draft.gameId === null) message = "Choose a game before saving.";
    else if (slot !== -1) message = `Slot ${slot + 1} needs an ability and a move in its first move slot.`;
    else if (draft.modality === "MONOTYPE" && commonMonotypeTypes(draft.members, pokemonById)?.length === 0) {
      message = "All Pokémon in a Monotype team must share a type.";
    }
    if (message) {
      setActionError(message);
      return;
    }
    const id = working.origin.kind === "saved" ? working.origin.id : null;
    void runOperation(async signal => {
      const team = await saveCompleteTeam(draft, id, token, signal);
      if (signal.aborted) return;
      if (!replace({ draft: team.draft, origin: { kind: "saved", id: team.id }, dirty: false })) return;
      setProfileAttempt(previous => previous + 1);
    });
  }

  function deleteTeam() {
    if (savedId === null) return;
    closeDialog();
    void runOperation(async signal => {
      await deleteSavedTeam(savedId, token, signal);
      if (signal.aborted || !replace({ draft: emptyDraft(), origin: { kind: "new" }, dirty: false })) return;
      setProfile(current => current.status === "ready"
        ? { ...current, teams: current.teams.filter(team => team.id !== savedId) } : current);
      setProfileAttempt(previous => previous + 1);
    });
  }

  function changeModality(modality: Modality) {
    if (modality === "MONOTYPE" && commonMonotypeTypes(draft.members, pokemonById)?.length === 0) {
      setActionError("All Pokémon in a Monotype team must share a type.");
      return;
    }
    setDraft({ ...draft, modality });
  }

  useEffect(() => {
    if (dialog) {
      dialogRef.current?.showModal();
      dialogRef.current?.querySelector<HTMLInputElement>("input:not([hidden])")?.focus();
    }
    else if (dialogRef.current?.open) {
      dialogRef.current.close();
      if (triggerRef.current?.disabled) document.getElementById("team-name")?.focus();
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

  function chooseLoadSource(source: string) {
    if (source === "profile") setDialog({ kind: "load", source });
    if (source === "json") {
      dialogRef.current?.close();
      closeDialog();
      fileInput.current?.click();
    }
  }

  function retryProfile() {
    setProfile({ status: "loading" });
    setProfileAttempt(previous => previous + 1);
  }
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
    if (monotypeTypes && !getPokemonTypes(pokemon).some(type => monotypeTypes.includes(type))) return;
    setDraft(previous => ({ ...previous, members: previous.members.map((member, index) => index === dialog.slot
      ? newMember(pokemon)
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
  const spentPoints = getStatPointTotal(statPoints);
  const remainingPoints = STAT_POINT_BUDGET - spentPoints;

  function changeStatPoints(stat: PokedexStat, requestedPoints: number) {
    setStatPoints(previous => {
      const points = Math.min(getStatPointLimit(previous, stat), Math.max(0, Math.trunc(requestedPoints) || 0));
      return { ...previous, [stat]: points };
    });
  }

  function saveStatPoints(event: SubmitEvent<HTMLFormElement>) {
    event.preventDefault();
    if (dialog?.kind !== "stat-points" || !isValidStatPoints(statPoints)) return;
    updateMember(dialog.slot, { statPoints });
    closeDialog();
  }

  function downloadTeam() {
    const data = {
      schemaVersion: TEAM_SCHEMA_VERSION, ...draft,
      members: draft.members.map((member, slot) => member ? { ...member, name: selected[slot]?.name } : null),
    };
    const url = URL.createObjectURL(new Blob([JSON.stringify(data, null, 2)], { type: "application/json" }));
    const link = document.createElement("a");
    link.href = url;
    link.download = `${draft.name.trim().replace(/[^a-z0-9_-]+/gi, "-") || "pokemon-team"}.json`;
    link.click();
    setTimeout(() => URL.revokeObjectURL(url), 1000);
  }

  const savedId = working?.origin.kind === "saved" ? working.origin.id : null;
  const profileOptions = profile.status === "ready"
    ? profile.teams.map(team => ({ value: String(team.id), label: team.name })) : [];
  const isConfirmation = dialog?.kind === "clear" || dialog?.kind === "delete";

  const dialogTitle = dialog?.kind === "move"
    ? `Choose move ${dialog.index + 1}`
    : { pokemon: "Choose a Pokémon", "stat-points": "Stat Points", load: "Load team", clear: "Clear draft?", delete: "Delete saved team?" }[dialog?.kind ?? "pokemon"];

  return <div className="teams-page">
    <Header />
    <main className="teams-page__main">
      <div className="teams-page__heading"><img src={teamsIcon} alt="" /><h1>Team <span>Editor</span></h1></div>
      <div className="teams-page__workspace" inert={busy}>
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
          <section className="team-data" aria-labelledby="team-data-title"><h2 id="team-data-title">DATA</h2>
            <Combobox inputId="team-name" label="Team name" placeholder="NAME" maxLength={100} inputClassName="team-control"
              value={savedId === null ? "" : String(savedId)} displayValue={draft.name} options={profileOptions}
              createOption={{ label: "New Team", onSelect: nameNewTeam }}
              invalidMessage="Choose a saved team or select New Team to use this name."
              onChange={loadFromProfile} />
            <Combobox label="Game" placeholder="GAME" inputClassName="team-control"
              value={draft.gameId === null ? "" : String(draft.gameId)}
              displayValue={GAME_OPTIONS.find(option => option.value === String(draft.gameId))?.label ?? ""}
              options={GAME_OPTIONS} clearLabel="Clear game" onChange={value => setDraft({ ...draft, gameId: value ? Number(value) : null })} />
            <div className="team-data__last-row">
              <Combobox label="Modality" inputClassName="team-control" value={draft.modality} options={MODALITY_OPTIONS}
                onChange={value => { if (value) changeModality(value as Modality); }} />
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
          <div className="teams-page__actions" role="group" aria-label="Team actions">
            <button className="teams-page__action" type="button" aria-label="Save team" title="Save team"
              disabled={busy || !working} onClick={saveTeam}>
              <svg aria-hidden="true" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                <path d="M19 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h12l4 4v12a2 2 0 0 1-2 2ZM7 3v5h10V3M7 21v-8h10v8" />
              </svg>
            </button>
            <button className="teams-page__action" type="button" aria-label="Load team" title="Load team"
              disabled={busy} aria-haspopup="dialog"
              onClick={event => openDialog({ kind: "load" }, event.currentTarget)}>
              <svg aria-hidden="true" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                <path d="M3 18V6a2 2 0 0 1 2-2h4l3 3h7a2 2 0 0 1 2 2v9a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2Z" />
              </svg>
            </button>
            <button className="teams-page__action" type="button" aria-label="Export JSON" title="Export JSON"
              disabled={busy || !working} onClick={downloadTeam}>
              <svg aria-hidden="true" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                <path d="M12 4v12m-4-4 4 4 4-4M4 15v4a1 1 0 0 0 1 1h14a1 1 0 0 0 1-1v-4" />
              </svg>
            </button>
            <button className="teams-page__action" type="button" aria-label="Clear draft" title="Clear draft"
              disabled={busy || !working} aria-haspopup="dialog"
              onClick={event => openDialog({ kind: "clear" }, event.currentTarget)}>
              <svg aria-hidden="true" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                <path d="m6 6 12 12M18 6 6 18" />
              </svg>
            </button>
            <button
              className="teams-page__action teams-page__action--erase"
              type="button"
              aria-label="Delete team"
              title="Delete team"
              disabled={busy || savedId === null}
              aria-haspopup="dialog"
              onClick={event => openDialog({ kind: "delete" }, event.currentTarget)}
            >
              <svg aria-hidden="true" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round">
                <path d="M4 7h16M9 7V4h6v3M6 7l1 13h10l1-13M10 11v5m4-5v5" />
              </svg>
            </button>
          </div>
          {actionError && <p className="teams-page__action-error" role="alert">{actionError}</p>}
        </aside>
      </div>
    </main>
    <Footer />
    <input ref={fileInput} type="file" accept=".json,application/json" hidden onChange={event => {
      const file = event.currentTarget.files?.[0];
      event.currentTarget.value = "";
      if (file) importFile(file);
    }} />
    <dialog
      className={isPicker ? "team-dialog team-dialog--picker" : isConfirmation ? "team-dialog team-dialog--erase" :
        dialog?.kind === "load" ? "team-dialog team-dialog--load" : "team-dialog"}
      ref={dialogRef}
      aria-labelledby="team-dialog-title"
      aria-describedby={isConfirmation ? "team-erase-description" : undefined}
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
        {!isConfirmation && (
          <button type="button" className={isPicker ? "team-dialog__picker-close" : undefined} aria-label="Close dialog" onClick={closeDialog}>
            {isPicker ? (
              <svg aria-hidden="true" width="16" height="16" viewBox="0 0 16 16" fill="none" stroke="currentColor" strokeWidth="1.5" strokeLinecap="round">
                <path d="m4 4 8 8M12 4l-8 8" />
              </svg>
            ) : "×"}
          </button>
        )}
      </div>
      {isConfirmation && <>
        <p id="team-erase-description">{dialog?.kind === "delete"
          ? `Delete “${draft.name}” from your profile and clear this draft? This cannot be undone.`
          : "This will clear all Pokémon and team settings from your draft."}</p>
        <div className="team-dialog__erase-actions">
          <button className="team-dialog__secondary" type="button" autoFocus onClick={closeDialog}>CANCEL</button>
          <button className="team-dialog__primary" type="button" onClick={() => {
            if (dialog?.kind === "delete") { deleteTeam(); return; }
            replace({ draft: emptyDraft(), origin: { kind: "new" }, dirty: false });
            setActionError(undefined);
            closeDialog();
          }}>{dialog?.kind === "delete" ? "DELETE TEAM" : "CLEAR DRAFT"}</button>
        </div>
      </>}
      {dialog?.kind === "load" && (dialog.source === "profile" ? <>
        {profile.status === "loading" && <p role="status">Loading saved teams…</p>}
        {profile.status === "error" && <p role="alert">Could not load your saved teams.{" "}
          <button type="button" className="team-dialog__primary" onClick={retryProfile}>Retry</button></p>}
        {profile.status === "ready" && (profileOptions.length ?
          <Combobox label="Teams from my profile" placeholder="Choose a team" value="" inputClassName="team-control"
            options={profileOptions} onChange={loadFromProfile} /> : <p>No saved teams.</p>)}
        <button className="team-dialog__secondary team-dialog__load-back" type="button"
          onClick={() => setDialog({ kind: "load" })}>Back</button>
      </> :
        <Combobox label="Load source" placeholder="Choose a source" value="" inputClassName="team-control"
          options={LOAD_OPTIONS} readOnly instantSelection onChange={chooseLoadSource} />)}
      {dialog?.kind === "move" && (
        <>
          <div className="team-dialog__search">
            <label htmlFor="team-picker-search">Search moves by name</label>
            <div className="team-dialog__search-row--tools">
              <input
                id="team-picker-search"
                value={query}
                onChange={event => setQuery(event.target.value)}
                placeholder="Search moves…"
              />
              <button
                className="team-dialog__tool"
                type="button"
                aria-label="Filter moves"
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
                aria-label="Sort moves"
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
              data-picker="move"
              id="team-picker-sort"
              role="region"
              aria-label="Sort options"
            >
              <SortOptions groups={TEAM_MOVE_SORT_GROUPS} criteria={moveSort} onChange={setMoveSort} />
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
              <button className="team-dialog__reset-filters" type="button" onClick={() => setMoveFilters(EMPTY_MOVE_FILTERS)}>Reset filters</button>
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
      <PokemonPicker
        active={isPokemonPicker}
        entries={entries}
        requiredTypes={monotypeTypes}
        status={loadStatus}
        query={query}
        onQueryChange={setQuery}
        panel={pickerPanel}
        onPanelChange={setPickerPanel}
        onRetry={() => { setLoadStatus("loading"); setAttempt(previous => previous + 1); }}
        onSelect={choosePokemon}
      />
      {dialog?.kind === "stat-points" && (
        <form onSubmit={saveStatPoints} className="team-dialog__stat-points">
          <div className="team-dialog__stat-budget">
            <p role="status">
              <strong>{remainingPoints < 0 ? `${-remainingPoints} SP over budget` : `${remainingPoints} SP remaining`}</strong>
              {" "}<span>{spentPoints} / {STAT_POINT_BUDGET} used</span>
            </p>
            <progress aria-label="Stat Points used" value={Math.min(spentPoints, STAT_POINT_BUDGET)} max={STAT_POINT_BUDGET} />
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
            <button className="team-dialog__primary" type="submit" disabled={!isValidStatPoints(statPoints)}>SAVE</button>
          </div>
        </form>
      )}
    </dialog>
  </div>;
}
