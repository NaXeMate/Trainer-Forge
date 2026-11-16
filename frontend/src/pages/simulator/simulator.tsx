import { useEffect, useRef, useState, type ReactNode } from "react";
import { Link, useLocation, useSearchParams } from "react-router";
import simulatorIcon from "../../assets/brand/sections/simulator_icon.png";
import Header from "../../components/layout/header";
import Footer from "../../components/layout/footer";
import { useAuth } from "../../context/useAuth";
import { useSessionTeamDraft, useSessionTeamDraftRevision } from "../../hooks/useSessionTeamDraft";
import { ApiError } from "../../services/api";
import { getPokedexEntries } from "../../services/pokedexService";
import { getCompleteTeam, getEligibleTeams, simulateBattle } from "../../services/teamService";
import type { BattleResult, BattleSelection, CompleteTeam, SavedTeam } from "../../services/teamService";
import type { PokedexEntry } from "../../types/pokedexTypes";
import { commonMonotypeTypes, incompleteSlot, TEAM_SIZE } from "../../utils/teamDraft";
import type { Draft } from "../../utils/teamDraft";
import "./simulator.css";

type Side = "one" | "two";
type PanelOutcome = "win" | "loss" | "draw";
type Choice = { key: "draft"; name: string } | { key: string; team: SavedTeam; name: string };
type Detail = { team: CompleteTeam } | { error: string };
type Catalogue = {
  own: SavedTeam[];
  demo: SavedTeam[];
  pokedex: Map<number, PokedexEntry>;
};
type BattleState = { key: string; result: BattleResult } | { key: string; error: string };

const sideLabels: Record<Side, string> = { one: "P1 (Player)", two: "P2 (AI)" };
const otherSide: Record<Side, Side> = { one: "two", two: "one" };
const sides: Side[] = ["one", "two"];
const emptySlots = Array.from({ length: TEAM_SIZE }, () => null);

function TeamPanel({ side, name = "No team selected", draft, pokedex, edit, outcome, onChoose }: {
  side: Side;
  name?: string;
  draft?: Draft;
  pokedex?: Map<number, PokedexEntry>;
  edit?: string;
  outcome?: PanelOutcome;
  onChoose?: (side: Side) => void;
}) {
  const sideLabel = sideLabels[side];
  const resultLabel = outcome === "win" ? "Victory" : outcome === "loss" ? "Defeat" : "Draw";
  return (
    <section className={`simulator-team simulator-team--${side}`} aria-label={sideLabel}>
      {outcome && <p className={`simulator-team__outcome simulator-team__outcome--${outcome}`}>{resultLabel}</p>}
      <button className="simulator-team__roster" type="button"
        aria-label={`Choose team for ${sideLabel}: ${name}`}
        aria-haspopup={onChoose ? "dialog" : undefined} disabled={!onChoose} onClick={() => onChoose?.(side)}>
        {(draft?.members ?? emptySlots).map((member, index) => {
          const pokemon = member ? pokedex?.get(member.pokedexId) : undefined;
          return <span className="simulator-team__slot" key={index}>
            {pokemon ? <img src={pokemon.imageUrl} alt={pokemon.name} />
              : <span aria-hidden="true" className="simulator-team__empty-slot">—</span>}
          </span>;
        })}
      </button>
      <div className="simulator-team__identity">
        <p className="simulator-team__role">{sideLabel}</p>
        <h2 className="simulator-team__name">{name}</h2>
      </div>
      {edit && <Link className="simulator-team__edit" to={edit}
        aria-label={`Edit this team: ${name}`}>Edit this team...</Link>}
    </section>
  );
}

function BattleButton({ onBattle }: { onBattle?: () => void }) {
  return <button className="simulator-arena__start" type="button" aria-label="Start battle"
    disabled={!onBattle} onClick={onBattle}>
    <img src={simulatorIcon} alt="" width={260} height={260} />
  </button>;
}

function EmptyArena({ children }: { children: ReactNode }) {
  return <>
    <TeamPanel side="one" />
    <TeamPanel side="two" />
    <div className="simulator-arena__battle"><BattleButton />{children}</div>
  </>;
}


