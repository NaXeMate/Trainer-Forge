-- ========================================
-- V4 - DEMO TRAINERS, TEAMS & POKÉMON DATA
-- ========================================


-- ========================================
-- MORE POKEMON SPECIES
-- ========================================

INSERT INTO "pokedex" (id, national_pokedex, name, image_url, generation_id, region_id, class, type_1_id, type_2_id, ability_1_id, ability_2_id, hidden_ability_id,
 description, category, weight, height, hp_base, attack_base, defense_base, special_attack_base, special_defense_base, speed_base) VALUES
(233, 906, 'Sprigatito', 'https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/906.png', 9, 9, 'COMMON', 12, NULL, 134, NULL, 291, 'Its fluffy fur is similar in composition to plants. This Pokémon frequently washes its face to keep it from drying out.', 'Grass Cat Pokemon', 0.4, 4.1, 40, 61, 54, 45, 45, 65),
(234, 907, 'Floragato', 'https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/907.png', 9, 9, 'COMMON', 12, NULL, 134, NULL, 291, 'The hardness of Floragato''s fur depends on the Pokémon''s mood. When Floragato is prepared to battle, its fur becomes pointed and needle sharp.', 'Grass Cat Pokemon', 0.9, 12.2, 61, 80, 63, 60, 63, 83),
(235, 908, 'Meowscarada', 'https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/908.png', 9, 9, 'COMMON', 12, 15, 134, NULL, 291, 'This Pokémon uses the reflective fur lining in its cape to camouflage the stem of its flower, creating the illusion that the flower is floating.', 'Magician Pokemon', 1.5, 31.2, 76, 110, 70, 81, 70, 123),
(236, 909, 'Fuecoco', 'https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/909.png', 9, 9, 'COMMON', 7, NULL, 17, NULL, 320, 'Its flame sac is small, so energy is always leaking out. This energy is released from the dent atop Fuecoco''s head and flickers to and fro.', 'Fire Croc Pokemon', 0.4, 9.8, 67, 45, 59, 63, 40, 36),
(237, 910, 'Crocalor', 'https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/910.png', 9, 9, 'COMMON', 7, NULL, 17, NULL, 320, 'The combination of Crocalor''s fire energy and overflowing vitality has caused an egg-shaped fireball to appear on the Pokémon''s head.', 'Fire Croc Pokemon', 1.0, 30.7, 81, 55, 78, 90, 58, 49),
(238, 911, 'Skeledirge', 'https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/911.png', 9, 9, 'COMMON', 7, 6, 17, NULL, 320, 'The fiery bird changes shape when Skeledirge sings. Rumor has it that the bird was born when the fireball on Skeledirge''s head gained a soul.', 'Fire Ghost Pokemon', 1.6, 326.5, 104, 75, 100, 110, 75, 66),
(239, 856, 'Hatenna', 'https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/856.png', 8, 8, 'COMMON', 13, NULL, 82, 244, 273, 'It senses the feelings of other living creatures. Be careful not to expose it to strong emotions for too long, or it will end up exhausted.', 'Calm Pokemon', 0.4, 3.4, 42, 30, 45, 56, 53, 39),
(240, 857, 'Hattrem', 'https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/857.png', 8, 8, 'COMMON', 13, NULL, 82, 244, 273, 'The moment this Pokémon finds someone who''s emitting strong emotions, it will pummel them senseless with its braids to silence them.', 'Serene Pokemon', 0.6, 4.8, 57, 40, 65, 86, 73, 49),
(241, 858, 'Hatterene', 'https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/858.png', 8, 8, 'COMMON', 13, 8, 82, 244, 273, 'Hatterene knocks out those that intrude in its home forest by blasting them with a beam, then slashing with claws enhanced by psychic power.', 'Silent Pokemon', 2.1, 5.1, 57, 90, 95, 136, 103, 29),
(242, 1024, 'Terapagos', 'https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/1024.png', 9, 9, 'LEGENDARY', 11, NULL, 214, NULL, NULL, 'Terapagos protects itself using its power to transform energy into hard crystals. This Pokémon is the source of the Terastal phenomenon.', 'Tera Pokemon', 0.2, 6.5, 90, 65, 85, 65, 85, 60),
(243, 935, 'Charcadet', 'https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/935.png', 9, 9, 'COMMON', 7, NULL, 255, NULL, 254, 'This Pokémon is skilled at manipulating flames. Humans have prized it highly since ancient times.', 'Fire Child Pokemon', 0.6, 10.5, 40, 50, 40, 50, 40, 35),
(244, 936, 'Armarouge', 'https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/936.png', 9, 9, 'COMMON', 7, 13, 255, NULL, 326, 'Though Armarouge pledges loyalty to its Trainer, it repeatedly leaves to go train on its own to strengthen its psychic energy.', 'Fire Warrior Pokemon', 1.5, 85.0, 85, 60, 100, 125, 80, 75),
(245, 937, 'Ceruledge', 'https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/937.png', 9, 9, 'COMMON', 7, 6, 255, NULL, 326, 'It swears loyalty to its Trainer. Should anyone dare to show that Trainer hostility, Ceruledge will cut the offender down without a second thought.', 'Fire Blades Pokemon', 1.6, 62.0, 75, 125, 80, 60, 100, 85),
(246, 940, 'Wattrel', 'https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/940.png', 9, 9, 'COMMON', 5, 18, 238, 324, 28, 'When its wings catch the wind, the bones within produce electricity. This Pokémon dives into the ocean, catching prey by electrocuting them.', 'Storm Petrel Pokemon', 0.4, 3.6, 40, 40, 35, 55, 40, 70),
(247, 941, 'Kilowattrel', 'https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/941.png', 9, 9, 'COMMON', 5, 18, 238, 324, 28, 'Kilowattrel inflates its throat sac to amplify its electricity. By riding the wind, this Pokémon can fly over 430 miles in a day.', 'Frigatebird Pokemon', 1.4, 38.6, 70, 70, 60, 105, 60, 125);


