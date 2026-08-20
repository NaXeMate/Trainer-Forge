package dev.trainerforge.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import dev.trainerforge.exception.notfound.PokedexNotFoundException;
import dev.trainerforge.model.entities.Pokedex;
import dev.trainerforge.model.entities.VideogamePokedex;
import dev.trainerforge.model.enumerated.PokemonClass;
import dev.trainerforge.repository.PokedexRepository;
import dev.trainerforge.repository.VideogamePokedexRepository;
import dev.trainerforge.validator.PokedexValidator;
import dev.trainerforge.validator.ValidationLimits;

@Transactional(readOnly = true)
@Service
public class PokedexService {

    private final PokedexRepository pokedexRepo;
    private final VideogamePokedexRepository videogamePokedexRepo;
    
    public PokedexService(PokedexRepository pokedexRepo, VideogamePokedexRepository videogamePokedexRepo) {
        this.pokedexRepo = pokedexRepo;
        this.videogamePokedexRepo = videogamePokedexRepo;
    }

    public List<Pokedex> findAll() {
        return pokedexRepo.findAll();
    }

    public Pokedex findById(Long id) {
        return pokedexRepo.findById(id)
        .orElseThrow(() -> new PokedexNotFoundException(id));
    }

    /**
     * Retrieves a species without imposing a caller-specific missing-resource exception.
     * Package-private visibility keeps this variant inside the service layer.
     */
    Optional<Pokedex> findOptionalById(Long id) {
        return pokedexRepo.findById(id);
    }

    /**
     * Retrieves entries by national Pokedex number after validating allowed bounds.
     *
     * @param nationalPokedex national dex number used as filter criteria.
     * @return all entries that match the provided national dex number.
     * @throws InvalidFilterValueException when the national dex number is outside the supported range.
     * @throws PokedexNotFoundException when no entries match the provided national dex number.
     */
    public List<Pokedex> findByNationalPokedex(Long nationalPokedex) {
        PokedexValidator.validateNationalPokedex(nationalPokedex);
        
        List<Pokedex> result = pokedexRepo.findByNationalPokedex(nationalPokedex);
        PokedexValidator.validateByNationalPokedexResult(result, nationalPokedex);

        return result;
    }

    public Pokedex findByName(String name) {
        return pokedexRepo.findByName(name)
        .orElseThrow(() -> new PokedexNotFoundException("Pokedex entry not found with name: " + name + "."));
    }
    
    /**
     * Retrieves entries introduced in a generation after validating generation bounds.
     *
     * @param generationId identifier of the generation used for filtering.
     * @return all entries associated with the provided generation.
     * @throws InvalidFilterValueException when the generation identifier is outside the supported range.
     * @throws PokedexNotFoundException when no entries are found for the provided generation.
     */
    public List<Pokedex> findByGenerationId(Long generationId) {
        PokedexValidator.validateGenerationId(generationId);

        List<Pokedex> result = pokedexRepo.findByGenerationId(generationId);
        PokedexValidator.validateByGenerationResult(result, generationId);
        
        return result;
    }
    
    public List<Pokedex> findByRegionId(Long regionId) {
        PokedexValidator.validateRegionId(regionId);

        List<Pokedex> result = pokedexRepo.findByRegionId(regionId);
        PokedexValidator.validateByRegionResult(result, regionId);

        return result;
    }
    
    public List<Pokedex> findByPokemonClass(PokemonClass pokemonClass) {
        List<Pokedex> result = pokedexRepo.findByPokemonClass(pokemonClass);
        PokedexValidator.validateByPokemonClassResult(result, pokemonClass);

        return result;
    }

    /**
     * Retrieves entries by species type after validating the type identifier.
     *
     * @param typeId identifier of the pokemon type used as filter criteria.
     * @return all entries that include the provided type.
     * @throws InvalidFilterValueException when the type identifier is outside the supported range.
     * @throws PokedexNotFoundException when no entries include the provided type.
     */
    public List<Pokedex> findByType(Long typeId) {
        PokedexValidator.validateTypeId(typeId);

        List<Pokedex> result = pokedexRepo.findByTypeId(typeId);
        PokedexValidator.validateByTypeResult(result, typeId);
        
        return result;
    }
    
    /**
     * Retrieves entries by ability after validating the ability identifier.
     *
     * @param abilityId identifier of the ability used as filter criteria.
     * @return all entries that can have the requested ability.
     * @throws InvalidFilterValueException when the ability identifier is outside the supported range.
     * @throws PokedexNotFoundException when no entries match the provided ability.
     */
    public List<Pokedex> findByAbility(Long abilityId) {
        PokedexValidator.validateAbilityId(abilityId);

        List<Pokedex> result = pokedexRepo.findByAbilityId(abilityId);
        PokedexValidator.validateByAbilityResult(result, abilityId);
        
        return result;        
    }

    public List<Pokedex> findByCategory(String category) {
        List<Pokedex> result = pokedexRepo.findByCategory(category);
        PokedexValidator.validateByCategoryResult(result, category);
        
        return result;
    }

