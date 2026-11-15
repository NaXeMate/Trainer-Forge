import { useEffect, useLayoutEffect, useRef, useState } from "react";
import { Link, Navigate, useLocation } from "react-router";

import trainerIcon from "../../assets/brand/sections/trainer_icon.png";
import favoriteGame from "../../assets/icons/favorite-game.svg";
import favoritePokemon from "../../assets/icons/favorite-pokemon.svg";
import pokeBallSeparator from "../../assets/icons/poke-ball-separator.svg";
import { trainerSpriteCredits } from "../../utils/trainerSpriteCredits";
import ArrowButton from "../../components/common/arrowButton";
import Combobox from "../../components/common/combobox";
import PokemonPicker from "../../components/common/pokemonPicker";
import type { PickerPanel } from "../../components/common/pokemonPicker";
import Footer from "../../components/layout/footer";
import Header from "../../components/layout/header";
import { useAuth } from "../../context/useAuth";
import { ApiError } from "../../services/api";
import { getPokedexEntries } from "../../services/pokedexService";
import type { PokedexEntry } from "../../types/pokedexTypes";

import { TRAINER_CLASSES } from "../../types/trainerTypes";
import type { EditableTrainerProfile, TrainerClass, TrainerProfile, TrainerProfileChanges } from "../../types/trainerTypes";
import trainerSpriteIds from "./trainerSprites.json";

import "./trainer.css";

type Picker = "pokemon" | "game" | "sprite";

const GAME_NAMES: Record<string, string> = {
  PokemonFireRed: "Pokemon FireRed",
  PokemonLeafGreen: "Pokemon LeafGreen",
  PokemonHeartGold: "Pokemon HeartGold",
  PokemonSoulSilver: "Pokemon SoulSilver",
  PokemonLetsGoPikachu: "Pokemon Let's Go, Pikachu!",
  PokemonLetsGoEevee: "Pokemon Let's Go, Eevee!",
  PokemonLegendsArceus: "Pokemon Legends: Arceus",
  PokemonLegendsZA: "Pokemon Legends Z-A",
};

const PICKER_TITLES = {
  pokemon: "Choose a favorite Pokémon",
  game: "Choose a favorite game",
  sprite: "Choose a trainer sprite",
};

const FAVORITE_LABELS = {
  pokemon: { image: favoritePokemon, text: "Favorite Pokémon" },
  game: { image: favoriteGame, text: "Favorite Game" },
};

const ACHIEVEMENT_SLOTS = 30;
const ACHIEVEMENTS_PER_PAGE = 15;
const LAST_ACHIEVEMENT_PAGE = Math.ceil(ACHIEVEMENT_SLOTS / ACHIEVEMENTS_PER_PAGE) - 1;

function assetName(path: string) {
  return path.slice(path.lastIndexOf("/") + 1, path.lastIndexOf("."));
}

function titleCase(name: string) {
  return name.replace(/([a-z])([A-Z0-9])/g, "$1 $2").replace(/[-_]/g, " ")
    .replace(/\b\w/g, letter => letter.toUpperCase());
}

const TRAINER_TITLE_OPTIONS = TRAINER_CLASSES.map(value => ({
  value,
  label: value === "CHAMPION"
    ? "Pokémon League Champion"
    : titleCase(value.toLowerCase()).replace("Pokemon", "Pokémon"),
}));

const games = Object.entries(import.meta.glob<string>("../../assets/images/gameLogos/*.png", {
  eager: true, import: "default", query: "?url",
})).flatMap(([path, image]) => {
  const id = assetName(path);
  if (id === "PokemonRedAndBlue") {
    return [
      { value: "Pokemon Red", name: "Pokémon Red", image },
      { value: "Pokemon Blue", name: "Pokémon Blue", image },
    ];
  }
  if (id === "PokemonWaves" || id === "PokemonWinds") return [];
  const value = GAME_NAMES[id] ?? titleCase(id);
  const name = value.replace(/^Pokemon /, "Pokémon ")
    .replace("Pokémon Let's Go", "Pokémon: Let's Go")
    .replace("Legends Z-A", "Legends: Z-A");
  return [{ value, name, image }];
}).sort((a, b) => a.name.localeCompare(b.name));

const sprites = trainerSpriteIds.map(id => ({
  value: `/trainer-sprites/${id}.png`,
  name: titleCase(id),
  image: `${import.meta.env.BASE_URL}trainer-sprites/${id}.png`,
  artist: trainerSpriteCredits[id],
})).sort((a, b) => a.name.localeCompare(b.name));

