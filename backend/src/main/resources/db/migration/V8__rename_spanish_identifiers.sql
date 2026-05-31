UPDATE "pokedex_items"
SET "item_relationship" = 'Z_CRYSTAL'
WHERE "item_relationship" = 'Z_CRISTAL';

ALTER TABLE "game_possesions" RENAME TO "game_possessions";

UPDATE "abilities"
SET "description" = REPLACE("description", 'Poké Ball', 'Poke Ball')
WHERE "description" LIKE '%Poké Ball%';

UPDATE "pokedex"
SET "description" = REPLACE("description", 'Pokémon', 'Pokemon')
WHERE "description" LIKE '%Pokémon%';