    public List<Pokedex> findByWeight(BigDecimal weight) {
        PokedexValidator.validateWeight(weight);

        List<Pokedex> result = pokedexRepo.findByWeight(weight);
        PokedexValidator.validateByWeightResult(result, weight);
        
        return result;
    }

    public List<Pokedex> findByHeight(BigDecimal height) {
        PokedexValidator.validateHeight(height);

        List<Pokedex> result = pokedexRepo.findByHeight(height);
        PokedexValidator.validateByHeightResult(result, height);
        
        return result;
    }

    /**
     * Retrieves entries whose weight is within an inclusive interval.
     *
     * When both bounds are equal, this method delegates to exact-weight filtering.
     *
     * @param minWeight lower weight bound in kilograms.
     * @param maxWeight upper weight bound in kilograms.
     * @return all entries with weight inside the requested interval.
     * @throws InvalidFilterValueException when bounds are null, out of allowed limits, or out of order.
     * @throws PokedexNotFoundException when no entries are found in the requested weight interval.
     */
    public List<Pokedex> findByWeightBetween(BigDecimal minWeight, BigDecimal maxWeight) {
        PokedexValidator.validateWeightRange(minWeight, maxWeight);

        if (PokedexValidator.isExactRange(minWeight, maxWeight)) {
            return findByWeight(minWeight);
        }

        List<Pokedex> result = pokedexRepo.findByWeightBetween(minWeight, maxWeight);
        PokedexValidator.validateByWeightRangeResult(result, minWeight, maxWeight);

        return result;
    }

    /**
     * Retrieves entries whose height is within an inclusive interval.
     *
     * When both bounds are equal, this method delegates to exact-height filtering.
     *
     * @param minHeight lower height bound in meters.
     * @param maxHeight upper height bound in meters.
     * @return all entries with height inside the requested interval.
     * @throws InvalidFilterValueException when bounds are null, out of allowed limits, or out of order.
     * @throws PokedexNotFoundException when no entries are found in the requested height interval.
     */
    public List<Pokedex> findByHeightBetween(BigDecimal minHeight, BigDecimal maxHeight) {
        PokedexValidator.validateHeightRange(minHeight, maxHeight);

        if (PokedexValidator.isExactRange(minHeight, maxHeight)) {
            return findByHeight(minHeight);
        }

        List<Pokedex> result = pokedexRepo.findByHeightBetween(minHeight, maxHeight);
        PokedexValidator.validateByHeightRangeResult(result, minHeight, maxHeight);

        return result;
    }

    public List<Pokedex> findByHpBase(int hpBase) {
        PokedexValidator.validateStatRange(hpBase, ValidationLimits.BaseStat.HP);

        List<Pokedex> result = pokedexRepo.findByHpBase(hpBase);
        PokedexValidator.validateByStatResult(result, ValidationLimits.BaseStat.HP, hpBase);
        
        return result;
    }
    
    public List<Pokedex> findByHpBaseBetween(int minHpBase, int maxHpBase) {
        PokedexValidator.validateStatRangeBetween(minHpBase, maxHpBase, ValidationLimits.BaseStat.HP);

        if (PokedexValidator.isExactRange(minHpBase, maxHpBase)) {
            return findByHpBase(minHpBase);
        }

        List<Pokedex> result = pokedexRepo.findByHpBaseBetween(minHpBase, maxHpBase);
        PokedexValidator.validateByStatRangeResult(result, ValidationLimits.BaseStat.HP, minHpBase, maxHpBase);

        return result;
    }

    public List<Pokedex> findByAttackBase(int attackBase) {
        PokedexValidator.validateStatRange(attackBase, ValidationLimits.BaseStat.ATTACK);

        List<Pokedex> result = pokedexRepo.findByAttackBase(attackBase);

        PokedexValidator.validateByStatResult(result, ValidationLimits.BaseStat.ATTACK, attackBase);
        
        return result;
    }

    public List<Pokedex> findByAttackBaseBetween(int minAttackBase, int maxAttackBase) {
        PokedexValidator.validateStatRangeBetween(minAttackBase, maxAttackBase, ValidationLimits.BaseStat.ATTACK);

        if (PokedexValidator.isExactRange(minAttackBase, maxAttackBase)) {
            return findByAttackBase(minAttackBase);
        }

        List<Pokedex> result = pokedexRepo.findByAttackBaseBetween(minAttackBase, maxAttackBase);

        PokedexValidator.validateByStatRangeResult(result, ValidationLimits.BaseStat.ATTACK, minAttackBase, maxAttackBase);

        return result;
    }

    public List<Pokedex> findByDefenseBase(int defenseBase) {
        PokedexValidator.validateStatRange(defenseBase, ValidationLimits.BaseStat.DEFENSE);

        List<Pokedex> result = pokedexRepo.findByDefenseBase(defenseBase);

        PokedexValidator.validateByStatResult(result, ValidationLimits.BaseStat.DEFENSE, defenseBase);

        return result;
    }