function searchKey(name: string) {
  return name.normalize("NFD").toLowerCase().replace(/[^\p{L}\p{N}]/gu, "");
}

function getProfileChanges(draft: EditableTrainerProfile, profile: TrainerProfile): TrainerProfileChanges {
  const changes: TrainerProfileChanges = {};
  const realName = draft.realName?.trim() || null;
  
  if (draft.realName !== profile.realName && realName !== profile.realName) changes.realName = realName;
  if (draft.trainerClass !== profile.trainerClass) changes.trainerClass = draft.trainerClass;
  if (draft.profilePictureUrl !== profile.profilePictureUrl) changes.profilePictureUrl = draft.profilePictureUrl;
  if (draft.favoriteGameName !== profile.favoriteGameName) changes.favoriteGameName = draft.favoriteGameName;
  if (draft.favoritePokemonId !== profile.favoritePokemonId) changes.favoritePokemonId = draft.favoritePokemonId;
  return changes;
}

function trimSprite(image: HTMLImageElement) {
  const { naturalWidth: width, naturalHeight: height } = image;
  
  if (!width || !height) return;

  const canvas = document.createElement("canvas");
  canvas.width = width;
  canvas.height = height;
  const context = canvas.getContext("2d");
  
  if (!context) return;
  context.drawImage(image, 0, 0);
  
  const { data } = context.getImageData(0, 0, width, height);
  let left = width;
  let top = height;
  let right = -1;
  let bottom = -1;

  for (let y = 0; y < height; y++) {
    for (let x = 0; x < width; x++) {
      if (data[(y * width + x) * 4 + 3] === 0) continue;
      left = Math.min(left, x);
      top = Math.min(top, y);
      right = Math.max(right, x);
      bottom = Math.max(bottom, y);
    }
  }

  if (right < left || bottom < top) return;

  canvas.width = right - left + 1;
  canvas.height = bottom - top + 1;
  context.drawImage(image, left, top, canvas.width, canvas.height, 0, 0, canvas.width, canvas.height);
  return canvas.toDataURL();
}

function TrainerSprite({ image, name }: { image: string; name?: string }) {
  const [trimmed, setTrimmed] = useState<string>();

  return (
    <picture>
      {trimmed && <source media="(width > 40rem)" srcSet={trimmed} />}
      <img src={image} alt={name ?? ""}
        className={trimmed ? "trainer-card__fitted-sprite" : undefined}
        onLoad={event => {
          const sprite = event.currentTarget;
          
          if (!trimmed && new URL(sprite.currentSrc).origin === window.location.origin) {
            setTrimmed(trimSprite(sprite));
          }
        }} />
    </picture>
  );
}

function ProfileArtwork({ kind, image, name, onChoose }: {
  kind: Picker;
  image?: string;
  name?: string;
  onChoose?: (kind: Picker, trigger: HTMLButtonElement) => void;
}) {
  const className = `trainer-card__choice trainer-card__choice--${kind}`;
  const favoriteLabel = kind === "sprite" ? null : FAVORITE_LABELS[kind];
  const artwork = image && (favoriteLabel ? (
    <span className="trainer-card__favorite-artwork">
      <img className="trainer-card__artwork-image" src={image} alt={onChoose ? "" : name ?? ""} />
      <img className="trainer-card__favorite-label" src={favoriteLabel.image}
        alt={onChoose ? "" : favoriteLabel.text} width={196} height={48} />
    </span>
  ) : (
    onChoose ? <img src={image} alt="" /> : <TrainerSprite key={image} image={image} name={name} />
  ));

  if (!onChoose) {
    return <div className={className}>{artwork || name}</div>;
  }

  const label = PICKER_TITLES[kind];
  return (
    <button className={className} type="button" aria-haspopup="dialog"
      aria-label={name ? `${label}: ${name}` : undefined}
      onClick={event => onChoose(kind, event.currentTarget)}>
      {artwork || <span>{name || label}</span>}
    </button>
  );
}

export default function Trainer() {
  const { session, retry, updateProfile } = useAuth();
  const { pathname, search, hash } = useLocation();
  const returnState = { from: pathname + search + hash };

  if (session.status === "guest") {
    return <Navigate to="/login" state={returnState} replace />;
  }

  if (session.status === "authenticated") {
    return <TrainerCard key={session.profile.id} profile={session.profile} updateProfile={updateProfile} />;
  }

  return (
    <div className="trainer-page">
      <Header />
      <main className="trainer-page__main">
        {session.status === "checking" ? <p role="status">Checking your session…</p> : (
          <div className="trainer-page__session-error">
            <p role="alert">Could not load your profile.</p>
            <button type="button" onClick={retry}>Retry</button>{" · "}
            <Link to="/login" state={returnState}>Account options</Link>
          </div>
        )}
      </main>
      <Footer />
    </div>
  );
}