-- ========================================
-- MORE MOVES
-- ========================================

INSERT INTO "moves" (id, name, type_id, class, power, accuracy, contact, priority, target_id, secondary_effect_id, secondary_effect_chance, pp, generation_id) VALUES
(164, 'Flower Trick', 12, 'PHYSICAL', 75, NULL, FALSE, 0, 7, NULL, NULL, 10, 9),
(165, 'Acrobatics', 18, 'PHYSICAL', 55, 100, TRUE, 0, 7, NULL, NULL, 15, 5),
(166, 'Magical Leaf', 12, 'SPECIAL', 60, NULL, FALSE, 0, 7, NULL, NULL, 20, 3),
(167, 'Heal Pulse', 13, 'STATUS', NULL, NULL, FALSE, 0, 6, NULL, NULL, 10, 5),
(168, 'Healing Wish', 13, 'STATUS', NULL, NULL, FALSE, 0, 1, NULL, NULL, 10, 4),
(169, 'Psycho Cut', 13, 'PHYSICAL', 70, 100, FALSE, 0, 7, NULL, NULL, 20, 4),
(170, 'Tera Starstorm', 11, 'SPECIAL', 100, 100, FALSE, 0, 9, NULL, NULL, 5, 9),
(171, 'Rapid Spin', 11, 'PHYSICAL', 50, 100, FALSE, 0, 7, 32, 100, 40, 1),
(172, 'Clear Smog', 17, 'SPECIAL', 50, NULL, FALSE, 0, 7, NULL, NULL, 15, 5),
(173, 'Armor Cannon', 7, 'SPECIAL', 120, 100, FALSE, 0, 7, 31, 100, 5, 9),
(174, 'Disarming Voice', 8, 'SPECIAL', 40, NULL, FALSE, 0, 9, NULL, NULL, 15, 6),
(175, 'Stomping Tantrum', 16, 'PHYSICAL', 75, 100, TRUE, 0, 7, NULL, NULL, 10, 7),
(176, 'Torch Song', 7, 'SPECIAL', 80, NULL, FALSE, 0, 7, 30, 100, 10, 9),
(177, 'Uproar', 11, 'SPECIAL', 90, 100, FALSE, 0, 7, NULL, NULL, 10, 3),
(178, 'Electro Ball', 5, 'SPECIAL', NULL, 100, FALSE, 0, 7, NULL, NULL, 10, 4),
(179, 'Supercell Slam', 5, 'SPECIAL', 100, 95, TRUE, 0, 7, NULL, NULL, 15, 9),
(180, 'Aerial Ace', 18, 'PHYSICAL', 60, NULL, TRUE, 0, 7, NULL, NULL, 20, 3),
(181, 'Flash Cannon', 1, 'SPECIAL', 80, 100, FALSE, 0, 7, 10, 10, 10, 4),
(182, 'Close Combat', 10, 'PHYSICAL', 120, 100, TRUE, 0, 7, 22, 100, 5, 4),
(183, 'Volt Tackle', 5, 'PHYSICAL', 120, 100, TRUE, 0, 7, 1, 10, 15, 3);


