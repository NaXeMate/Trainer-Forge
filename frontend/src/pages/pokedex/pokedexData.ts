import chikorita from "../../assets/images/pokedex/chikorita.png";
import bayleef from "../../assets/images/pokedex/bayleef.png";
import meganium from "../../assets/images/pokedex/meganium.png";
import tepig from "../../assets/images/pokedex/tepig.png";
import pignite from "../../assets/images/pokedex/pignite.png";
import emboar from "../../assets/images/pokedex/emboar.png";
import totodile from "../../assets/images/pokedex/totodile.png";
import croconaw from "../../assets/images/pokedex/croconaw.png";
import feraligatr from "../../assets/images/pokedex/feraligatr.png";
import fletchling from "../../assets/images/pokedex/fletchling.png";
import fletchinder from "../../assets/images/pokedex/fletchinder.png";
import talonflame from "../../assets/images/pokedex/talonflame.png";

// Presentation data for the general-page mockup, pending the PokéDex service.
export const pokedexPreview = [
  { name: "Chikorita", nationalNumber: 152, image: chikorita, surface: "grass" },
  { name: "Bayleef", nationalNumber: 153, image: bayleef, surface: "grass" },
  { name: "Meganium", nationalNumber: 154, image: meganium, surface: "grass" },
  { name: "Tepig", nationalNumber: 498, image: tepig, surface: "fire" },
  { name: "Pignite", nationalNumber: 499, image: pignite, surface: "fire-fighting" },
  { name: "Emboar", nationalNumber: 500, image: emboar, surface: "fire-fighting" },
  { name: "Totodile", nationalNumber: 158, image: totodile, surface: "water" },
  { name: "Croconaw", nationalNumber: 159, image: croconaw, surface: "water" },
  { name: "Feraligatr", nationalNumber: 160, image: feraligatr, surface: "water" },
  { name: "Fletchling", nationalNumber: 661, image: fletchling, surface: "normal-flying" },
  { name: "Fletchinder", nationalNumber: 662, image: fletchinder, surface: "fire-flying", compactName: true },
  { name: "Talonflame", nationalNumber: 663, image: talonflame, surface: "fire-flying", compactName: true },
] as const;
