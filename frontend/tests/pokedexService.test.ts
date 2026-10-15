import assert from "node:assert/strict";
import { afterEach, mock, test } from "node:test";

import { getPokedexEntries } from "../src/services/pokedexService.ts";
import type { PokedexEntry } from "../src/types/pokedexTypes.ts";

function entry(id: number, nationalPokedex = 152): PokedexEntry {
  return {
    id,
    nationalPokedex,
    name: `Species ${id}`,
    imageUrl: "https://example.com/pokemon.png",
    generation: 2,
    region: "Johto",
    pokemonClass: "COMMON",
    type1: "Grass",
    type2: null,
    ability1: "Overgrow",
    ability2: null,
    hiddenAbility: null,
    description: "Test species",
    category: "Leaf Pokemon",
    weight: 6.4,
    height: 0.9,
    hpBase: 45,
    attackBase: 49,
    defenseBase: 65,
    specialAttackBase: 49,
    specialDefenseBase: 65,
    speedBase: 45,
  };
}

afterEach(() => mock.restoreAll());

test("sorts each response numerically by database ID, independently of national number", async () => {
  const entries = [entry(10, 1), entry(2, 152), entry(1, 500)];
  mock.method(globalThis, "fetch", async () => new Response(JSON.stringify(entries)));

  const result = await getPokedexEntries();

  assert.deepEqual(result.map((pokemon) => pokemon.id), [1, 2, 10]);
  assert.deepEqual(result.map((pokemon) => pokemon.nationalPokedex), [500, 152, 1]);
});

test("does not mutate the response array when sorting", async () => {
  const entries = Object.freeze([entry(10), entry(2), entry(1)]);
  const response = new Response();
  mock.method(response, "json", async () => entries);
  mock.method(globalThis, "fetch", async () => response);

  const result = await getPokedexEntries();

  assert.deepEqual(result.map((pokemon) => pokemon.id), [1, 2, 10]);
  assert.deepEqual(entries.map((pokemon) => pokemon.id), [10, 2, 1]);
  assert.notEqual(result, entries);
});

test("sorts fresh data again when the request is retried or repeated", async () => {
  const responses = [[entry(10), entry(2)], [entry(7), entry(1)]];
  let call = 0;
  mock.method(globalThis, "fetch", async () => new Response(JSON.stringify(responses[call++])));

  assert.deepEqual((await getPokedexEntries()).map((pokemon) => pokemon.id), [2, 10]);
  assert.deepEqual((await getPokedexEntries()).map((pokemon) => pokemon.id), [1, 7]);
});

test("preserves an empty response", async () => {
  mock.method(globalThis, "fetch", async () => new Response("[]"));

  assert.deepEqual(await getPokedexEntries(), []);
});

test("rejects a response that is not a list", async () => {
  mock.method(globalThis, "fetch", async () => new Response('{"entries":[]}'));

  await assert.rejects(getPokedexEntries(), /Invalid PokéDex list response/);
});

test("propagates server errors so the page can offer a retry", async () => {
  mock.method(globalThis, "fetch", async () => new Response(null, { status: 503 }));

  await assert.rejects(getPokedexEntries(), /Error 503/);
});
