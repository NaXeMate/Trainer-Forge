CREATE TYPE "pokemon_class_t" AS ENUM ('COMMON', 'LEGENDARY', 'MYTHICAL', 'ULTRABEAST', 'PARADOX');

CREATE TABLE IF NOT EXISTS "pokedex" (
	"id" bigserial NOT NULL UNIQUE,
	"national_pokedex" bigint NOT NULL,
	"name" varchar(64) NOT NULL UNIQUE,
	"image_url" varchar(255) NOT NULL,
	"generation_id" bigint NOT NULL,
	"region_id" bigint NOT NULL,
	"class" pokemon_class_t NOT NULL DEFAULT 'COMMON',
	"type_1_id" bigint NOT NULL,
	"type_2_id" bigint,
	"ability_1_id" bigint NOT NULL,
	"ability_2_id" bigint,
	"hidden_ability_id" bigint,
	"description" text(65535) NOT NULL,
	"category" varchar(64) NOT NULL,
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


CREATE TYPE "gender_t" AS ENUM ('MALE', 'FEMALE', 'GENDERLESS');

CREATE TABLE IF NOT EXISTS "pokemon" (
	"id" bigserial NOT NULL UNIQUE,
	"species_id" bigint NOT NULL,
	"nickname" varchar(64),
	"location_found" varchar(255) NOT NULL,
	"level" int NOT NULL,
	"shiny" boolean NOT NULL DEFAULT 0,
	"gender" gender_t,
	"ability_id" bigint NOT NULL,
	"move_1_id" bigint NOT NULL,
	"move_2_id" bigint,
	"move_3_id" bigint,
	"move_4_id" bigint,
	"equipped_item_id" bigint,
	"nature_id" bigint NOT NULL,
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
	"type_id" bigint NOT NULL,
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
	"name" varchar(100) NOT NULL UNIQUE,
	"generation_id" bigint NOT NULL,
	PRIMARY KEY("id")
);


CREATE TYPE "nature_rise_lower_t" AS ENUM ('ATTACK', 'DEFENSE', 'SPECIAL_ATTACK', 'SPECIAL_DEFENSE', 'SPEED');

CREATE TABLE IF NOT EXISTS "natures" (
	"id" bigserial NOT NULL UNIQUE,
	"name" varchar(100) NOT NULL UNIQUE,
	"rise" nature_rise_lower_t NOT NULL,
	"lower" nature_rise_lower_t NOT NULL,
	PRIMARY KEY("id")
);


CREATE TABLE IF NOT EXISTS "pokemon_types" (
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


CREATE TYPE "type_item_t" AS ENUM ('HELD_ITEM', 'BATTLE_ITEM', 'KEY_ITEM', 'EVOLUTION_ITEM', 'OTHER');

CREATE TABLE IF NOT EXISTS "pokemon_items" (
	"id" bigserial NOT NULL UNIQUE,
	"name" varchar(64) NOT NULL UNIQUE,
	"description" text(65535) NOT NULL,
	"type" type_item_t NOT NULL,
	"generation_id" bigint NOT NULL,
	PRIMARY KEY("id")
);


CREATE TYPE "team_modality_t" AS ENUM ('NORMAL', 'NUZLOCKE', 'MONOTYPE', 'THEMED');

CREATE TABLE IF NOT EXISTS "teams" (
	"id" bigserial NOT NULL UNIQUE,
	"trainer_id" bigint NOT NULL,
	"videogame_id" bigint NOT NULL,
	"name" varchar(100) NOT NULL,
	"modality" team_modality_t NOT NULL DEFAULT 'NORMAL',
	"hidden" boolean NOT NULL DEFAULT TRUE,
	PRIMARY KEY("id")
);


CREATE TYPE "trainer_class_t" AS ENUM ('NOVICE', 'CHAMPION', 'ACE_TRAINER', 'GYM_LEADER', 'ELITE_FOUR', 'RIVAL', 'PROFESSOR', 'HIKER', 'BIKE_RIDER', 'SWIMMER', 'BLACK_BELT', 'BEAUTY', 'COOLTRAINER', 'LASS', 'CAMPER', 'POKEMON_BREEDER', 'POKEMON_RANGER', 'POKEMON_SCHOOL_TEACHER', 'POKEMON_FAN', 'POKEMON_COLLECTOR', 'POKEMON_RIDER', 'DRAGON_TAMER', 'FISHERMAN', 'GENTLEMAN', 'LADY', 'NINJA', 'PARASOL_LADY', 'PICNICKER', 'POLICEMAN', 'PSYCHIC', 'SAILOR', 'SCHOOL_KID', 'SENIOR', 'SWIMSUIT', 'TUBER', 'YOUNGSTER', 'WORKER', 'JUGGLER', 'KIMONO_GIRL', 'KIMONO_BOY', 'KAHUNA', 'CAPTAIN');

CREATE TABLE IF NOT EXISTS "trainers" (
	"id" bigserial NOT NULL UNIQUE,
	"username" varchar(55) NOT NULL UNIQUE,
	"email" varchar(255) NOT NULL,
	"profile_picture_url" varchar(255),
	"password_hash" varchar(255) NOT NULL,
	"real_name" varchar(55) UNIQUE,
	"region_id" bigint,
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


CREATE TABLE IF NOT EXISTS "game_possesions" (
	"id" bigserial NOT NULL UNIQUE,
	"trainer_id" bigint NOT NULL,
	"videogame_id" bigint NOT NULL,
	PRIMARY KEY("id")
);


CREATE TABLE IF NOT EXISTS "videogames_pokedex" (
	"id" bigserial NOT NULL UNIQUE,
	"pokedex_id" bigint NOT NULL,
	"videogame_id" bigint NOT NULL,
	PRIMARY KEY("id")
);


CREATE TYPE "learning_method_t" AS ENUM ('LEVEL', 'MT_MO', 'EGG', 'TUTOR', 'OTHER');

CREATE TABLE IF NOT EXISTS "moves_pokedex" (
	"id" bigserial NOT NULL UNIQUE,
	"pokedex_id" bigint NOT NULL,
	"move_id" bigint NOT NULL,
	"videogame_id" bigint NOT NULL,
	"learning_method" learning_method_t NOT NULL DEFAULT 'LEVEL',
	"level" int,
	PRIMARY KEY("id")
);


CREATE TYPE "item_relationship_t" AS ENUM ('EVOLUTION', 'MEGASTONE', 'Z_CRISTAL', 'ASSOCIATED', 'WILD_HELD', 'OTHER');

CREATE TABLE IF NOT EXISTS "pokedex_items" (
	"id" bigserial NOT NULL UNIQUE,
	"pokedex_id" bigint NOT NULL,
	"item_id" bigint NOT NULL,
	"item_relationship" item_relationship_t NOT NULL,
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


CREATE TABLE IF NOT EXISTS "type_effectiveness" (
	"id" bigserial NOT NULL UNIQUE,
	"attacking_type_id" bigint NOT NULL,
	"defending_type_id" bigint NOT NULL,
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


ALTER TABLE "pokemon_items"
ADD FOREIGN KEY("generation_id") REFERENCES "generations"("id")
ON UPDATE NO ACTION ON DELETE NO ACTION;
ALTER TABLE "abilities"
ADD FOREIGN KEY("generation_id") REFERENCES "generations"("id")
ON UPDATE NO ACTION ON DELETE NO ACTION;
ALTER TABLE "regions"
ADD FOREIGN KEY("generation_id") REFERENCES "generations"("id")
ON UPDATE NO ACTION ON DELETE NO ACTION;
ALTER TABLE "pokemon_types"
ADD FOREIGN KEY("generation_id") REFERENCES "generations"("id")
ON UPDATE NO ACTION ON DELETE NO ACTION;
ALTER TABLE "moves"
ADD FOREIGN KEY("type_id") REFERENCES "pokemon_types"("id")
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
ALTER TABLE "pokedex"
ADD FOREIGN KEY("generation_id") REFERENCES "generations"("id")
ON UPDATE NO ACTION ON DELETE NO ACTION;
ALTER TABLE "pokedex"
ADD FOREIGN KEY("region_id") REFERENCES "regions"("id")
ON UPDATE NO ACTION ON DELETE NO ACTION;
ALTER TABLE "pokedex"
ADD FOREIGN KEY("type_1_id") REFERENCES "pokemon_types"("id")
ON UPDATE NO ACTION ON DELETE NO ACTION;
ALTER TABLE "pokedex"
ADD FOREIGN KEY("type_2_id") REFERENCES "pokemon_types"("id")
ON UPDATE NO ACTION ON DELETE NO ACTION;
ALTER TABLE "pokedex"
ADD FOREIGN KEY("ability_1_id") REFERENCES "abilities"("id")
ON UPDATE NO ACTION ON DELETE NO ACTION;
ALTER TABLE "pokedex"
ADD FOREIGN KEY("ability_2_id") REFERENCES "abilities"("id")
ON UPDATE NO ACTION ON DELETE NO ACTION;
ALTER TABLE "pokedex"
ADD FOREIGN KEY("hidden_ability_id") REFERENCES "abilities"("id")
ON UPDATE NO ACTION ON DELETE NO ACTION;
ALTER TABLE "pokemon"
ADD FOREIGN KEY("species_id") REFERENCES "pokedex"("id")
ON UPDATE NO ACTION ON DELETE NO ACTION;
ALTER TABLE "pokemon"
ADD FOREIGN KEY("ability_id") REFERENCES "abilities"("id")
ON UPDATE NO ACTION ON DELETE NO ACTION;
ALTER TABLE "pokemon"
ADD FOREIGN KEY("nature_id") REFERENCES "natures"("id")
ON UPDATE NO ACTION ON DELETE NO ACTION;
ALTER TABLE "pokemon"
ADD FOREIGN KEY("move_1_id") REFERENCES "moves"("id")
ON UPDATE NO ACTION ON DELETE NO ACTION;
ALTER TABLE "pokemon"
ADD FOREIGN KEY("move_2_id") REFERENCES "moves"("id")
ON UPDATE NO ACTION ON DELETE NO ACTION;
ALTER TABLE "pokemon"
ADD FOREIGN KEY("move_3_id") REFERENCES "moves"("id")
ON UPDATE NO ACTION ON DELETE NO ACTION;
ALTER TABLE "pokemon"
ADD FOREIGN KEY("move_4_id") REFERENCES "moves"("id")
ON UPDATE NO ACTION ON DELETE NO ACTION;
ALTER TABLE "pokemon"
ADD FOREIGN KEY("equipped_item_id") REFERENCES "pokemon_items"("id")
ON UPDATE NO ACTION ON DELETE NO ACTION;
ALTER TABLE "videogames"
ADD FOREIGN KEY("generation_id") REFERENCES "generations"("id")
ON UPDATE NO ACTION ON DELETE NO ACTION;
ALTER TABLE "videogames"
ADD FOREIGN KEY("region_id") REFERENCES "regions"("id")
ON UPDATE NO ACTION ON DELETE NO ACTION;
ALTER TABLE "trainers"
ADD FOREIGN KEY("region_id") REFERENCES "regions"("id")
ON UPDATE NO ACTION ON DELETE NO ACTION;
ALTER TABLE "trainers"
ADD FOREIGN KEY("best_friend_id") REFERENCES "trainers"("id")
ON UPDATE NO ACTION ON DELETE NO ACTION;
ALTER TABLE "trainers"
ADD FOREIGN KEY("favorite_game_id") REFERENCES "videogames"("id")
ON UPDATE NO ACTION ON DELETE NO ACTION;
ALTER TABLE "trainers"
ADD FOREIGN KEY("favorite_pokemon_id") REFERENCES "pokedex"("id")
ON UPDATE NO ACTION ON DELETE NO ACTION;
ALTER TABLE "teams"
ADD FOREIGN KEY("trainer_id") REFERENCES "trainers"("id")
ON UPDATE NO ACTION ON DELETE NO ACTION;
ALTER TABLE "teams"
ADD FOREIGN KEY("videogame_id") REFERENCES "videogames"("id")
ON UPDATE NO ACTION ON DELETE NO ACTION;
ALTER TABLE "game_possesions"
ADD FOREIGN KEY("trainer_id") REFERENCES "trainers"("id")
ON UPDATE NO ACTION ON DELETE NO ACTION;
ALTER TABLE "game_possesions"
ADD FOREIGN KEY("videogame_id") REFERENCES "videogames"("id")
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
ADD FOREIGN KEY("item_id") REFERENCES "pokemon_items"("id")
ON UPDATE NO ACTION ON DELETE NO ACTION;
ALTER TABLE "trainer_achievements"
ADD FOREIGN KEY("achievement_id") REFERENCES "achievements"("id")
ON UPDATE NO ACTION ON DELETE NO ACTION;
ALTER TABLE "trainer_achievements"
ADD FOREIGN KEY("trainer_id") REFERENCES "trainers"("id")
ON UPDATE NO ACTION ON DELETE NO ACTION;
ALTER TABLE "type_effectiveness"
ADD FOREIGN KEY("attacking_type_id") REFERENCES "pokemon_types"("id")
ON UPDATE NO ACTION ON DELETE NO ACTION;
ALTER TABLE "type_effectiveness"
ADD FOREIGN KEY("defending_type_id") REFERENCES "pokemon_types"("id")
ON UPDATE NO ACTION ON DELETE NO ACTION;
ALTER TABLE "pokemon_teams"
ADD FOREIGN KEY("team_id") REFERENCES "teams"("id")
ON UPDATE NO ACTION ON DELETE NO ACTION;
ALTER TABLE "pokemon_teams"
ADD FOREIGN KEY("pokemon_id") REFERENCES "pokemon"("id")
ON UPDATE NO ACTION ON DELETE NO ACTION;