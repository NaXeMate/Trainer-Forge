import { gameCatalogue, teamNatures } from "../../constants/teamCatalogue";
import type { PokedexEntry } from "../../types/pokedexTypes";
import { incompleteSlot, readTeamFile, type Draft } from "../../utils/teamDraft";

export type PresetTeam = { key: `preset:${string}`; name: string; draft: Draft };

const files = import.meta.glob<unknown>("./preset-teams/*.json", { eager: true, import: "default" });

export function loadPresetTeams(pokedex: Map<number, PokedexEntry>): PresetTeam[] {
  return Object.entries(files).flatMap(([path, data]): PresetTeam[] => {
    const filename = path.slice("./preset-teams/".length);
    try {
      const { draft } = readTeamFile(data, { pokedex, games: gameCatalogue, natures: teamNatures });
      if (!draft.name.trim() || !draft.members.some(Boolean) || incompleteSlot(draft) !== -1) {
        throw new Error("Preset teams need a name and at least one Pokémon, with an ability and a first move in each occupied slot.");
      }
      return [{ key: `preset:${filename.slice(0, -".json".length)}`, name: draft.name, draft }];
    } catch (error) {
      console.error(`Could not load preset team ${filename}.`, error);
      return [];
    }
  });
}
