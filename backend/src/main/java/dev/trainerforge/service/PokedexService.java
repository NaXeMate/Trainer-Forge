package dev.trainerforge.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import dev.trainerforge.exception.InvalidFilterValueException;
import dev.trainerforge.exception.PokedexNotFoundException;
import dev.trainerforge.model.entities.Pokedex;
import dev.trainerforge.model.enumerated.PokemonClass;
import dev.trainerforge.repository.PokedexRepository;

@Transactional(readOnly = true)
@Service
public class PokedexService {

    private final PokedexRepository pokedexRepo;
    
    private static final Long MAX_NATIONAL_POKEDEX = 1025L;
    
    private static final Long GENERATION_MAX_ID = 10L;

    private static final Long REGION_MAX_ID = 10L;

    private static final Long MAX_TYPE_ID = 18L;

    private static final Long MAX_ABILITY_ID = 326L;
    
    private static final BigDecimal MAX_WEIGHT = new BigDecimal("999.9");
    private static final BigDecimal MIN_WEIGHT = new BigDecimal("0.1");

    private static final BigDecimal MAX_HEIGHT = new BigDecimal("100.0");
    private static final BigDecimal MIN_HEIGHT = new BigDecimal("0.1");
    
    private static final int MAX_BASE_STAT = 255;
    private static final int MIN_BASE_STAT = 5;

    private void validateStatRange(int value, String statName) {
        if (value < MIN_BASE_STAT || value > MAX_BASE_STAT) {
            throw new InvalidFilterValueException(statName + " base stat must be between " + MIN_BASE_STAT + " and " + MAX_BASE_STAT + ".");
        }
    }

    private void validateStatRangeBetween(int minValue, int maxValue, String statName) {
        if (minValue < MIN_BASE_STAT) {
            throw new InvalidFilterValueException("Minimum " + statName + " cannot be less than " + MIN_BASE_STAT + ".");
        }

        if (maxValue > MAX_BASE_STAT) {
            throw new InvalidFilterValueException("Maximum " + statName + " cannot be greater than " + MAX_BASE_STAT + ".");
        }

        if (minValue > maxValue) {
            throw new InvalidFilterValueException("Minimum " + statName + " (" + minValue + ") cannot be greater than maximum " + statName + " (" + maxValue + ").");
        }
    }

    private void validateBigDecimalRange(BigDecimal min, BigDecimal max, BigDecimal minBound, BigDecimal maxBound, String fieldName, String unit) {
        if (min == null || max == null) {
            throw new InvalidFilterValueException(fieldName + " values cannot be null.");
        }

        if (min.compareTo(minBound) < 0) {
            throw new InvalidFilterValueException("Minimum " + fieldName + " cannot be less than " + minBound + " " + unit + ".");
        }

        if (max.compareTo(maxBound) > 0) {
            throw new InvalidFilterValueException("Maximum " + fieldName + " cannot be greater than " + maxBound + " " + unit + ".");
        }

        if (min.compareTo(max) > 0) {
            throw new InvalidFilterValueException("Minimum " + fieldName + " (" + min
                + ") cannot be greater than maximum " + fieldName + " (" + max + ") " + unit + ".");
        }
    }

    public PokedexService(PokedexRepository pokedexRepo) {
        this.pokedexRepo = pokedexRepo;
    }

    public List<Pokedex> findAll() {
        return pokedexRepo.findAll();
    }

    public Pokedex findById(Long id) {
        return pokedexRepo.findById(id)
        .orElseThrow(() -> new PokedexNotFoundException(id));
    }

    public List<Pokedex> findByNationalPokedex(Long nationalPokedex) {
        if (nationalPokedex < 1 || nationalPokedex > MAX_NATIONAL_POKEDEX) {
            throw new InvalidFilterValueException("National Pokedex number must be between 1 and " + MAX_NATIONAL_POKEDEX + ".");
        }
        
        List<Pokedex> result = pokedexRepo.findByNationalPokedex(nationalPokedex);

        if (result.isEmpty()) {
            throw new PokedexNotFoundException("Pokedex entry not found with national Pokedex number: #" + nationalPokedex + ".");
        }

        return result;
    }

