package dev.trainerforge.repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import dev.trainerforge.model.entities.Pokedex;
import dev.trainerforge.model.enumerated.PokemonClass;

public interface PokedexRepository extends JpaRepository<Pokedex, Long> {
    List<Pokedex> findByNationalPokedex(Long nationalPokedex);
    Optional<Pokedex> findByName(String name);
    List<Pokedex> findByGenerationId(Long generationId);
    List<Pokedex> findByRegionId(Long regionId);
    List<Pokedex> findByPokemonClass(PokemonClass pokemonClass);
    @Query("SELECT p FROM Pokedex p WHERE p.type1.id = :typeId OR p.type2.id = :typeId")
    List<Pokedex> findByTypeId(@Param("typeId") Long typeId);
    @Query("SELECT p FROM Pokedex p WHERE p.ability1.id = :abilityId OR p.ability2.id = :abilityId OR p.hiddenAbility.id = :abilityId")
    List<Pokedex> findByAbilityId(@Param("abilityId") Long abilityId);
    Optional<Pokedex> findByCategory(String category);
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
