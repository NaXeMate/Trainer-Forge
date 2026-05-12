package dev.trainerforge.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import dev.trainerforge.model.entities.Pokedex;
import dev.trainerforge.model.enumerated.PokemonClass;

public interface PokedexRepository extends JpaRepository<Pokedex, Long> {
    List<Pokedex> findByNationalPokedex(Long nationalPokedex);
    Pokedex findByName(String name);
    List<Pokedex> findByGenerationId(Long generationId);
    List<Pokedex> findByRegion(String region);
    List<Pokedex> findByPokemonClass(PokemonClass pokemonClass);
    List<Pokedex> findByType1(String type);
    List<Pokedex> findByType2(String type);
    List<Pokedex> findByAbility1(String ability);
    List<Pokedex> findByAbility2(String ability);
    Pokedex findByCategory(String category);
    List<Pokedex> findByWeight(Double weight);
    List<Pokedex> findByHeight(Double height);
    List<Pokedex> findByWeightBetween(Double minWeight, Double maxWeight);
    List<Pokedex> findByHeightBetween(Double minHeight, Double maxHeight);
    List<Pokedex> findByHpBase(int hpBase);
    List<Pokedex> findByHpBaseBetween(int minHpBase, int maxHpBase);
    List<Pokedex> findByAttackBase(int attackBase);
    List<Pokedex> findByAttackBaseBetween(int minAttackBase, int maxAttackBase);
    List<Pokedex> findByDefenseBase(int defenseBase);
    List<Pokedex> findByDefenseBaseBetween(int minDefenseBase, int maxDefenseBase);
    List<Pokedex> findBySpecialAttackBase(int specialAttackBase);
    List<Pokedex> findBySpecialAttackBaseBetween(int minSpecialAttackBase, int maxSpecialAttackBase);
    List<Pokedex> findBySpecialDefenseBase(int specialDefenseBase);
    List<Pokedex> findBySpecialDefenseBaseBetween(int minSpecialDefenseBase, int maxSpecialDefenseBase);
    List<Pokedex> findBySpeedBase(int speedBase);
    List<Pokedex> findBySpeedBaseBetween(int minSpeedBase, int maxSpeedBase);
}
