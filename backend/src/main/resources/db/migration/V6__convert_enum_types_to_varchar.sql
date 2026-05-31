-- pokemon.gender
ALTER TABLE "pokemon"
    ALTER COLUMN "gender" TYPE VARCHAR(32) USING "gender"::TEXT;

-- pokedex.class
ALTER TABLE "pokedex"
    ALTER COLUMN "class" TYPE VARCHAR(32) USING "class"::TEXT;

-- moves.class
ALTER TABLE "moves"
    ALTER COLUMN "class" TYPE VARCHAR(32) USING "class"::TEXT;

-- natures.rise
ALTER TABLE "natures"
    ALTER COLUMN "rise" TYPE VARCHAR(32) USING "rise"::TEXT;

-- natures.lower
ALTER TABLE "natures"
    ALTER COLUMN "lower" TYPE VARCHAR(32) USING "lower"::TEXT;

-- pokemon_items.type
ALTER TABLE "pokemon_items"
    ALTER COLUMN "type" TYPE VARCHAR(32) USING "type"::TEXT;

-- teams.modality
ALTER TABLE "teams"
    ALTER COLUMN "modality" TYPE VARCHAR(32) USING "modality"::TEXT;

-- trainers.trainer_class
ALTER TABLE "trainers"
    ALTER COLUMN "trainer_class" TYPE VARCHAR(64) USING "trainer_class"::TEXT;

-- moves_pokedex.learning_method
ALTER TABLE "moves_pokedex"
    ALTER COLUMN "learning_method" TYPE VARCHAR(32) USING "learning_method"::TEXT;

-- pokedex_items.item_relationship
ALTER TABLE "pokedex_items"
    ALTER COLUMN "item_relationship" TYPE VARCHAR(32) USING "item_relationship"::TEXT;

DROP TYPE IF EXISTS "gender_t" CASCADE;
DROP TYPE IF EXISTS "pokemon_class_t" CASCADE;
DROP TYPE IF EXISTS "move_class_t" CASCADE;
DROP TYPE IF EXISTS "nature_rise_lower_t" CASCADE;
DROP TYPE IF EXISTS "type_item_t" CASCADE;
DROP TYPE IF EXISTS "team_modality_t" CASCADE;
DROP TYPE IF EXISTS "trainer_class_t" CASCADE;
DROP TYPE IF EXISTS "learning_method_t" CASCADE;
DROP TYPE IF EXISTS "item_relationship_t" CASCADE;

ALTER TABLE "pokedex" ALTER COLUMN "class" SET DEFAULT 'COMMON';
ALTER TABLE "teams" ALTER COLUMN "modality" SET DEFAULT 'NORMAL';
ALTER TABLE "trainers" ALTER COLUMN "trainer_class" SET DEFAULT 'NOVICE';
ALTER TABLE "moves_pokedex" ALTER COLUMN "learning_method" SET DEFAULT 'LEVEL';
ALTER TABLE "pokemon" ALTER COLUMN "shiny" SET DEFAULT false;