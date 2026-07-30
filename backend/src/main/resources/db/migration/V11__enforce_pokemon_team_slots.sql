ALTER TABLE "pokemon_teams"
ADD CONSTRAINT "chk_pokemon_teams_position"
CHECK ("position" BETWEEN 1 AND 6);

ALTER TABLE "pokemon_teams"
ADD CONSTRAINT "uq_pokemon_teams_team_position"
UNIQUE ("team_id", "position");

ALTER TABLE "pokemon_teams"
ADD CONSTRAINT "uq_pokemon_teams_team_pokemon"
UNIQUE ("team_id", "pokemon_id");