    public Pokedex findByName(String name) {
        return pokedexRepo.findByName(name)
        .orElseThrow(() -> new PokedexNotFoundException("Pokedex entry not found with name: " + name + "."));
    }
    
    public List<Pokedex> findByGenerationId(Long generationId) {
        if (generationId < 1 || generationId > GENERATION_MAX_ID) {
            throw new InvalidFilterValueException("Generation ID must be between 1 and " + GENERATION_MAX_ID + ".");
        }

        List<Pokedex> result = pokedexRepo.findByGenerationId(generationId);

        if (result.isEmpty()) {
            throw new PokedexNotFoundException("Pokedex entry not found with generation ID: " + generationId + ".");
        }
        
        return result;
    }
    
    public List<Pokedex> findByRegionId(Long regionId) {
        if (regionId < 1 || regionId > REGION_MAX_ID) {
            throw new InvalidFilterValueException("Region ID must be between 1 and " + REGION_MAX_ID + ".");
        }

        List<Pokedex> result = pokedexRepo.findByRegionId(regionId);

        if (result.isEmpty()) {
            throw new PokedexNotFoundException("Pokedex entry not found with region ID: " + regionId + ".");
        }

        return result;
    }
    
    public List<Pokedex> findByPokemonClass(PokemonClass pokemonClass) {
        List<Pokedex> result = pokedexRepo.findByPokemonClass(pokemonClass);

        if (result.isEmpty()) {
            throw new PokedexNotFoundException("Pokedex entry not found with Pokémon class: " + pokemonClass + ".");
        }

        return result;
    }

    public List<Pokedex> findByType(Long typeId) {
        
        if (typeId < 1 || typeId > MAX_TYPE_ID) {
            throw new InvalidFilterValueException("Type ID must be between 1 and " + MAX_TYPE_ID + ".");
        }

        List<Pokedex> result = pokedexRepo.findByTypeId(typeId);

        if (result.isEmpty()) {
            throw new PokedexNotFoundException("Pokedex entry not found with type ID: " + typeId + ".");
        }
        
        return result;
    }
    
    public List<Pokedex> findByAbility(Long abilityId) {
        if (abilityId < 1 || abilityId > MAX_ABILITY_ID) {
            throw new InvalidFilterValueException("Ability ID must be between 1 and " + MAX_ABILITY_ID + ".");
        }

        List<Pokedex> result = pokedexRepo.findByAbilityId(abilityId);

        if (result.isEmpty()) {
            throw new PokedexNotFoundException("Pokedex entry not found with ability ID: " + abilityId + ".");
        }
        
        return result;        
    }

    public List<Pokedex> findByCategory(String category) {
        List<Pokedex> result = pokedexRepo.findByCategory(category);
        
        if (result.isEmpty()) {
            throw new PokedexNotFoundException("Pokedex entry found with category: " + category + ".");
        }
        
        return result;
    }

    public List<Pokedex> findByWeight(BigDecimal weight) {
        validateBigDecimalRange(weight, weight, MIN_WEIGHT, MAX_WEIGHT, "Weight", "kg");

        List<Pokedex> result = pokedexRepo.findByWeight(weight);

        if (result.isEmpty()) {
            throw new PokedexNotFoundException("Pokedex entry found with weight: " + weight + " kg.");
        }
        
        return result;
    }

    public List<Pokedex> findByHeight(BigDecimal height) {
        validateBigDecimalRange(height, height, MIN_HEIGHT, MAX_HEIGHT, "Height", "m");

        List<Pokedex> result = pokedexRepo.findByHeight(height);

        if (result.isEmpty()) {
            throw new PokedexNotFoundException("Pokedex entry found with height: " + height + " m.");
        }
        
        return result;
    }

