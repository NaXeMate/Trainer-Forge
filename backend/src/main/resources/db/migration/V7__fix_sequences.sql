SELECT setval(pg_get_serial_sequence('"pokemon"', 'id'), COALESCE(MAX(id), 1), MAX(id) IS NOT NULL) FROM "pokemon";
SELECT setval(pg_get_serial_sequence('"trainers"', 'id'), COALESCE(MAX(id), 1), MAX(id) IS NOT NULL) FROM "trainers";
SELECT setval(pg_get_serial_sequence('"teams"', 'id'), COALESCE(MAX(id), 1), MAX(id) IS NOT NULL) FROM "teams";
SELECT setval(pg_get_serial_sequence('"pokemon_teams"', 'id'), COALESCE(MAX(id), 1), MAX(id) IS NOT NULL) FROM "pokemon_teams";
SELECT setval(pg_get_serial_sequence('"pokedex"', 'id'), COALESCE(MAX(id), 1), MAX(id) IS NOT NULL) FROM "pokedex";
SELECT setval(pg_get_serial_sequence('"achievements"', 'id'), COALESCE(MAX(id), 1), MAX(id) IS NOT NULL) FROM "achievements";
SELECT setval(pg_get_serial_sequence('"trainer_achievements"', 'id'), COALESCE(MAX(id), 1), MAX(id) IS NOT NULL) FROM "trainer_achievements";
SELECT setval(pg_get_serial_sequence('"game_possesions"', 'id'), COALESCE(MAX(id), 1), MAX(id) IS NOT NULL) FROM "game_possesions";