function SavedArena({ token, username }: { token: string; username: string }) {
  const { logout } = useAuth();
  const [attempt, setAttempt] = useState(0);
  const [loaded, setLoaded] = useState<{ catalogue: Catalogue; started: number }>();
  const [error, setError] = useState("");

  useEffect(() => {
    const controller = new AbortController();
    const { signal } = controller;
    const started = performance.now();
    Promise.all([getEligibleTeams(username, token, signal), getPokedexEntries(signal)])
      .then(([teams, entries]) => {
        if (!signal.aborted) setLoaded({ started, catalogue: { ...teams, pokedex: new Map(entries.map(entry => [entry.id, entry])) } });
      })
      .catch(failure => {
        if (signal.aborted) return;
        if (failure instanceof ApiError && failure.status === 401) logout();
        setError("Could not load teams or the Pokédex.");
        controller.abort();
      });
    return () => controller.abort();
  }, [token, username, logout, attempt]);

  if (loaded) return <Matchup token={token} catalogue={loaded.catalogue} initialStart={loaded.started} />;

  return <EmptyArena>
    {error ? <p role="alert">{error}{" "}
      <button type="button" onClick={() => { setError(""); setAttempt(previous => previous + 1); }}>Retry</button>
    </p> : <p role="status">Loading teams…</p>}
  </EmptyArena>;
}

function choices(teams: SavedTeam[]): Choice[] {
  return teams.map(team => ({ key: String(team.id), name: team.name, team }));
}

function requestedChoice(params: URLSearchParams, side: Side) {
  const value = params.get(side);
  return value === "draft" || value === "none" || (value && /^[1-9]\d*$/.test(value)) ? value : null;
}

function panelOutcome(result: BattleResult | undefined, side: Side): PanelOutcome | undefined {
  if (!result?.outcome) return;
  if (result.outcome === "DRAW") return "draw";
  return (result.outcome === "TEAM_ONE" ? "one" : "two") === side ? "win" : "loss";
}

type MatchupMeasurement = { start: number; key: string; requests: number };

function beginMeasurement(key: string, start = performance.now()): MatchupMeasurement {
  return { start, key, requests: 0 };
}

