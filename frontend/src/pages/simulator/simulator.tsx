import { useEffect, useRef, useState } from "react";
import { Link, useSearchParams } from "react-router";
import simulatorIcon from "../../assets/brand/sections/simulator_icon.png";
import Header from "../../components/layout/header";
import Footer from "../../components/layout/footer";
import { getPokedexEntries } from "../../services/pokedexService";
import type { PokedexEntry } from "../../types/pokedexTypes";
import { isExampleTeamId, readTeamDraft } from "../../utils/localTeamDrafts";
import type { Draft, LocalTeamId } from "../../utils/localTeamDrafts";
import "./simulator.css";

type Side = "one" | "two";
type Team = { id: LocalTeamId; draft: Draft };
type Catalogue =
  | { status: "loading" | "error" }
  | { status: "ready"; entries: Map<number, PokedexEntry> };

const sideLabels: Record<Side, string> = { one: "P1 (Player)", two: "P2 (AI)" };

function teamName({ draft }: Team) {
  return draft.name.trim() ? draft.name : "Unnamed team";
}

function TeamPanel({ team, side, entries, simulatorSearch, onChoose }: {
  team: Team;
  side: Side;
  entries?: Map<number, PokedexEntry>;
  simulatorSearch: string;
  onChoose: (side: Side) => void;
}) {
  const name = teamName(team);
  const sideLabel = sideLabels[side];
  return (
    <section className={`simulator-team simulator-team--${side}`} aria-label={sideLabel}>
      <button
        className="simulator-team__roster"
        type="button"
        aria-label={`Choose team for ${sideLabel}: ${name}`}
        aria-haspopup="dialog"
        onClick={() => onChoose(side)}
      >
        {team.draft.members.map((member, index) => {
          const pokemon = member ? entries?.get(member.pokedexId) : undefined;
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
      <Link
        className="simulator-team__edit"
        to={`/teams?team=${team.id}`}
        state={{ simulatorSearch }}
        aria-label={`Edit this team: ${name}`}
      >
        Edit this team...
      </Link>
    </section>
  );
}

export default function Simulator() {
  const [params, setParams] = useSearchParams();
  const [teams] = useState<[Team, Team, Team]>(() => [
    { id: "vanguard", draft: readTeamDraft("vanguard") },
    { id: "rivals", draft: readTeamDraft("rivals") },
    { id: "draft", draft: readTeamDraft() },
  ]);
  const [catalogue, setCatalogue] = useState<Catalogue>({ status: "loading" });
  const [attempt, setAttempt] = useState(0);
  const [pickerSide, setPickerSide] = useState<Side>("one");
  const picker = useRef<HTMLDialogElement>(null);

  useEffect(() => {
    const controller = new AbortController();
    getPokedexEntries(controller.signal).then(entries => {
      if (!controller.signal.aborted) setCatalogue({ status: "ready", entries: new Map(entries.map(entry => [entry.id, entry])) });
    }).catch(() => {
      if (!controller.signal.aborted) setCatalogue({ status: "error" });
    });
    return () => controller.abort();
  }, [attempt]);

  function findTeam(id: string | null) {
    return id === "draft" || isExampleTeamId(id) ? teams.find(team => team.id === id) : undefined;
  }

  const [vanguard, rivals, localTeam] = teams;
  const hasLocalTeam = Boolean(localTeam.draft.name.trim() || localTeam.draft.members.some(Boolean));
  const requestedTwo = findTeam(params.get("two"));
  const defaultExample = requestedTwo === vanguard ? rivals : vanguard;
  const defaultOne = hasLocalTeam && requestedTwo !== localTeam ? localTeam : defaultExample;
  const one = findTeam(params.get("one")) ?? defaultOne;
  const two = requestedTwo && requestedTwo !== one ? requestedTwo : one === rivals ? vanguard : rivals;
  const selected = { one, two };
  const entries = catalogue.status === "ready" ? catalogue.entries : undefined;

  function openPicker(side: Side) {
    setPickerSide(side);
    picker.current?.showModal();
  }

  function chooseTeam(team: Team) {
    const otherSide = pickerSide === "one" ? "two" : "one";
    const otherTeam = selected[otherSide];
    const next = new URLSearchParams(params);
    next.set(pickerSide, team.id);
    next.set(otherSide, team === otherTeam ? selected[pickerSide].id : otherTeam.id);
    picker.current?.close();
    setParams(next, { replace: true, preventScrollReset: true });
  }

  function retryCatalogue() {
    setCatalogue({ status: "loading" });
    setAttempt(value => value + 1);
  }

  const matchup = new URLSearchParams({ one: one.id, two: two.id });
  const panelProps = { entries, simulatorSearch: matchup.toString(), onChoose: openPicker };
  return (
    <div className="simulator-page">
      <Header />
      <main className="simulator-page__main">
        <div className="simulator-arena">
          <h1>Battle <span>Simulator</span></h1>
          <TeamPanel {...panelProps} side="one" team={one} />
          <TeamPanel {...panelProps} side="two" team={two} />
          <div className="simulator-arena__battle">
            <button
              className="simulator-arena__start"
              type="button"
              aria-label="Start battle"
              aria-describedby="simulator-battle-requirement"
              disabled
            >
              <img src={simulatorIcon} alt="" width={260} height={260} />
            </button>
            <p className="simulator-arena__status" id="simulator-battle-requirement">Sign in to battle.</p>
            {catalogue.status === "loading" && <p className="simulator-arena__status" role="status">Loading Pokémon…</p>}
            {catalogue.status === "error" && <p className="simulator-arena__error" role="alert">
              Could not load the Pokédex. <button type="button" onClick={retryCatalogue}>Retry</button>
            </p>}
          </div>
        </div>
      </main>
      <Footer />

      <dialog ref={picker} className="simulator-picker" aria-labelledby="simulator-picker-title">
        <h2 id="simulator-picker-title">Choose team for {sideLabels[pickerSide]}</h2>
        <ul>
          {teams.map(team => {
            const current = team === selected[pickerSide];
            const other = team === selected[pickerSide === "one" ? "two" : "one"];
            return <li key={team.id}>
              <button type="button" disabled={current} onClick={() => chooseTeam(team)}>
                <strong>{teamName(team)}</strong>
                <span>{team.draft.members.filter(Boolean).length} Pokémon · {team.id === "draft" ? "Local draft" : "Example team"}</span>
                {current && <span>Currently selected</span>}
                {other && <span>Swap sides</span>}
              </button>
            </li>;
          })}
        </ul>
        <form method="dialog"><button className="simulator-picker__cancel" type="submit">Cancel</button></form>
      </dialog>
    </div>
  );
}