function TrainerCard({ profile, updateProfile }: {
  profile: TrainerProfile;
  updateProfile: (changes: TrainerProfileChanges) => Promise<void>;
}) {
  const [draft, setDraft] = useState<EditableTrainerProfile | null>(null);
  const [saving, setSaving] = useState(false);
  const [saveError, setSaveError] = useState("");
  const savingRequest = useRef(false);
  const [picker, setPicker] = useState<Picker | null>(null);
  const [query, setQuery] = useState("");
  const [panel, setPanel] = useState<PickerPanel | null>(null);
  const [entries, setEntries] = useState<PokedexEntry[]>([]);
  const [pokemonStatus, setPokemonStatus] = useState<"loading" | "success" | "error">("loading");
  const [pokemonAttempt, setPokemonAttempt] = useState(profile.favoritePokemonId === null ? 0 : 1);
  const [achievementPage, setAchievementPage] = useState(0);
  const dialogRef = useRef<HTMLDialogElement>(null);
  const triggerRef = useRef<HTMLButtonElement | null>(null);
  const editButtonRef = useRef<HTMLButtonElement>(null);
  const identityRef = useRef<HTMLDivElement>(null);
  const summaryRef = useRef<HTMLDivElement>(null);

  const displayedProfile = draft ?? profile;
  const selectedGame = games.find(game => game.value === displayedProfile.favoriteGameName);
  const selectedSprite = sprites.find(sprite => sprite.value === displayedProfile.profilePictureUrl);
  const selectedPokemon = entries.find(entry => entry.id === displayedProfile.favoritePokemonId);
  const pokemonName = selectedPokemon?.name ?? (
    displayedProfile.favoritePokemonId === profile.favoritePokemonId ? profile.favoritePokemonName : null
  );
  const pickerValue = picker === "game" ? displayedProfile.favoriteGameName : displayedProfile.profilePictureUrl;
  const trainerTitle = TRAINER_TITLE_OPTIONS.find(option => option.value === displayedProfile.trainerClass)?.label ?? "";
  const savedTrainerTitle = TRAINER_TITLE_OPTIONS.find(option => option.value === profile.trainerClass)?.label;
  const chooseArtwork = draft && !saving ? openPicker : undefined;
  const changes = draft ? getProfileChanges(draft, profile) : {};
  const hasChanges = Object.keys(changes).length > 0;
  const imageQuery = searchKey(query);
  const imageMatches = picker === "game" || picker === "sprite"
    ? (picker === "game" ? games : sprites).filter(option => searchKey(option.name).includes(imageQuery))
    : [];

  useLayoutEffect(() => {
    const identity = identityRef.current;
    const summary = summaryRef.current;
    const username = summary?.firstElementChild;
    const lastLine = summary?.lastElementChild;
    if (!identity || !summary || !username || !lastLine) return;

    const fitSprite = () => {
      const topInset = Number.parseFloat(getComputedStyle(username).fontSize) * 0.22;
      const bottomInset = Number.parseFloat(getComputedStyle(lastLine).fontSize) * 0.08;
      const height = summary.getBoundingClientRect().height - topInset - bottomInset;
      identity.style.setProperty("--trainer-sprite-top", `${topInset}px`);
      identity.style.setProperty("--trainer-sprite-height", height > 0 ? `${height}px` : "12rem");
    };

    fitSprite();
    const observer = new ResizeObserver(fitSprite);
    observer.observe(summary);
    return () => observer.disconnect();
  }, [profile.username, profile.realName, savedTrainerTitle]);

  useEffect(() => {
    if (pokemonAttempt === 0) return;

    const controller = new AbortController();
    
    getPokedexEntries(controller.signal).then(pokemon => {
      if (!controller.signal.aborted) {
        setEntries(pokemon);
        setPokemonStatus("success");
      }
    }).catch(() => {
      if (!controller.signal.aborted) setPokemonStatus("error");
    });
    return () => controller.abort();
  }, [pokemonAttempt]);

  useEffect(() => {
    const dialog = dialogRef.current;
    
    if (picker && dialog && !dialog.open) {
      dialog.showModal();
      dialog.querySelector("input")?.focus();
    }
  }, [picker]);

  function openPicker(next: Picker, trigger: HTMLButtonElement) {
    triggerRef.current = trigger;
    setQuery("");
    setPanel(null);
    setPicker(next);
    if (next === "pokemon") setPokemonAttempt(previous => Math.max(1, previous));
  }

  function closePicker() {
    dialogRef.current?.close();
  }

  function updateDraft(changes: TrainerProfileChanges) {
    if (savingRequest.current) return;
    setDraft(previous => previous && { ...previous, ...changes });
  }

  function finishEditing() {
    setDraft(null);
    setSaveError("");
    requestAnimationFrame(() => editButtonRef.current?.focus());
  }

  async function saveProfile() {
    if (!hasChanges || savingRequest.current) return;
    
    savingRequest.current = true;
    setSaving(true);
    setSaveError("");
    
    try {
      await updateProfile(changes);
      finishEditing();
    } catch (error) {
      setSaveError(error instanceof ApiError && error.status < 500
        ? error.message : "Could not save your profile. Please try again.");
    } finally {
      savingRequest.current = false;
      setSaving(false);
    }
  }

  return (
    <div className="trainer-page">
      <Header />
      <main className="trainer-page__main" aria-labelledby="trainer-title">
        <div className="trainer-page__heading">
          <img src={trainerIcon} alt="" width={78} height={78} />
          <h1 id="trainer-title">Trainer <span>Card</span></h1>
        </div>

        <section className="trainer-card" aria-label="Trainer profile">
          {!draft && (
            <button ref={editButtonRef} className="trainer-card__edit" type="button"
              aria-label="Edit trainer profile" onClick={() => setDraft(profile)}>
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2"
                strokeLinecap="round" strokeLinejoin="round" aria-hidden="true">
                <path d="m15 5 4 4M4 20l4.5-1 11-11a2.8 2.8 0 0 0-4-4l-11 11L4 20Z" />
              </svg>
            </button>
          )}
          <div ref={identityRef} className="trainer-card__identity">
            <ProfileArtwork kind="sprite" name={selectedSprite?.name} image={selectedSprite?.image ?? displayedProfile.profilePictureUrl ?? undefined} onChoose={chooseArtwork} />

            <div className="trainer-card__summary" aria-hidden={draft ? true : undefined}>
              <div ref={summaryRef} className="trainer-card__summary-content">
                <h2>{profile.username || "Trainer profile"}</h2>
                {profile.realName && <p className="trainer-card__real-name">{profile.realName}</p>}
                {savedTrainerTitle && <p className="trainer-card__title">{savedTrainerTitle}</p>}
              </div>
              {!draft && (
                <div className="trainer-card__separator" aria-hidden="true">
                  <img src={pokeBallSeparator} alt="" width={24} height={24} />
                </div>
              )}
            </div>

            {draft && (
              <div className="trainer-card__fields">
                <p className="trainer-card__username">{profile.username}</p>
                <label>
                  Real name
                  <input value={draft.realName ?? ""} maxLength={50} placeholder="Real name" autoFocus disabled={saving}
                    onChange={event => updateDraft({ realName: event.target.value })} />
                </label>
                <div className="trainer-card__title-field">
                  <label htmlFor="trainer-class">Trainer title</label>
                  <Combobox
                    inputId="trainer-class"
                    label="Trainer title"
                    placeholder="Choose a trainer title"
                    value={trainerTitle}
                    options={TRAINER_TITLE_OPTIONS}
                    readOnly={saving}
                    onChange={trainerClass => updateDraft({ trainerClass: (trainerClass || "NOVICE") as TrainerClass })}
                  />
                </div>
              </div>
            )}
          </div>

          <div className="trainer-card__details">
            <div className="trainer-card__favorites">
              <ProfileArtwork kind="pokemon" name={pokemonName ?? undefined}
                image={selectedPokemon?.imageUrl} onChoose={chooseArtwork} />
              <ProfileArtwork kind="game" name={selectedGame?.name ?? displayedProfile.favoriteGameName ?? undefined} image={selectedGame?.image} onChoose={chooseArtwork} />
            </div>

            <section className="trainer-achievements" aria-labelledby="trainer-achievements-title">
              <h2 id="trainer-achievements-title">Achievements</h2>
              <div className="trainer-achievements__body">
                <ArrowButton aria-label="Previous achievements" aria-controls="trainer-achievements-grid" disabled={achievementPage === 0}
                  onClick={() => setAchievementPage(page => Math.max(0, page - 1))} />
                <ul className="trainer-achievements__grid" id="trainer-achievements-grid" aria-hidden="true">
                  {Array.from({ length: ACHIEVEMENTS_PER_PAGE }, (_, index) => (
                    <li key={achievementPage * ACHIEVEMENTS_PER_PAGE + index} />
                  ))}
                </ul>
                <ArrowButton direction="right" aria-label="Next achievements" aria-controls="trainer-achievements-grid"
                  disabled={achievementPage === LAST_ACHIEVEMENT_PAGE}
                  onClick={() => setAchievementPage(page => Math.min(LAST_ACHIEVEMENT_PAGE, page + 1))} />
              </div>
            </section>
          </div>
          {draft && (
            <div className="trainer-card__actions">
              <button type="button" onClick={finishEditing} disabled={saving}>CANCEL</button>
              <button className="trainer-card__save" type="button" disabled={!hasChanges || saving} onClick={saveProfile}>
                {saving ? "SAVING…" : "SAVE"}
              </button>
            </div>
          )}
          {saveError && <p className="trainer-card__save-error" role="alert">{saveError}</p>}
        </section>
      </main>
      <Footer />

      <dialog ref={dialogRef} className="team-dialog team-dialog--picker trainer-picker" aria-labelledby="trainer-picker-title"
        onClose={() => {
          setPicker(null);
          triggerRef.current?.focus();
        }}
        onCancel={event => {
          if (!panel) return;
          event.preventDefault();
          event.currentTarget.querySelector<HTMLButtonElement>(".team-dialog__tool[aria-expanded=true]")?.focus();
          setPanel(null);
        }}
        onClick={event => {
          if (event.target !== event.currentTarget) return;
          const { top, right, bottom, left } = event.currentTarget.getBoundingClientRect();
          if (event.clientX < left || event.clientX > right || event.clientY < top || event.clientY > bottom) closePicker();
        }}>
        <div className="team-dialog__header">
          <h2 id="trainer-picker-title">{picker && PICKER_TITLES[picker]}</h2>
          <button type="button" className="team-dialog__picker-close" aria-label="Close dialog" onClick={closePicker}>
            <svg aria-hidden="true" width="16" height="16" viewBox="0 0 16 16" fill="none" stroke="currentColor" strokeWidth="1.5" strokeLinecap="round">
              <path d="m4 4 8 8M12 4l-8 8" />
            </svg>
          </button>
        </div>

        {picker && (picker === "pokemon" ? displayedProfile.favoritePokemonId !== null : pickerValue !== null) && (
          <button className="trainer-picker__clear" type="button" onClick={() => {
            if (picker === "pokemon") updateDraft({ favoritePokemonId: null });
            else if (picker === "game") updateDraft({ favoriteGameName: null });
            else updateDraft({ profilePictureUrl: null });
            closePicker();
          }}>
            {picker === "sprite" ? "Remove sprite" : "Clear favorite"}
          </button>
        )}

        <PokemonPicker active={picker === "pokemon"} entries={entries} status={pokemonStatus}
          query={query} onQueryChange={setQuery} panel={panel} onPanelChange={setPanel}
          onRetry={() => {
            setPokemonStatus("loading");
            setPokemonAttempt(previous => previous + 1);
          }}
          onSelect={({ id }) => {
            updateDraft({ favoritePokemonId: id });
            closePicker();
          }} />

        {(picker === "game" || picker === "sprite") && (
          <>
            <div className="team-dialog__search">
              <label htmlFor="trainer-image-search">{picker === "game" ? "Search games by name" : "Search trainer sprites by name"}</label>
              <input id="trainer-image-search" value={query} onChange={event => setQuery(event.target.value)}
                placeholder={picker === "game" ? "Search games…" : "Search trainer sprites…"} />
            </div>
            <p className="team-dialog__count" role="status">{imageMatches.length} {picker === "game" ? "games" : "sprites"} found</p>
            <div className="team-dialog__results team-dialog__pokemon trainer-picker__images" data-picker={picker}>
              {imageMatches.map(option => (
                <button type="button" key={option.value} aria-pressed={pickerValue === option.value}
                  onClick={() => {
                    if (picker === "game") updateDraft({ favoriteGameName: option.value });
                    else updateDraft({ profilePictureUrl: option.value });
                    closePicker();
                  }}>
                  <img className="team-dialog__artwork" src={option.image} alt="" loading="lazy" />
                  <strong>{option.name}</strong>
                  {picker === "sprite" && "artist" in option && typeof option.artist === "string" && option.artist && (
                    <small>by {option.artist}</small>
                  )}
                </button>
              ))}
            </div>
            {!imageMatches.length && <p>No results match your search.</p>}
          </>
        )}
      </dialog>
    </div>
  );
}