function Matchup({ token, catalogue, initialStart }: { token: string; catalogue: Catalogue; initialStart: number }) {
  const { logout } = useAuth();
  const { working, storageAvailable } = useSessionTeamDraft();
  const [params, setParams] = useSearchParams();
  const [pickerSide, setPickerSide] = useState<Side>("one");
  const picker = useRef<HTMLDialogElement>(null);
  const [battle, setBattle] = useState<BattleState>();
  const [pending, setPending] = useState(false);
  const battleRequest = useRef<AbortController | null>(null);
  const [details, setDetails] = useState(new Map<number, Detail>());
  const completed = useRef(new Map<number, Detail>());
  const requests = useRef(new Map<number, AbortController>());
  const [retry, setRetry] = useState(0);
  const uptime = useRef<MatchupMeasurement | null>(null);

  const own = choices(catalogue.own);
  const demo = choices(catalogue.demo);
  const local: Choice[] = working ? [{ key: "draft", name: working.draft.name.trim() || "Unnamed team" }] : [];
  const options = { one: [...own, ...local], two: [...own, ...local, ...demo] };
  const requestedOne = requestedChoice(params, "one");
  const requestedTwo = requestedChoice(params, "two");
  const one = requestedOne ? options.one.find(team => team.key === requestedOne)
    : options.one.find(team => team.key !== requestedTwo);
  const two = requestedTwo ? options.two.find(team => team.key === requestedTwo && team.key !== one?.key)
    : options.two.find(team => team.key !== one?.key);
  const selected = { one, two };
  const missing = (requestedOne && requestedOne !== "none" && !one) || (requestedTwo && requestedTwo !== "none" && !two);
  const firstId = one && "team" in one ? one.team.id : undefined;
  const secondId = two && "team" in two ? two.team.id : undefined;
  const battleKey = `${one?.key ?? ""}:${two?.key ?? ""}`;

  useEffect(() => {
    const activeRequests = requests.current;
    return () => {
      activeRequests.forEach(controller => controller.abort());
      activeRequests.clear();
      battleRequest.current?.abort();
    };
  }, []);

  useEffect(() => {
    const measure = beginMeasurement(battleKey, uptime.current ? undefined : initialStart);
    uptime.current = measure;
    for (const id of [firstId, secondId]) {
      if (id === undefined || completed.current.has(id)) continue;
      if (requests.current.has(id)) continue;
      const controller = new AbortController();
      const { signal } = controller;
      requests.current.set(id, controller);
      const store = (value: Detail) => {
        if (signal.aborted) return;
        completed.current.set(id, value);
        setDetails(new Map(completed.current));
      };
      measure.requests++;
      getCompleteTeam(id, token, signal).then(team => store({ team })).catch(error => {
        if (signal.aborted) return;
        if (error instanceof ApiError && error.status === 401) logout();
        const description = error instanceof ApiError && error.status === 404
          ? "A selected team is no longer available. Choose another team."
          : "Could not load a selected team.";
        store({ error: description });
      }).finally(() => {
        if (requests.current.get(id) === controller) requests.current.delete(id);
      });
    }
  }, [firstId, secondId, battleKey, token, logout, retry, initialStart]);

  const match = sides.map(side => {
    const choice = selected[side];
    const detail = choice && "team" in choice ? details.get(choice.team.id) : undefined;
    return {
      side, choice, detail,
      draft: choice?.key === "draft" ? working?.draft : detail && "team" in detail ? detail.team.draft : undefined,
    };
  });
  const [left, right] = match;
  const loadError = match.find(({ detail }) => detail && "error" in detail)?.detail;
  let hint = "";
  if (missing) hint = "A previous selection is no longer available. Choose another team.";
  else if (!one || !two) hint = "Choose a team for each side.";
  else if (left.draft && right.draft) {
    for (const { side, draft } of match) {
      if (!draft) continue;
      const count = draft.members.filter(Boolean).length;
      const missingMove = incompleteSlot(draft);
      if (!count) hint = `${sideLabels[side]} needs at least one Pokémon.`;
      else if (missingMove !== -1) hint = `${sideLabels[side]}, slot ${missingMove + 1} needs an ability and a first move.`;
      else if (draft.modality === "MONOTYPE" && commonMonotypeTypes(draft.members, catalogue.pokedex)?.length === 0) {
        hint = `${sideLabels[side]} must share one Pokémon type.`;
      }
      if (hint) break;
    }
    if (!hint && left.draft.members.filter(Boolean).length !== right.draft.members.filter(Boolean).length) {
      hint = "Both teams need the same number of Pokémon.";
    }
  }
  const ready = Boolean(left.draft && right.draft && !hint);

  useEffect(() => {
    const measure = uptime.current;
    if (ready && measure?.key === battleKey) {
      performance.measure("trainerforge:simulator:ready", {
        start: measure.start, end: performance.now(), detail: { teamDetailRequests: measure.requests },
      });
      uptime.current = { ...measure, key: "" };
    }
  }, [ready, battleKey]);

  const matchup = new URLSearchParams();
  for (const side of sides) if (selected[side]) matchup.set(side, selected[side].key);

  function canSwap(choice: Choice, side: Side) {
    if (choice.key !== selected[otherSide[side]]?.key) return true;
    const current = selected[side];
    return !current || options[otherSide[side]].some(option => option.key === current.key);
  }

  function choose(choice: Choice) {
    if (!canSwap(choice, pickerSide)) return;
    const opposite = otherSide[pickerSide];
    const next = new URLSearchParams(matchup);
    if (choice.key === selected[opposite]?.key) next.set(opposite, selected[pickerSide]?.key ?? "none");
    next.set(pickerSide, choice.key);
    picker.current?.close();
    setBattle(undefined);
    setParams(next, { replace: true, preventScrollReset: true });
  }

  async function startBattle() {
    if (!ready || battleRequest.current || !one || !two || !left.draft || !right.draft) return;
    function toSelection(choice: Choice, draft: Draft): BattleSelection {
      return "team" in choice ? { team: choice.team } : { draft };
    }
    const controller = new AbortController();
    const { signal } = controller;
    battleRequest.current = controller;
    setPending(true);
    setBattle(undefined);
    try {
      const result = await simulateBattle(toSelection(one, left.draft), toSelection(two, right.draft), token, signal);
      if (!signal.aborted) setBattle({ key: battleKey, result });
    } catch (error) {
      if (signal.aborted) return;
      if (error instanceof ApiError && error.status === 401) logout();
      setBattle({ key: battleKey, error: error instanceof ApiError && error.status < 500 ? error.message : "Could not simulate the battle." });
    } finally {
      battleRequest.current = null;
      if (!signal.aborted) setPending(false);
    }
  }

  const activeBattle = battle?.key === battleKey ? battle : undefined;
  const result = activeBattle && "result" in activeBattle ? activeBattle.result : undefined;
  const groups = [
    { title: "My saved teams", choices: own },
    { title: "Session draft", choices: local },
    ...(pickerSide === "two" ? [{ title: "Demo teams", choices: demo }] : []),
  ];

  return <>
    {match.map(({ side, choice, draft }) => {
      let edit: string | undefined;
      if (choice?.key === "draft") edit = "/teams";
      else if (choice && "team" in choice && catalogue.own.some(team => team.id === choice.team.id)) {
        edit = `/teams?savedTeam=${choice.team.id}`;
      }
      return <TeamPanel key={side} side={side} name={draft?.name || choice?.name} draft={draft} pokedex={catalogue.pokedex}
        edit={edit} outcome={panelOutcome(result, side)}
        onChoose={!pending && options[side].length ? side => { setPickerSide(side); picker.current?.showModal(); } : undefined} />;
    })}
    <div className="simulator-arena__battle">
      <BattleButton onBattle={ready && !pending ? startBattle : undefined} />
      {!options.one.length && <Link to="/teams">Create or load a team</Link>}
      {hint && <p className="simulator-arena__status">{hint}</p>}
      {!storageAvailable && (one?.key === "draft" || two?.key === "draft") && <p className="simulator-arena__status">
        The temporary draft is in memory because browser storage is unavailable.
      </p>}
      {loadError && "error" in loadError && <p className="simulator-arena__error" role="alert">{loadError.error}{" "}
        <button type="button" onClick={() => {
          for (const id of [firstId, secondId]) {
            if (id === undefined) continue;
            const detail = completed.current.get(id);
            if (detail && "error" in detail) completed.current.delete(id);
          }
          setDetails(new Map(completed.current));
          setRetry(value => value + 1);
        }}>Retry</button>
      </p>}
      {one && two && !ready && !hint && !loadError && <p role="status">Loading Pokémon…</p>}
      {pending && <p role="status">Simulating battle…</p>}
      {result && <p className="simulator-arena__status" role="status">{result.summary}</p>}
      {activeBattle && "error" in activeBattle && <p className="simulator-arena__error" role="alert">{activeBattle.error}</p>}
    </div>
    <dialog ref={picker} className="simulator-picker" aria-labelledby="simulator-picker-title">
      <h2 id="simulator-picker-title">Choose team for {sideLabels[pickerSide]}</h2>
      {groups.map(group => group.choices.length > 0 && <section key={group.title} className="simulator-picker__group">
        <h3>{group.title}</h3>
        <ul>
          {group.choices.map(choice => {
            const current = choice.key === selected[pickerSide]?.key;
            const opposite = choice.key === selected[otherSide[pickerSide]]?.key;
            const swappable = canSwap(choice, pickerSide);
            return <li key={choice.key}><button type="button" disabled={current || !swappable} onClick={() => choose(choice)}>
              <strong>{choice.name}</strong>
              {current && <span>Currently selected</span>}
              {opposite && <span>{swappable
                ? (selected[pickerSide] ? "Swap sides" : "Move to this side") : "Cannot swap the current teams"}</span>}
            </button></li>;
          })}
        </ul>
      </section>)}
      <form method="dialog"><button className="simulator-picker__cancel" type="submit">Cancel</button></form>
    </dialog>
  </>;
}

export default function Simulator() {
  const { session, accessToken, logout, retry } = useAuth();
  const draftRevision = useSessionTeamDraftRevision();
  const { pathname, search } = useLocation();
  return <div className="simulator-page">
    <Header />
    <main className="simulator-page__main">
      <div className="simulator-arena">
        <h1>Battle <span>Simulator</span></h1>
        {session.status === "authenticated" && accessToken
          ? <SavedArena key={`${session.profile.id}:${draftRevision}`} token={accessToken} username={session.profile.username} />
          : <EmptyArena>
            {session.status === "guest" && <Link to="/login" state={{ from: pathname + search }}>Log in to battle</Link>}
            {session.status === "checking" && <p role="status">Checking your session…</p>}
            {session.status === "error" && <div className="simulator-arena__error">
              <p role="alert">Could not verify your session.</p>
              <button type="button" onClick={retry}>Retry</button>{" · "}
              <button type="button" onClick={logout}>Use another account</button>
            </div>}
          </EmptyArena>}
      </div>
    </main>
    <Footer />
  </div>;
}
