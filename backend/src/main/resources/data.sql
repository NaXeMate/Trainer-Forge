CREATE TYPE "pokemon_class_t" AS ENUM ('COMMON', 'LEGENDARY', 'SINGULAR', 'ULTRABEAST', 'PARADOX');

CREATE TABLE IF NOT EXISTS "pokedex" (
	"id" bigserial NOT NULL UNIQUE,
	"national_pokedex" bigint NOT NULL,
	"name" varchar(100) NOT NULL UNIQUE,
	"image_url" varchar(255) NOT NULL,
	"generation_id" bigint NOT NULL,
	"region" bigint NOT NULL,
	"class" pokemon_class_t NOT NULL DEFAULT 'COMMON',
	"type_1" bigint NOT NULL,
	"type_2" bigint,
	"ability_1" bigint NOT NULL,
	"ability_2" bigint,
	"hidden_ability" bigint,
	"description" text(65535) NOT NULL,
	"category" varchar(100) NOT NULL,
	"weight" decimal(5,2) NOT NULL,
	"height" decimal(5,2) NOT NULL,
	"hp_base" int NOT NULL,
	"attack_base" int NOT NULL,
	"defense_base" int NOT NULL,
	"special_attack_base" int NOT NULL,
	"special_defense_base" int NOT NULL,
	"speed_base" int NOT NULL,
	PRIMARY KEY("id")
);


CREATE TYPE "gender_t" AS ENUM ('MALE', 'FEMALE');

CREATE TABLE IF NOT EXISTS "pokemon" (
	"id" bigserial NOT NULL UNIQUE,
	"species" bigint NOT NULL,
	"nickname" varchar(32),
	"location_found" varchar(255) NOT NULL,
	"level" int NOT NULL,
	"shiny" boolean NOT NULL DEFAULT 0,
	"gender" gender_t,
	"ability" bigint NOT NULL,
	"move_1" bigint NOT NULL,
	"move_2" bigint,
	"move_3" bigint,
	"move_4" bigint,
	"equipped_item" bigint,
	"nature" bigint NOT NULL,
	"hp_ev" int NOT NULL,
	"attack_ev" int NOT NULL,
	"defense_ev" int NOT NULL,
	"special_attack_ev" int NOT NULL,
	"special_defense_ev" int NOT NULL,
	"speed_ev" int NOT NULL,
	PRIMARY KEY("id")
);


CREATE TYPE "move_class_t" AS ENUM ('PHYSICAL', 'SPECIAL', 'STATUS');

CREATE TABLE IF NOT EXISTS "moves" (
	"id" bigserial NOT NULL UNIQUE,
	"name" varchar(100) NOT NULL UNIQUE,
	"type" bigint NOT NULL,
	"class" move_class_t NOT NULL,
	"power" int NOT NULL,
	"accuracy" int NOT NULL,
	"contact" boolean NOT NULL,
	"priority" int NOT NULL,
	"target_id" bigint NOT NULL,
	"secondary_effect_id" bigint,
	"pp" int NOT NULL,
	"generation_id" bigint NOT NULL,
	PRIMARY KEY("id")
);


CREATE TABLE IF NOT EXISTS "moves_targets" (
	"id" bigserial NOT NULL UNIQUE,
	"target" text(65535) NOT NULL,
	PRIMARY KEY("id")
);


CREATE TABLE IF NOT EXISTS "secondary_effects" (
	"id" bigserial NOT NULL UNIQUE,
	"secondary_effect" text(65535) NOT NULL,
	PRIMARY KEY("id")
);


CREATE TABLE IF NOT EXISTS "regions" (
	"id" bigserial NOT NULL UNIQUE,
	"name" varchar(255) NOT NULL UNIQUE,
	"generation_id" bigint NOT NULL,
	PRIMARY KEY("id")
);


CREATE TYPE "nature_rise_t" AS ENUM ('ATTACK', 'DEFENSE', 'SPECIAL ATTACK', 'SPECIAL DEFENSE', 'SPEED');

CREATE TYPE "nature_lower_t" AS ENUM ('ATTACK', 'DEFENSE', 'SPECIAL ATTACK', 'SPECIAL DEFENSE', 'SPEED');

CREATE TABLE IF NOT EXISTS "natures" (
	"id" bigserial NOT NULL UNIQUE,
	"nature" varchar(100) NOT NULL UNIQUE,
	"rise" nature_rise_t NOT NULL,
	"lower" nature_lower_t NOT NULL,
	PRIMARY KEY("id")
);


CREATE TABLE IF NOT EXISTS "types" (
	"id" bigserial NOT NULL UNIQUE,
	"name" varchar(32) NOT NULL UNIQUE,
	"generation_id" bigint NOT NULL,
	PRIMARY KEY("id")
);


