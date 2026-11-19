ALTER TABLE "trainer_achievements"
    ADD CONSTRAINT uq_trainer_achievements_trainer_achievement UNIQUE (trainer_id, achievement_id);

INSERT INTO "achievements" (id, name, description, hidden) VALUES
    (6, 'How to Train Your Dragon', 'Defeat Lance''s team using a Dragon-type monotype team.', FALSE),
    (7, 'Heir to Mega Evolution', 'Defeat Korrina''s team using at least one Pokémon holding a Mega Stone.', FALSE),
    (8, 'By an Eiscue''s Hair!', 'Win a simulator battle with a score margin of 10% or less, relative to both scores combined.', TRUE),
    (9, 'Odd Type Out', 'Add a Pokémon with a unique type combination to a team.', FALSE),
    (10, 'Memories of a Pokémon Journey', 'Build a team with Charizard, Lucario, Gardevoir, Absol, Garchomp, and Zygarde.', TRUE),
    (11, 'Let It Go!', 'Build an Ice-type monotype team and use it to defeat a team with at least three Pokémon that resist Ice-type attacks.', TRUE),
    (12, 'Got You Covered', 'Build a team of six Pokémon with no exploitable weaknesses (zero red stripes). Yellow stripes are fine.', FALSE),
    (13, 'Perfectly Balanced, as All Things Should Be', 'Build a team of six Pokémon with no red stripes and a teammate that resists or is immune to each type the team is weak to.', FALSE),
    (14, 'Shiny Spectrum', 'Build a team of six Pokémon without repeating any type.', TRUE),
    (15, 'Gotta Pick ''Em All', 'Save a team that includes Venusaur, Charizard, and Blastoise.', FALSE),
    (16, 'Tour de Forge', 'Save a team of six Pokémon from six different regions.', FALSE),
    (17, 'Hold, Please', 'Save a team of six Pokémon, each holding a different item.', FALSE),
    (18, 'Take a STAB at It', 'Save a team of six Pokémon. Give each one an attack with listed power that matches one of its own types.', FALSE),
    (19, 'Jack of Four Trades', 'Save a team of six Pokémon with one member that knows four attacks of different types, each with listed power.', FALSE),
    (20, 'Eevee League', 'Save a team of six different Eevee evolutions.', TRUE),
    (21, 'One-Mon Army', 'Win a simulator battle with one Pokémon on each side.', FALSE),
    (22, 'Look, Ma, No Items!', 'Win a simulator battle with a team of six Pokémon that are all empty-handed.', FALSE),
    (23, 'Mirror, Mirror', 'Win a simulator battle with the same six Pokémon, including their forms, as your opponent.', TRUE),
    (24, 'The Underdex', 'Win a six-on-six simulator battle with a lower combined base stat total than your opponent.', FALSE);
-- Explicit catalogue IDs do not advance the sequence used by generated IDs.
SELECT setval(pg_get_serial_sequence('achievements', 'id'), MAX(id)) FROM "achievements";
