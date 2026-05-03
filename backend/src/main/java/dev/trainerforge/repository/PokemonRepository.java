package dev.trainerforge.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import dev.trainerforge.model.entities.Nature;
import dev.trainerforge.model.entities.Pokemon;
import dev.trainerforge.model.enumerated.Gender;

public interface PokemonRepository extends JpaRepository<Pokemon, Long> {
    List<Pokemon> findBySpecies(Long speciesId);
    List<Pokemon> findByNickname(String nickname);
    List<Pokemon> findByLocationFound(String locationFound);
    List<Pokemon> findByLevel(int level);
    List<Pokemon> findByLevelBetween(int minLevel, int maxLevel);
    List<Pokemon> findByShiny(boolean shiny);
    List<Pokemon> findByGender(Gender gender);
    List<Pokemon> findByAbility(Long abilityId);
    @Query("SELECT p FROM Pokemon p WHERE p.move1.id = :moveId OR p.move2.id = :moveId OR p.move3.id = :moveId OR p.move4.id = :moveId")
    List<Pokemon> findByMove(@Param("moveId") Long moveId);
    List<Pokemon> findByEquippedItem(Long itemId);
    List<Pokemon> findByNature(Nature nature);
    Pokemon findByHpEv(int hpEv);
    List<Pokemon> findByHpEvBetween(int minHpEv, int maxHpEv);
    Pokemon findByAttackEv(int attackEv);
    List<Pokemon> findByAttackEvBetween(int minAttackEv, int maxAttackEv);
    Pokemon findByDefenseEv(int defenseEv);
    List<Pokemon> findByDefenseEvBetween(int minDefenseEv, int maxDefenseEv);
    Pokemon findBySpecialAttackEv(int specialAttackEv);
    List<Pokemon> findBySpecialAttackEvBetween(int minSpecialAttackEv, int maxSpecialAttackEv);
    Pokemon findBySpecialDefenseEv(int specialDefenseEv);
    List<Pokemon> findBySpecialDefenseEvBetween(int minSpecialDefenseEv, int maxSpecialDefenseEv);
    Pokemon findBySpeedEv(int speedEv);
    List<Pokemon> findBySpeedEvBetween(int minSpeedEv, int maxSpeedEv);
}