-- ========================================
-- ACHIEVEMENTS
-- ========================================

INSERT INTO "achievements" (id, name, description, hidden) VALUES
(1, 'Route 1', 'Create your first team in TrainerForge.', FALSE),
(2, 'Ready for Battle', 'Create a full team of six Pokemon.', FALSE),
(3, 'Gym Leader Fan', 'Create a team focused on a single type.', FALSE),
(4, 'Oh, the old days!', 'Create a team using only Pokemon from Kanto.', TRUE),
(5, 'Rising Volt-Tackler', 'Win a battle against Liko''s team or Rod''s team.', TRUE);


-- ========================================
-- TRAINERS (USERS)
-- ========================================

INSERT INTO "trainers"
(id, username, email, profile_picture_url, password_hash, real_name, region_id, favorite_game_id, favorite_pokemon_id, best_friend_id, friend_code, trainer_class)
VALUES
(1,
 'red',
 'red@trainerforge.test',
 'https://cdn.trainerforge.test/avatars/red.png',
 'hash_red_demo',
 'Red',
 1,   -- Kanto
 10,   -- Pokemon FireRed
 153, -- Charizard
 NULL,
 'TF0000000001',
 'CHAMPION'),

(2,
 'leaf',
 'leaf@trainerforge.test',
 'https://cdn.trainerforge.test/avatars/leaf.png',
 'hash_leaf_demo',
 'Leaf',
 1,   -- Kanto
 11,  -- Pokemon LeafGreen
 150, -- Venusaur
 1,   -- Red
 'TF0000000002',
 'CHAMPION'),

(3,
 'liko',
 'liko@trainerforge.test',
 'https://cdn.trainerforge.test/avatars/liko.png',
 'hash_liko_demo',
 'Liko',
 9,   -- Paldea
  36,   -- Pokemon Scarlet
  235,   -- Meowscarada
  NULL,   -- Rod (set after INSERT to satisfy FK)
  'TF0000000003',
  'ADVENTURER'),

(4,
 'may',
 'may@trainerforge.test',
 'https://cdn.trainerforge.test/avatars/may.png',
 'hash_may_demo',
 'May',
 3,   -- Hoenn
 24,  -- Alpha Sapphire
 148,   -- Bulbasaur
 2,   -- Leaf
 'TF0000000004',
 'COORDINATOR'),

(5,
 'rod',
 'rod@trainerforge.test',
 'https://cdn.trainerforge.test/avatars/rod.png',
 'hash_rod_demo',
 'Rod',
 9,   -- Paldea
 36,  -- Pokemon Scarlet
 238, -- Skeledirge
 3,   -- Liko
 'TF0000000005',
 'ADVENTURER');