    public List<Pokedex> findByDefenseBaseBetween(int minDefenseBase, int maxDefenseBase) {
        PokedexValidator.validateStatRangeBetween(minDefenseBase, maxDefenseBase, ValidationLimits.BaseStat.DEFENSE);

        if (PokedexValidator.isExactRange(minDefenseBase, maxDefenseBase)) {
            return findByDefenseBase(minDefenseBase);
        }

        List<Pokedex> result = pokedexRepo.findByDefenseBaseBetween(minDefenseBase, maxDefenseBase);

        PokedexValidator.validateByStatRangeResult(result, ValidationLimits.BaseStat.DEFENSE, minDefenseBase, maxDefenseBase);

        return result;
    }

    public List<Pokedex> findBySpecialAttackBase(int specialAttackBase) {
        PokedexValidator.validateStatRange(specialAttackBase, ValidationLimits.BaseStat.SPECIAL_ATTACK);

        List<Pokedex> result = pokedexRepo.findBySpecialAttackBase(specialAttackBase);

        PokedexValidator.validateByStatResult(result, ValidationLimits.BaseStat.SPECIAL_ATTACK, specialAttackBase);

        return result;
    }

    public List<Pokedex> findBySpecialAttackBaseBetween(int minSpecialAttackBase, int maxSpecialAttackBase) {
        PokedexValidator.validateStatRangeBetween(minSpecialAttackBase, maxSpecialAttackBase, ValidationLimits.BaseStat.SPECIAL_ATTACK);

        if (PokedexValidator.isExactRange(minSpecialAttackBase, maxSpecialAttackBase)) {
            return findBySpecialAttackBase(minSpecialAttackBase);
        }

        List<Pokedex> result = pokedexRepo.findBySpecialAttackBaseBetween(minSpecialAttackBase, maxSpecialAttackBase);

        PokedexValidator.validateByStatRangeResult(result, ValidationLimits.BaseStat.SPECIAL_ATTACK, minSpecialAttackBase, maxSpecialAttackBase);

        return result;
    }

    public List<Pokedex> findBySpecialDefenseBase(int specialDefenseBase) {
        PokedexValidator.validateStatRange(specialDefenseBase, ValidationLimits.BaseStat.SPECIAL_DEFENSE);

        List<Pokedex> result = pokedexRepo.findBySpecialDefenseBase(specialDefenseBase);

        PokedexValidator.validateByStatResult(result, ValidationLimits.BaseStat.SPECIAL_DEFENSE, specialDefenseBase);

        return result;
    }

    public List<Pokedex> findBySpecialDefenseBaseBetween(int minSpecialDefenseBase, int maxSpecialDefenseBase) {
        PokedexValidator.validateStatRangeBetween(minSpecialDefenseBase, maxSpecialDefenseBase, ValidationLimits.BaseStat.SPECIAL_DEFENSE);

        if (PokedexValidator.isExactRange(minSpecialDefenseBase, maxSpecialDefenseBase)) {
            return findBySpecialDefenseBase(minSpecialDefenseBase);
        }

        List<Pokedex> result = pokedexRepo.findBySpecialDefenseBaseBetween(minSpecialDefenseBase, maxSpecialDefenseBase);

        PokedexValidator.validateByStatRangeResult(result, ValidationLimits.BaseStat.SPECIAL_DEFENSE, minSpecialDefenseBase, maxSpecialDefenseBase);

        return result;
    }

    public List<Pokedex> findBySpeedBase(int speedBase) {
        PokedexValidator.validateStatRange(speedBase, ValidationLimits.BaseStat.SPEED);

        List<Pokedex> result = pokedexRepo.findBySpeedBase(speedBase);

        PokedexValidator.validateByStatResult(result, ValidationLimits.BaseStat.SPEED, speedBase);

        return result;
    }

    public List<Pokedex> findBySpeedBaseBetween(int minSpeedBase, int maxSpeedBase) {
        PokedexValidator.validateStatRangeBetween(minSpeedBase, maxSpeedBase, ValidationLimits.BaseStat.SPEED);
        
        if (PokedexValidator.isExactRange(minSpeedBase, maxSpeedBase)) {
            return findBySpeedBase(minSpeedBase);
        }
        
        List<Pokedex> result = pokedexRepo.findBySpeedBaseBetween(minSpeedBase, maxSpeedBase);
        
        PokedexValidator.validateByStatRangeResult(result, ValidationLimits.BaseStat.SPEED, minSpeedBase, maxSpeedBase);
        
        return result;
    }
    
    public boolean existsById(Long id) {
        return pokedexRepo.existsById(id);
    }
    
    /**
     * Retrieves videogame availability records for a species.
     *
     * @param pokedexId identifier of the species whose videogame records are requested.
     * @return all videogame-pokedex associations for the provided species.
     * @throws PokedexNotFoundException when the species does not appear in any videogame.
     */
    public List<VideogamePokedex> findVideogamesByPokedexId(Long pokedexId) {
        List<VideogamePokedex> result = videogamePokedexRepo.findByPokedexId(pokedexId);
        PokedexValidator.validateByVideogamesResult(result, pokedexId);
        
        return result;
    }
}