    public List<Pokedex> findByWeightBetween(BigDecimal minWeight, BigDecimal maxWeight) {
        validateBigDecimalRange(minWeight, maxWeight, MIN_WEIGHT, MAX_WEIGHT, "Weight", "kg");

        if (minWeight.compareTo(maxWeight) == 0) {
            return findByWeight(minWeight);
        }

        List<Pokedex> result = pokedexRepo.findByWeightBetween(minWeight, maxWeight);

        if (result.isEmpty()) {
            throw new PokedexNotFoundException("Pokedex entry found with weight between " + minWeight + " and " + maxWeight + " kg.");
        }

        return result;
    }

    public List<Pokedex> findByHeightBetween(BigDecimal minHeight, BigDecimal maxHeight) {
        validateBigDecimalRange(minHeight, maxHeight, MIN_HEIGHT, MAX_HEIGHT, "Height", "m");

        if (minHeight.compareTo(maxHeight) == 0) {
            return findByHeight(minHeight);
        }

        List<Pokedex> result = pokedexRepo.findByHeightBetween(minHeight, maxHeight);

        if (result.isEmpty()) {
            throw new PokedexNotFoundException("Pokedex entry found with height between " + minHeight + " and " + maxHeight + " m.");
        }

        return result;
    }

    public List<Pokedex> findByHpBase(int hpBase) {
        validateStatRange(hpBase, "HP");

        List<Pokedex> result = pokedexRepo.findByHpBase(hpBase);

        if (result.isEmpty()) {
            throw new PokedexNotFoundException("Pokedex entry found with HP base stat: " + hpBase + ".");
        }
        
        return result;
    }
    
    public List<Pokedex> findByHpBaseBetween(int minHpBase, int maxHpBase) {
        validateStatRangeBetween(minHpBase, maxHpBase, "HP");

        if (minHpBase == maxHpBase) {
            return findByHpBase(minHpBase);
        }

        List<Pokedex> result = pokedexRepo.findByHpBaseBetween(minHpBase, maxHpBase);

        if (result.isEmpty()) {
            throw new PokedexNotFoundException("Pokedex entry found with HP between " + minHpBase + " and " + maxHpBase + ".");
        }

        return result;
    }

    public List<Pokedex> findByAttackBase(int attackBase) {
        validateStatRange(attackBase, "Attack");

        List<Pokedex> result = pokedexRepo.findByAttackBase(attackBase);

        if (result.isEmpty()) {
            throw new PokedexNotFoundException("Pokedex entry found with Attack base stat: " + attackBase + ".");
        }
        
        return result;
    }

    public List<Pokedex> findByAttackBaseBetween(int minAttackBase, int maxAttackBase) {
        validateStatRangeBetween(minAttackBase, maxAttackBase, "Attack");

        if (minAttackBase == maxAttackBase) {
            return findByAttackBase(minAttackBase);
        }

        List<Pokedex> result = pokedexRepo.findByAttackBaseBetween(minAttackBase, maxAttackBase);

        if (result.isEmpty()) {
            throw new PokedexNotFoundException("Pokedex entry found with Attack between " + minAttackBase + " and " + maxAttackBase + ".");
        }

        return result;
    }

    public List<Pokedex> findByDefenseBase(int defenseBase) {
        validateStatRange(defenseBase, "Defense");

        List<Pokedex> result = pokedexRepo.findByDefenseBase(defenseBase);

        if (result.isEmpty()) {
            throw new PokedexNotFoundException("Pokedex entry found with Defense base stat: " + defenseBase + ".");
        }

        return result;
    }