CREATE TABLE IF NOT EXISTS "generations" (
	"id" bigserial NOT NULL UNIQUE,
	"name" varchar(32) NOT NULL,
	PRIMARY KEY("id")
);


CREATE TABLE IF NOT EXISTS "abilities" (
	"id" bigserial NOT NULL UNIQUE,
	"name" varchar(100) NOT NULL UNIQUE,
	"description" text(65535) NOT NULL,
	"generation_id" bigint NOT NULL,
	PRIMARY KEY("id")
);


CREATE TABLE IF NOT EXISTS "items" (
	"id" bigserial NOT NULL UNIQUE,
	"name" varchar(100) NOT NULL UNIQUE,
	"description" text(65535) NOT NULL,
	"type_id" bigint NOT NULL,
	"generation_id" bigint NOT NULL,
	PRIMARY KEY("id")
);


CREATE TABLE IF NOT EXISTS "item_types" (
	"id" bigserial NOT NULL UNIQUE,
	"type" varchar(55) NOT NULL,
	PRIMARY KEY("id")
);


CREATE TYPE "team_modality_t" AS ENUM ('NORMAL', 'NUZLOCKE', 'MONOTYPE');

CREATE TABLE IF NOT EXISTS "teams" (
	"id" bigserial NOT NULL UNIQUE,
	"trainer_id" bigint NOT NULL,
	"game_id" bigint NOT NULL,
	"name" varchar(100) NOT NULL,
	"modality" team_modality_t NOT NULL DEFAULT 'NORMAL',
	"hidden" boolean NOT NULL DEFAULT TRUE,
	PRIMARY KEY("id")
);


CREATE TYPE "trainer_class_t" AS ENUM ('NOVICE', 'CHAMPION', 'GYM LEADER', 'ELITE FOUR');

CREATE TABLE IF NOT EXISTS "trainers" (
	"id" bigserial NOT NULL UNIQUE,
	"username" varchar(55) NOT NULL,
	"email" varchar(100) NOT NULL,
	"password_hash" varchar(255) NOT NULL,
	"visible_name" varchar(55) UNIQUE,
	"region" bigint,
	"favorite_game_id" bigint,
	"favorite_pokemon_id" bigint,
	"best_friend_id" bigint,
	"friend_code" varchar(12) NOT NULL UNIQUE,
	"trainer_class" trainer_class_t NOT NULL DEFAULT 'NOVICE',
	PRIMARY KEY("id")
);


CREATE TABLE IF NOT EXISTS "videogames" (
	"id" bigserial NOT NULL UNIQUE,
	"name" varchar(100) NOT NULL UNIQUE,
	"generation_id" bigint NOT NULL,
	"region_id" bigint NOT NULL,
	PRIMARY KEY("id")
);


CREATE TABLE IF NOT EXISTS "game_possesion" (
	"id" bigserial NOT NULL UNIQUE,
	"trainer_id" bigint NOT NULL,
	"game_id" bigint NOT NULL,
	PRIMARY KEY("id")
);


CREATE TABLE IF NOT EXISTS "videogames_pokedex" (
	"id" bigserial NOT NULL UNIQUE,
	"pokedex_id" bigint NOT NULL,
	"videogame_id" bigint NOT NULL,
	PRIMARY KEY("id")
);


CREATE TYPE "moves_learning_method_t" AS ENUM ('LEVEL', 'MT/MO', 'EGG', 'TUTOR', 'OTHER');

CREATE TABLE IF NOT EXISTS "moves_pokedex" (
	"id" bigserial NOT NULL UNIQUE,
	"pokedex_id" bigint NOT NULL,
	"move_id" bigint NOT NULL,
	"videogame_id" bigint NOT NULL,
	"metodo" moves_learning_method_t NOT NULL DEFAULT 'LEVEL',
	"level" int,
	PRIMARY KEY("id")
);


CREATE TYPE "tipo_relacion_t" AS ENUM ('EVOLUCION', 'MEGAPIEDRA', 'CRISTAL Z', 'ASOCIADO', 'SALVAJE');

CREATE TABLE IF NOT EXISTS "pokedex_items" (
	"id" bigserial NOT NULL UNIQUE,
	"pokedex_id" bigint NOT NULL,
	"item_id" bigint NOT NULL,
	"tipo_relacion" tipo_relacion_t NOT NULL,
	PRIMARY KEY("id")
);


CREATE TABLE IF NOT EXISTS "achievements" (
	"id" bigserial NOT NULL UNIQUE,
	"name" varchar(255) NOT NULL,
	"description" text(65535) NOT NULL,
	"hidden" boolean NOT NULL,
	PRIMARY KEY("id")
);


