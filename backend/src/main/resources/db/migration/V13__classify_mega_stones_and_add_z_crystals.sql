UPDATE "pokemon_items"
SET "type" = 'MEGASTONE'
WHERE "name" IN (
    -- Generation 6
    'Gengarite', 'Gardevoirite', 'Ampharosite', 'Venusaurite', 'Charizardite X',
    'Blastoisinite', 'Mewtonite X', 'Mewtonite Y', 'Blazikenite', 'Medichamite',
    'Houndoominite', 'Aggronite', 'Banettite', 'Tyranitarite', 'Scizorite',
    'Pinsirite', 'Aerodactylite', 'Lucarionite', 'Abomasite', 'Kangaskhanite',
    'Gyaradosite', 'Absolite', 'Charizardite Y', 'Alakazite', 'Heracronite',
    'Mawilite', 'Manectite', 'Garchompite', 'Latiasite', 'Latiosite',
    'Swampertite', 'Sceptilite', 'Sablenite', 'Altarianite', 'Galladite',
    'Audinite', 'Metagrossite', 'Sharpedonite', 'Slowbronite', 'Steelixite',
    'Pidgeotite', 'Glalitite', 'Diancite', 'Cameruptite', 'Lopunite',
    'Salamencite', 'Beedrillite',
    
    -- Generation 9
    'Clefableite', 'Victreebelite', 'Starminite', 'Dragoninite', 'Meganiumite',
    'Feraligite', 'Skarmorite', 'Frosslasite', 'Heatranite', 'Darkranite',
    'Emboarite', 'Excadrite', 'Scolipite', 'Scraftinite', 'Eelektrossite',
    'Chandelurite', 'Chesnaughtite', 'Delphoxite', 'Greninjite', 'Pyroarite',
    'Floettite', 'Malamarite', 'Barbaracite', 'Dragalgite', 'Haluchanite',
    'Zygardite', 'Drampanite', 'Zeraorite', 'Falinksite', 'Raichunite X',
    'Raichunite Y', 'Chimechite', 'Absolite Z', 'Staraptite', 'Garchompite Z',
    'Lucarionite Z', 'Golurkite', 'Meowsticite', 'Crabominite', 'Golisopite',
    'Magearnite', 'Scovillainite', 'Baxcalibrite', 'Tatsugirinite', 'Glimmmoranite'
);

INSERT INTO "pokemon_items" ("name", "description", "type", "generation_id")
SELECT crystal.name, crystal.description, 'Z_CRYSTAL', 7
FROM (VALUES
    -- Type-specific Z-Crystals
    ('Buginium Z', 'Turns a damaging Bug-type move into Savage Spin-Out.'),
    ('Darkinium Z', 'Turns a damaging Dark-type move into Black Hole Eclipse.'),
    ('Dragonium Z', 'Turns a damaging Dragon-type move into Devastating Drake.'),
    ('Electrium Z', 'Turns a damaging Electric-type move into Gigavolt Havoc.'),
    ('Fairium Z', 'Turns a damaging Fairy-type move into Twinkle Tackle.'),
    ('Fightinium Z', 'Turns a damaging Fighting-type move into All-Out Pummeling.'),
    ('Firium Z', 'Turns a damaging Fire-type move into Inferno Overdrive.'),
    ('Flyinium Z', 'Turns a damaging Flying-type move into Supersonic Skystrike.'),
    ('Ghostium Z', 'Turns a damaging Ghost-type move into Never-Ending Nightmare.'),
    ('Grassium Z', 'Turns a damaging Grass-type move into Bloom Doom.'),
    ('Groundium Z', 'Turns a damaging Ground-type move into Tectonic Rage.'),
    ('Icium Z', 'Turns a damaging Ice-type move into Subzero Slammer.'),
    ('Normalium Z', 'Turns a damaging Normal-type move into Breakneck Blitz.'),
    ('Poisonium Z', 'Turns a damaging Poison-type move into Acid Downpour.'),
    ('Psychium Z', 'Turns a damaging Psychic-type move into Shattered Psyche.'),
    ('Rockium Z', 'Turns a damaging Rock-type move into Continental Crush.'),
    ('Steelium Z', 'Turns a damaging Steel-type move into Corkscrew Crash.'),
    ('Waterium Z', 'Turns a damaging Water-type move into Hydro Vortex.'),

    -- Species-specific Z-Crystals
    ('Aloraichium Z', 'Allows Alolan Raichu to turn Thunderbolt into Stoked Sparksurfer.'),
    ('Decidium Z', 'Allows Decidueye to turn Spirit Shackle into Sinister Arrow Raid.'),
    ('Eevium Z', 'Allows Eevee to turn Last Resort into Extreme Evoboost.'),
    ('Incinium Z', 'Allows Incineroar to turn Darkest Lariat into Malicious Moonsault.'),
    ('Kommonium Z', 'Allows Kommo-o to turn Clanging Scales into Clangorous Soulblaze.'),
    ('Lunalium Z', 'Allows Lunala or Dawn Wings Necrozma to turn Moongeist Beam into Menacing Moonraze Maelstrom.'),
    ('Lycanium Z', 'Allows Lycanroc to turn Stone Edge into Splintered Stormshards.'),
    ('Marshadium Z', 'Allows Marshadow to turn Spectral Thief into Soul-Stealing 7-Star Strike.'),
    ('Mewnium Z', 'Allows Mew to turn Psychic into Genesis Supernova.'),
    ('Mimikium Z', 'Allows Mimikyu to turn Play Rough into Let''s Snuggle Forever.'),
    ('Pikanium Z', 'Allows Pikachu without a cap to turn Volt Tackle into Catastropika.'),
    ('Pikashunium Z', 'Allows Pikachu in a cap to turn Thunderbolt into 10,000,000 Volt Thunderbolt.'),
    ('Primarium Z', 'Allows Primarina to turn Sparkling Aria into Oceanic Operetta.'),
    ('Snorlium Z', 'Allows Snorlax to turn Giga Impact into Pulverizing Pancake.'),
    ('Solganium Z', 'Allows Solgaleo or Dusk Mane Necrozma to turn Sunsteel Strike into Searing Sunraze Smash.'),
    ('Tapunium Z', 'Allows Tapu Koko, Tapu Lele, Tapu Bulu or Tapu Fini to turn Nature''s Madness into Guardian of Alola.'),
    ('Ultranecrozium Z', 'Allows Ultra Necrozma to turn Photon Geyser into Light That Burns the Sky.')
) AS crystal(name, description);