    public List<Pokedex> findByDefenseBaseBetween(int minDefenseBase, int maxDefenseBase) {
        validateStatRangeBetween(minDefenseBase, maxDefenseBase, "Defense");

        if (minDefenseBase == maxDefenseBase) {
            return findByDefenseBase(minDefenseBase);
        }

        List<Pokedex> result = pokedexRepo.findByDefenseBaseBetween(minDefenseBase, maxDefenseBase);

        if (result.isEmpty()) {
            throw new PokedexNotFoundException("Pokedex entry found with Defense between " + minDefenseBase + " and " + maxDefenseBase + ".");
        }

        return result;
    }

    public List<Pokedex> findBySpecialAttackBase(int specialAttackBase) {
        validateStatRange(specialAttackBase, "Special Attack");

        List<Pokedex> result = pokedexRepo.findBySpecialAttackBase(specialAttackBase);

        if (result.isEmpty()) {
            throw new PokedexNotFoundException("Pokedex entry found with Special Attack base stat: " + specialAttackBase + ".");
        }

        return result;
    }

    public List<Pokedex> findBySpecialAttackBaseBetween(int minSpecialAttackBase, int maxSpecialAttackBase) {
        validateStatRangeBetween(minSpecialAttackBase, maxSpecialAttackBase, "Special Attack");

        if (minSpecialAttackBase == maxSpecialAttackBase) {
            return findBySpecialAttackBase(minSpecialAttackBase);
        }

        List<Pokedex> result = pokedexRepo.findBySpecialAttackBaseBetween(minSpecialAttackBase, maxSpecialAttackBase);

        if (result.isEmpty()) {
            throw new PokedexNotFoundException("Pokedex entry found with Special Attack between " + minSpecialAttackBase + " and " + maxSpecialAttackBase + ".");
        }

        return result;
    }

    public List<Pokedex> findBySpecialDefenseBase(int specialDefenseBase) {
        validateStatRange(specialDefenseBase, "Special Defense");

        List<Pokedex> result = pokedexRepo.findBySpecialDefenseBase(specialDefenseBase);

        if (result.isEmpty()) {
            throw new PokedexNotFoundException("Pokedex entry found with Special Defense base stat: " + specialDefenseBase + ".");
        }

        return result;
    }

    public List<Pokedex> findBySpecialDefenseBaseBetween(int minSpecialDefenseBase, int maxSpecialDefenseBase) {
        validateStatRangeBetween(minSpecialDefenseBase, maxSpecialDefenseBase, "Special Defense");

        if (minSpecialDefenseBase == maxSpecialDefenseBase) {
            return findBySpecialDefenseBase(minSpecialDefenseBase);
        }

        List<Pokedex> result = pokedexRepo.findBySpecialDefenseBaseBetween(minSpecialDefenseBase, maxSpecialDefenseBase);

        if (result.isEmpty()) {
            throw new PokedexNotFoundException("Pokedex entry found with Special Defense between " + minSpecialDefenseBase + " and " + maxSpecialDefenseBase + ".");
        }

        return result;
    }

    public List<Pokedex> findBySpeedBase(int speedBase) {
        validateStatRange(speedBase, "Speed");

        List<Pokedex> result = pokedexRepo.findBySpeedBase(speedBase);

        if (result.isEmpty()) {
            throw new PokedexNotFoundException("Pokedex entry found with Speed base stat: " + speedBase + ".");
        }

        return result;
    }

    public List<Pokedex> findBySpeedBaseBetween(int minSpeedBase, int maxSpeedBase) {
        validateStatRangeBetween(minSpeedBase, maxSpeedBase, "Speed");

        if (minSpeedBase == maxSpeedBase) {
            return findBySpeedBase(minSpeedBase);
        }

        List<Pokedex> result = pokedexRepo.findBySpeedBaseBetween(minSpeedBase, maxSpeedBase);

        if (result.isEmpty()) {
            throw new PokedexNotFoundException("Pokedex entry found with Speed between " + minSpeedBase + " and " + maxSpeedBase + ".");
        }

        return result;
    }
}