-- ========================================
-- TRAINER ACHIEVEMENTS
-- ========================================

INSERT INTO "trainer_achievements"
(id, trainer_id, achievement_id, date_obtained)
VALUES
(1, 1, 1, TIMESTAMP '2025-01-15 18:30:00'),
(2, 1, 4, TIMESTAMP '2025-02-03 21:10:00'),
(3, 2, 1, TIMESTAMP '2025-03-10 10:05:00'),
(4, 3, 1, TIMESTAMP '2025-04-01 16:45:00'),
(5, 5, 5, TIMESTAMP '2025-05-20 12:00:00');


-- ========================================
-- GAME POSSESIONS (TRAINER'S GAMES)
-- ========================================

INSERT INTO "game_possesions" (id, trainer_id, videogame_id) VALUES
(1, 1, 1),   -- Red -> Pokemon Red
(2, 1, 2),   -- Red -> Pokemon Blue
(3, 2, 11),  -- Leaf -> Pokemon LeafGreen
(4, 2, 10),  -- Leaf -> Pokemon FireRed
(5, 3, 36),   -- Liko -> Pokemon Scarlet
(6, 3, 37),   -- Liko -> Pokemon Violet
(7, 4, 24),  -- May -> Alpha Sapphire
(8, 4, 9),   -- May -> Pokemon Emerald
(9, 5, 36),  -- Rod -> Pokemon Scarlet
(10, 5, 37),  -- Rod -> Pokemon Violet
(11, 1, 10);   -- Red -> Pokemon FireRed


-- ========================================
-- POKEMON FOR TEAMS
-- ========================================

INSERT INTO "pokemon"
(id, species_id, nickname, location_found, level, shiny, gender,
 ability_id, move_1_id, move_2_id, move_3_id, move_4_id,
 equipped_item_id, nature_id,
 hp_ev, attack_ev, defense_ev, special_attack_ev, special_defense_ev, speed_ev)
VALUES
-- Red's Team: Venusaur, Charizard, Blastoise
(1, 150, NULL, 'Kanto Route 2', 100, FALSE, 'MALE',
 134, 125, 69, 91, 140, 1, 3,
 32, 4, 0, 32, 0, 2),

(2, 153, NULL, 'Palet Town', 100, FALSE, 'MALE',
 17, 45, 40, 30, 87, 3, 24,
 0, 32, 0, 4, 0, 32),

(3, 156, NULL, 'Kanto Route 24', 100, FALSE, 'MALE',
 220, 59, 136, 30, 63, 4, 20,
 32, 0, 32, 4, 0, 2),

-- Equipo de Leaf: Pikachu, Clefable, Gardevoir
(4, 53, NULL, 'Kanto Viridian Forest', 55, FALSE, 'FEMALE',
 310, 148, 99, 150, 149, 2, 21,
 0, 4, 0, 32, 0, 32),

(5, 57, NULL, 'Mt. Moon', 55, FALSE, 'FEMALE',
 251, 98, 102, 153, 62, 1, 11,
 32, 0, 4, 32, 0, 2),

(6, 89, NULL, 'Hoenn Route 102', 55, FALSE, 'FEMALE',
 317, 98, 103, 139, 62, 4, 15,
 32, 0, 0, 32, 4, 2),

-- Equipo de Liko: Meowscarada, Hatterene, Terapagos, Armarouge
-- Meowscarada
(7, 235, NULL, 'Indigo Academy', 90, FALSE, 'FEMALE',
 134, 164, 165, 166, 99,
  1, 3,
 0, 32, 0, 0, 4, 32),

-- Hatterene
(8, 241, NULL, 'Motostoke City', 70, FALSE, 'FEMALE',
 82, 18, 167, 168, 169,
  9, 11,
 32, 0, 0, 32, 4, 0),

-- Terapagos
(9, 242, NULL, 'Crystal Pool', 85, FALSE, 'GENDERLESS',
 214, 170, 96, 171, 5,
  68, 10,
 0, 32, 2, 2, 32, 0),

-- Armarouge
(10, 244, NULL, 'Paldea South Province (Area 5)', 75, FALSE, 'MALE',
 255, 172, 173, 143, 42,
  2, 1,
 0, 4, 0, 32, 0, 32),

-- Equipo de May: Gardevoir, Gallade, Lucario
(11, 89, NULL, 'Hoenn Route 117', 62, FALSE, 'FEMALE',
 317, 98, 48, 140, 62, 4, 16,
 32, 0, 0, 32, 4, 2),

(12, 90, NULL, 'Hoenn Victory Road', 62, FALSE, 'MALE',
 195, 121, 90, 75, 34, 2, 22,
 0, 32, 0, 0, 4, 32),

(13, 136, NULL, 'Sinnoh Iron Island', 65, FALSE, 'MALE',
 195, 12, 30, 74, 45, 3, 21,
 0, 32, 0, 4, 0, 32),

-- Rod's Team: Skeledirge, Kilowattrel, Lucario (shiny), Pikachu (another instance)
-- Skeledirge
(14, 238, NULL, 'Rod''s Island', 80, FALSE, 'MALE',
 17, 176, 174, 175, 45,
  2, 11,
 32, 0, 2, 32, 2, 0),

-- Kilowattrel
(15, 247, NULL, 'Paldea East Province (Area 2)', 68, FALSE, 'MALE',
 238, 126, 177, 178, 179,
  66, 1,
 0, 32, 0, 16, 4, 16),

-- Lucario (shiny)
(16, 136, NULL, 'Lost Mayo Maze', 80, TRUE, 'MALE',
 195, 76, 180, 181, 182,
  159, 1,
 0, 32, 0, 16, 0, 20),

-- Captain Pikachu
 (17, 53, 'Cap', 'Paldea', 90, FALSE, 'MALE',
 271, 183, 25, 120, 147,
  16, 22,
 0, 32, 0, 4, 0, 32);


-- ========================================
-- TEAMS (EQUIPOS)
-- ========================================

INSERT INTO "teams"
(id, trainer_id, videogame_id, name, modality, hidden)
VALUES
(1, 1, 1, 'Kanto Classics', 'THEMED', FALSE),
(2, 2, 11, 'LeafGreen Squad', 'NORMAL', FALSE),
(3, 3, 36, 'Liko''s Team', 'THEMED', FALSE),
(4, 4, 24, 'Hoenn Spectacular Team', 'NORMAL', FALSE),
(5, 5, 36, 'Rod''s Team',  'THEMED', FALSE);


-- ========================================
-- POKÉMON_TEAMS (TEAM / POKEMON)
-- ========================================

INSERT INTO "pokemon_teams"
(id, team_id, pokemon_id, position)
VALUES
-- Team 1: Red
(1, 1, 1, 1),   -- Venusaur
(2, 1, 2, 2),   -- Charizard
(3, 1, 3, 3),   -- Blastoise

-- Team 2: Leaf
(4, 2, 4, 1),   -- Pikachu
(5, 2, 5, 2),   -- Clefable
(6, 2, 6, 3),   -- Gardevoir

-- Team 3: Liko
(7, 3, 7, 1),   -- Meowscarada
(8, 3, 8, 2),   -- Hatterene
(9, 3, 9, 3),   -- Terapagos
(10, 3, 10, 4), -- Armarouge

-- Team 4: May
(11, 4, 11, 1), -- Gardevoir
(12, 4, 12, 2), -- Gallade
(13, 4, 13, 3), -- Lucario

-- Team 5: Rod
(14, 5, 14, 1), -- Skeledirge
(15, 5, 15, 2), -- Kilowattrel
(16, 5, 16, 3), -- Lucario
(17, 5, 17, 4); -- Captain Pikachu