CREATE TABLE IF NOT EXISTS "trainer_achievements" (
	"id" bigserial NOT NULL UNIQUE,
	"trainer_id" bigint NOT NULL,
	"achievement_id" bigint NOT NULL,
	"date_obtained" timestamp NOT NULL,
	PRIMARY KEY("id")
);


CREATE TABLE IF NOT EXISTS "types_effectiveness" (
	"id" bigserial NOT NULL UNIQUE,
	"attacker_id" bigint NOT NULL,
	"defender_id" bigint NOT NULL,
	"multiplier" decimal(3,2) NOT NULL,
	PRIMARY KEY("id")
);


CREATE TABLE IF NOT EXISTS "pokemon_teams" (
	"id" bigserial NOT NULL UNIQUE,
	"team_id" bigint NOT NULL,
	"pokemon_id" bigint NOT NULL,
	"position" int NOT NULL,
	PRIMARY KEY("id")
);


ALTER TABLE "items"
ADD FOREIGN KEY("type_id") REFERENCES "item_types"("id")
ON UPDATE NO ACTION ON DELETE NO ACTION;
ALTER TABLE "items"
ADD FOREIGN KEY("generation_id") REFERENCES "generations"("id")
ON UPDATE NO ACTION ON DELETE NO ACTION;
ALTER TABLE "pokemon"
ADD FOREIGN KEY("species") REFERENCES "pokedex"("national_pokedex")
ON UPDATE NO ACTION ON DELETE NO ACTION;
ALTER TABLE "pokedex"
ADD FOREIGN KEY("type_1") REFERENCES "types"("id")
ON UPDATE NO ACTION ON DELETE NO ACTION;
ALTER TABLE "pokedex"
ADD FOREIGN KEY("type_2") REFERENCES "types"("id")
ON UPDATE NO ACTION ON DELETE NO ACTION;
ALTER TABLE "pokedex"
ADD FOREIGN KEY("region") REFERENCES "regions"("id")
ON UPDATE NO ACTION ON DELETE NO ACTION;
ALTER TABLE "abilities"
ADD FOREIGN KEY("id") REFERENCES "pokemon"("ability")
ON UPDATE NO ACTION ON DELETE NO ACTION;
ALTER TABLE "abilities"
ADD FOREIGN KEY("generation_id") REFERENCES "generations"("id")
ON UPDATE NO ACTION ON DELETE NO ACTION;
ALTER TABLE "abilities"
ADD FOREIGN KEY("id") REFERENCES "pokedex"("ability_1")
ON UPDATE NO ACTION ON DELETE NO ACTION;
ALTER TABLE "abilities"
ADD FOREIGN KEY("id") REFERENCES "pokedex"("ability_2")
ON UPDATE NO ACTION ON DELETE NO ACTION;
ALTER TABLE "abilities"
ADD FOREIGN KEY("id") REFERENCES "pokedex"("hidden_ability")
ON UPDATE NO ACTION ON DELETE NO ACTION;
ALTER TABLE "pokemon"
ADD FOREIGN KEY("nature") REFERENCES "natures"("id")
ON UPDATE NO ACTION ON DELETE NO ACTION;
ALTER TABLE "teams"
ADD FOREIGN KEY("trainer_id") REFERENCES "trainers"("id")
ON UPDATE NO ACTION ON DELETE NO ACTION;
ALTER TABLE "teams"
ADD FOREIGN KEY("game_id") REFERENCES "videogames"("id")
ON UPDATE NO ACTION ON DELETE NO ACTION;
ALTER TABLE "pokemon"
ADD FOREIGN KEY("move_1") REFERENCES "moves"("id")
ON UPDATE NO ACTION ON DELETE NO ACTION;
ALTER TABLE "pokemon"
ADD FOREIGN KEY("move_2") REFERENCES "moves"("id")
ON UPDATE NO ACTION ON DELETE NO ACTION;
ALTER TABLE "pokemon"
ADD FOREIGN KEY("move_3") REFERENCES "moves"("id")
ON UPDATE NO ACTION ON DELETE NO ACTION;
ALTER TABLE "pokemon"
ADD FOREIGN KEY("move_4") REFERENCES "moves"("id")
ON UPDATE NO ACTION ON DELETE NO ACTION;
ALTER TABLE "pokemon"
ADD FOREIGN KEY("equipped_item") REFERENCES "items"("id")
ON UPDATE NO ACTION ON DELETE NO ACTION;
ALTER TABLE "regions"
ADD FOREIGN KEY("generation_id") REFERENCES "generations"("id")
ON UPDATE NO ACTION ON DELETE NO ACTION;
ALTER TABLE "types"
ADD FOREIGN KEY("generation_id") REFERENCES "generations"("id")
ON UPDATE NO ACTION ON DELETE NO ACTION;
ALTER TABLE "moves"
ADD FOREIGN KEY("target_id") REFERENCES "moves_targets"("id")
ON UPDATE NO ACTION ON DELETE NO ACTION;
ALTER TABLE "moves"
ADD FOREIGN KEY("secondary_effect_id") REFERENCES "secondary_effects"("id")
ON UPDATE NO ACTION ON DELETE NO ACTION;
ALTER TABLE "moves"
ADD FOREIGN KEY("generation_id") REFERENCES "generations"("id")
ON UPDATE NO ACTION ON DELETE NO ACTION;
ALTER TABLE "videogames"
ADD FOREIGN KEY("generation_id") REFERENCES "generations"("id")
ON UPDATE NO ACTION ON DELETE NO ACTION;
ALTER TABLE "trainers"
ADD FOREIGN KEY("region") REFERENCES "regions"("id")
ON UPDATE NO ACTION ON DELETE NO ACTION;
ALTER TABLE "trainers"
ADD FOREIGN KEY("best_friend_id") REFERENCES "trainers"("id")
ON UPDATE NO ACTION ON DELETE NO ACTION;
ALTER TABLE "trainers"
ADD FOREIGN KEY("favorite_game_id") REFERENCES "videogames"("id")
ON UPDATE NO ACTION ON DELETE NO ACTION;
ALTER TABLE "videogames"
ADD FOREIGN KEY("region_id") REFERENCES "regions"("id")
ON UPDATE NO ACTION ON DELETE NO ACTION;
ALTER TABLE "game_possesion"
ADD FOREIGN KEY("trainer_id") REFERENCES "trainers"("id")
ON UPDATE NO ACTION ON DELETE NO ACTION;
ALTER TABLE "game_possesion"
ADD FOREIGN KEY("game_id") REFERENCES "videogames"("id")
ON UPDATE NO ACTION ON DELETE NO ACTION;
ALTER TABLE "pokedex"
ADD FOREIGN KEY("generation_id") REFERENCES "generations"("id")
ON UPDATE NO ACTION ON DELETE NO ACTION;
ALTER TABLE "videogames_pokedex"
ADD FOREIGN KEY("pokedex_id") REFERENCES "pokedex"("id")
ON UPDATE NO ACTION ON DELETE NO ACTION;
ALTER TABLE "videogames_pokedex"
ADD FOREIGN KEY("videogame_id") REFERENCES "videogames"("id")
ON UPDATE NO ACTION ON DELETE NO ACTION;
ALTER TABLE "moves_pokedex"
ADD FOREIGN KEY("pokedex_id") REFERENCES "pokedex"("id")
ON UPDATE NO ACTION ON DELETE NO ACTION;
ALTER TABLE "moves_pokedex"
ADD FOREIGN KEY("move_id") REFERENCES "moves"("id")
ON UPDATE NO ACTION ON DELETE NO ACTION;
ALTER TABLE "moves_pokedex"
ADD FOREIGN KEY("videogame_id") REFERENCES "videogames"("id")
ON UPDATE NO ACTION ON DELETE NO ACTION;
ALTER TABLE "pokedex_items"
ADD FOREIGN KEY("pokedex_id") REFERENCES "pokedex"("id")
ON UPDATE NO ACTION ON DELETE NO ACTION;
ALTER TABLE "pokedex_items"
ADD FOREIGN KEY("item_id") REFERENCES "items"("id")
ON UPDATE NO ACTION ON DELETE NO ACTION;
ALTER TABLE "trainers"
ADD FOREIGN KEY("favorite_pokemon_id") REFERENCES "pokedex"("id")
ON UPDATE NO ACTION ON DELETE NO ACTION;
ALTER TABLE "trainer_achievements"
ADD FOREIGN KEY("achievement_id") REFERENCES "achievements"("id")
ON UPDATE NO ACTION ON DELETE NO ACTION;
ALTER TABLE "trainer_achievements"
ADD FOREIGN KEY("trainer_id") REFERENCES "trainers"("id")
ON UPDATE NO ACTION ON DELETE NO ACTION;
ALTER TABLE "types_effectiveness"
ADD FOREIGN KEY("attacker_id") REFERENCES "types"("id")
ON UPDATE NO ACTION ON DELETE NO ACTION;
ALTER TABLE "types_effectiveness"
ADD FOREIGN KEY("defender_id") REFERENCES "types"("id")
ON UPDATE NO ACTION ON DELETE NO ACTION;
ALTER TABLE "pokemon_teams"
ADD FOREIGN KEY("team_id") REFERENCES "teams"("id")
ON UPDATE NO ACTION ON DELETE NO ACTION;
ALTER TABLE "pokemon_teams"
ADD FOREIGN KEY("pokemon_id") REFERENCES "pokemon"("id")
ON UPDATE NO ACTION ON DELETE NO ACTION;