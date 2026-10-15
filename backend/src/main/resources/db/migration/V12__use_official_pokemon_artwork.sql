-- Replace default PokéAPI sprites with official artwork

UPDATE "pokedex"
SET "image_url" = regexp_replace(
    "image_url",
    '^https://raw\.githubusercontent\.com/PokeAPI/sprites/master/sprites/pokemon/([0-9]+)\.png$',
    'https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/\1.png'
)
WHERE "image_url" ~ '^https://raw\.githubusercontent\.com/PokeAPI/sprites/master/sprites/pokemon/[0-9]+\.png$';
