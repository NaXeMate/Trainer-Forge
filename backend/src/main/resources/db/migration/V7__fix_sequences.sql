SELECT setval('pokemon_id_seq',        (SELECT MAX(id) FROM pokemon));
SELECT setval('trainers_id_seq',       (SELECT MAX(id) FROM trainers));
SELECT setval('teams_id_seq',          (SELECT MAX(id) FROM teams));
SELECT setval('pokemon_teams_id_seq',  (SELECT MAX(id) FROM pokemon_teams));
SELECT setval('pokedex_id_seq',        (SELECT MAX(id) FROM pokedex));
SELECT setval('achievements_id_seq',   (SELECT MAX(id) FROM achievements));
SELECT setval('trainer_achievements_id_seq', (SELECT MAX(id) FROM trainer_achievements));
SELECT setval('game_possesions_id_seq',(SELECT MAX(id) FROM game_possesions));