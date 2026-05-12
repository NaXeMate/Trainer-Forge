package dev.trainerforge.repository;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import dev.trainerforge.model.entities.Pokedex;
import dev.trainerforge.model.enumerated.PokemonClass;

public interface PokedexRepository extends JpaRepository<Pokedex, Long> {
    List<Pokedex> findByNationalPokedex(Long nationalPokedex);
    Pokedex findByName(String name);
    List<Pokedex> findByGenerationId(Long generationId);
    List<Pokedex> findByRegionId(Long regionId);
    List<Pokedex> findByPokemonClass(PokemonClass pokemonClass);
    List<Pokedex> findByType1Id(Long type1Id);
    List<Pokedex> findByType2Id(Long type2Id);
    List<Pokedex> findByAbility1Id(Long ability1Id);
    List<Pokedex> findByAbility2Id(Long ability2Id);
    Pokedex findByCategory(String category);
    List<Pokedex> findByWeight(BigDecimal weight);
    List<Pokedex> findByHeight(BigDecimal height);
    List<Pokedex> findByWeightBetween(BigDecimal minWeight, BigDecimal maxWeight);
    List<Pokedex> findByHeightBetween(BigDecimal minHeight, BigDecimal maxHeight);
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
