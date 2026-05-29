package dev.trainerforge.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import dev.trainerforge.dto.response.PokemonDto;
import dev.trainerforge.exception.InvalidFilterValueException;
import dev.trainerforge.exception.notfound.PokemonNotFoundException;
import dev.trainerforge.mapper.PokemonMapper;
import dev.trainerforge.model.entities.Nature;
import dev.trainerforge.model.entities.Pokemon;
import dev.trainerforge.model.enumerated.Gender;
import dev.trainerforge.repository.PokemonRepository;

@Transactional(readOnly = true)
@Service
public class PokemonService {

    private final PokemonRepository pokemonRepo;
    private final PokedexService pokedexService;
    private final AbilityService abilityService;
    private final MoveService moveService;
    private final NatureService natureService;
    private final PokemonItemService pokemonItemService;

    private final PokemonMapper pokemonMapper;

    private static final int LEVEL_MIN = 1;
    private static final int LEVEL_MAX = 100;

    private static final int EV_MIN = 0;
    private static final int EV_MAX = 32; // This uses the new Pokemon Champions system for EVs.

    private void validateEvRange(int value, String statName) {
        if (value < EV_MIN || value > EV_MAX) {
            throw new InvalidFilterValueException(statName + " EVs must be between " + EV_MIN + " and " + EV_MAX + ".");
        }
    }

    private void validateEvRangeBetween(int minValue, int maxValue, String statName) {
        if (minValue < EV_MIN) {
            throw new InvalidFilterValueException("Minimum " + statName + " EVs cannot be less than " + EV_MIN + ".");
        }

        if (maxValue > EV_MAX) {
            throw new InvalidFilterValueException("Maximum " + statName + " EVs cannot be greater than " + EV_MAX + ".");
        }

        if (minValue > maxValue) {
            throw new InvalidFilterValueException("Minimum " + statName + " EVs (" + minValue + ") cannot be greater than maximum " + statName + " EVs (" + maxValue + ").");
        }
    }

    private void validateSpecies(Long speciesId) {
        if (!pokedexService.existsById(speciesId)) {
            throw new InvalidFilterValueException("No Pokemon found with species id: " + speciesId + ".");
        }
    }

    private void validateLocationFound(String locationFound) {
        if (locationFound == null || locationFound.isBlank()) {
            throw new InvalidFilterValueException("The location found cannot be empty.");
        }
    }

    private void validateLevel(int level) {
        if (level < LEVEL_MIN || level > LEVEL_MAX) {
            throw new InvalidFilterValueException("Levels must be between " + LEVEL_MIN + " and " + LEVEL_MAX + ".");
        }
    }

    private void validateAbility(String ability) {
        if (ability == null || ability.isBlank()) {
            throw new InvalidFilterValueException("The ability cannot be empty.");
        }
    }

    private void validateMove(String move) {
        if (move == null || move.isBlank()) {
            throw new InvalidFilterValueException("The move cannot be empty.");
        }
    }

    private void validateEquippedItem(String equippedItem) {
        if (equippedItem == null || equippedItem.isBlank()) {
            throw new InvalidFilterValueException("The equipped item cannot be empty.");
        }
    }

    private void validateNature(String nature) {
        if (nature == null || nature.isBlank()) {
            throw new InvalidFilterValueException("The nature cannot be empty.");
        }
    }

    private void validatePokemonFromDto(PokemonDto dto) {
        validateSpecies(dto.species());
        validateLocationFound(dto.locationFound());
        validateLevel(dto.level());
        validateAbility(dto.ability());
        validateMove(dto.move1());

        if (dto.move2() != null && !dto.move2().isBlank()) {
            validateMove(dto.move2());
        }
        if (dto.move3() != null && !dto.move3().isBlank()) {
            validateMove(dto.move3());
        }
        if (dto.move4() != null && !dto.move4().isBlank()) {
            validateMove(dto.move4());
        }
        if (dto.equippedItem() != null && !dto.equippedItem().isBlank()) {
            validateEquippedItem(dto.equippedItem());
        }

        validateNature(dto.nature());
        validateEvRange(dto.hpEv(), "HP");
        validateEvRange(dto.attackEv(), "Attack");
        validateEvRange(dto.defenseEv(), "Defense");
        validateEvRange(dto.specialAttackEv(), "Special Attack");
        validateEvRange(dto.specialDefenseEv(), "Special Defense");
        validateEvRange(dto.speedEv(), "Speed");

        java.util.List<String> moves = java.util.stream.Stream.of(dto.move1(), dto.move2(), dto.move3(), dto.move4())
            .filter(m -> m != null && !m.isBlank())
            .toList();
        if (moves.size() != new java.util.HashSet<>(moves).size()) {
            throw new InvalidFilterValueException("All moves must be different from each other.");
        }
    }

    public PokemonService(
        PokemonRepository pokemonRepo,
        PokedexService pokedexService,
        AbilityService abilityService,
        MoveService moveService,
        NatureService natureService,
        PokemonItemService pokemonItemService,
        PokemonMapper pokemonMapper
    ) {
        this.pokemonRepo = pokemonRepo;
        this.pokedexService = pokedexService;
        this.abilityService = abilityService;
        this.moveService = moveService;
        this.natureService = natureService;
        this.pokemonItemService = pokemonItemService;
        this.pokemonMapper = pokemonMapper;
    }

    private void updateRelationsFromDto(PokemonDto dto, Pokemon pokemon) {
        pokemon.setSpecies(pokedexService.findById(dto.species()));
        pokemon.setAbility(abilityService.findByName(dto.ability()));
        pokemon.setMove1(moveService.findByName(dto.move1()));
        pokemon.setNature(natureService.findByName(dto.nature()));

        if (dto.move2() != null && !dto.move2().isBlank()) {
            pokemon.setMove2(moveService.findByName(dto.move2()));
        }
        if (dto.move3() != null && !dto.move3().isBlank()) {
            pokemon.setMove3(moveService.findByName(dto.move3()));
        }
        if (dto.move4() != null && !dto.move4().isBlank()) {
            pokemon.setMove4(moveService.findByName(dto.move4()));
        }
        if (dto.equippedItem() != null && !dto.equippedItem().isBlank()) {
            pokemon.setEquippedItem(pokemonItemService.findByName(dto.equippedItem()));
        }
    }

    @Transactional
    public Pokemon createPokemon(PokemonDto dto) {

        validatePokemonFromDto(dto);

        Pokemon newPokemon = new Pokemon();
        pokemonMapper.updateEntityFromDto(dto, newPokemon);
        updateRelationsFromDto(dto, newPokemon);
        
        // The Pokemon species name will be used as default "nickname" if it's not provided
        if (dto.nickname() == null || dto.nickname().isBlank()) {
            String speciesName = pokedexService.findById(dto.species()).getName();
            newPokemon.setNickname(speciesName);
        }

        return pokemonRepo.save(newPokemon);
    }

    @Transactional
    public Pokemon updatePokemon(Long id, PokemonDto dto) {
        Pokemon pokemon = this.findById(id);

        validatePokemonFromDto(dto);

        pokemonMapper.updateEntityFromDto(dto, pokemon);
        updateRelationsFromDto(dto, pokemon);

        return pokemonRepo.save(pokemon);
    }

    @Transactional
    public void deletePokemon(Long id) {
        Pokemon pokemon = this.findById(id);
        pokemonRepo.delete(pokemon);
        System.out.println("Deleted Pokemon with id: " + id);
    }

    public List<Pokemon> findAll() {
        return pokemonRepo.findAll();
    }

    public Pokemon findById(Long id) {
        return pokemonRepo.findById(id)
        .orElseThrow(() -> new PokemonNotFoundException(id));
    }

    public List<Pokemon> findBySpeciesId(Long speciesId) {
        validateSpecies(speciesId);
        return pokemonRepo.findBySpeciesId(speciesId);
    }

    public List<Pokemon> findByNickname(String nickname) {
        if (nickname == null || nickname.isBlank()) {
            throw new InvalidFilterValueException("The nickname cannot be empty.");
        }
        
        List<Pokemon> result = pokemonRepo.findByNickname(nickname);

        if (result.isEmpty()) {
            throw new PokemonNotFoundException("No Pokemon/s found with nickname: " + nickname + ".");
        }
        
        return result;
    }

    public List<Pokemon> findByLocationFound(String locationFound) {
        if (locationFound == null || locationFound.isBlank()) {
            throw new InvalidFilterValueException("The location found cannot be empty.");
        }

        List<Pokemon> result = pokemonRepo.findByLocationFound(locationFound);

        if (result.isEmpty()) {
            throw new PokemonNotFoundException("No Pokemon/s found captured at location " + locationFound + " were found.");
        }
        
        return result;
    }

    public List<Pokemon> findByLevel(int level) {
        validateLevel(level);

        return pokemonRepo.findByLevel(level);
    }

    public List<Pokemon> findByLevelBetween(int minLevel, int maxLevel) {
        validateLevel(minLevel);
        validateLevel(maxLevel);

        if (minLevel > maxLevel) {
            throw new InvalidFilterValueException("Minimum level (" + minLevel + ") cannot be greater than maximum level (" + maxLevel + ").");
        }
        
        if (minLevel == maxLevel) {
            return findByLevel(minLevel);
        }

        return pokemonRepo.findByLevelBetween(minLevel, maxLevel);
    }

    public List<Pokemon> findByShiny(boolean shiny) {
        List<Pokemon> result = pokemonRepo.findByShiny(shiny);

        if (result.isEmpty()) {
            throw new PokemonNotFoundException("No shiny Pokemon were found.");
        }
        
        return result;
    }


    public List<Pokemon> findByGender(Gender gender) {
        if (gender == null) {
            throw new InvalidFilterValueException("The gender filter cannot be null.");
        }

        List<Pokemon> result = pokemonRepo.findByGender(gender);

        if (result.isEmpty()) {
            throw new PokemonNotFoundException("No Pokemon found with gender: " + gender + ".");
        }

        return result;
    }

    public List<Pokemon> findByAbilityId(Long abilityId) {
        List<Pokemon> result = pokemonRepo.findByAbilityId(abilityId);

        if (result.isEmpty()) {
            throw new PokemonNotFoundException("No Pokemon found with ability id: " + abilityId + ".");
        }
        
        return result;
    }

    public List<Pokemon> findByMoveId(Long moveId) {
        List<Pokemon> result = pokemonRepo.findByMoveId(moveId);

        if (result.isEmpty()) {
            throw new PokemonNotFoundException("No Pokemon found with move id: " + moveId + ".");
        }
        
        return result;
    }

    public List<Pokemon> findByEquippedItemId(Long itemId) {
        List<Pokemon> result = pokemonRepo.findByEquippedItemId(itemId);

        if (result.isEmpty()) {
            throw new PokemonNotFoundException("No Pokemon found with equipped item id: " + itemId + ".");
        }
        
        return result;
    }

    public List<Pokemon> findByNature(Nature nature) {
        List<Pokemon> result = pokemonRepo.findByNature(nature);

        if (result.isEmpty()) {
            throw new PokemonNotFoundException("No Pokemon found with nature: " + nature + ".");
        }
        return result;
    }

    public List<Pokemon> findByHpEv(int hpEv) {
        validateEvRange(hpEv, "HP");

        List<Pokemon> result = pokemonRepo.findByHpEv(hpEv);

        if (result.isEmpty()) {
            throw new PokemonNotFoundException("No Pokemon found with HP EVs: " + hpEv + ".");
        }

        return result;
    }

    public List<Pokemon> findByHpEvBetween(int minHpEv, int maxHpEv) {
        validateEvRangeBetween(minHpEv, maxHpEv, "HP");

        if (minHpEv == maxHpEv) {
            return findByHpEv(minHpEv);
        }

        List<Pokemon> result = pokemonRepo.findByHpEvBetween(minHpEv, maxHpEv);

        if (result.isEmpty()) {
            throw new PokemonNotFoundException("No Pokemon found with HP EVs between " + minHpEv + " and " + maxHpEv + ".");
        }

        return result;
    }

    public List<Pokemon> findByAttackEv(int attackEv) {
        validateEvRange(attackEv, "Attack");

        List<Pokemon> result = pokemonRepo.findByAttackEv(attackEv);

        if (result.isEmpty()) {
            throw new PokemonNotFoundException("No Pokemon found with Attack EVs: " + attackEv + ".");
        }

        return result;
    }

    public List<Pokemon> findByAttackEvBetween(int minAttackEv, int maxAttackEv) {
        validateEvRangeBetween(minAttackEv, maxAttackEv, "Attack");

        if (minAttackEv == maxAttackEv) {
            return findByAttackEv(minAttackEv);
        }

        List<Pokemon> result = pokemonRepo.findByAttackEvBetween(minAttackEv, maxAttackEv);

        if (result.isEmpty()) {
            throw new PokemonNotFoundException("No Pokemon found with Attack EVs between " + minAttackEv + " and " + maxAttackEv + ".");
        }

        return result;
    }

    public List<Pokemon> findByDefenseEv(int defenseEv) {
        validateEvRange(defenseEv, "Defense");

        List<Pokemon> result = pokemonRepo.findByDefenseEv(defenseEv);

        if (result.isEmpty()) {
            throw new PokemonNotFoundException("No Pokemon found with Defense EVs: " + defenseEv + ".");
        }

        return result;
    }

    public List<Pokemon> findByDefenseEvBetween(int minDefenseEv, int maxDefenseEv) {
        validateEvRangeBetween(minDefenseEv, maxDefenseEv, "Defense");

        if (minDefenseEv == maxDefenseEv) {
            return findByDefenseEv(minDefenseEv);
        }

        List<Pokemon> result = pokemonRepo.findByDefenseEvBetween(minDefenseEv, maxDefenseEv);

        if (result.isEmpty()) {
            throw new PokemonNotFoundException("No Pokemon found with Defense EVs between " + minDefenseEv + " and " + maxDefenseEv + ".");
        }

        return result;
    }

    public List<Pokemon> findBySpecialAttackEv(int specialAttackEv) {
        validateEvRange(specialAttackEv, "Special Attack");

        List<Pokemon> result = pokemonRepo.findBySpecialAttackEv(specialAttackEv);

        if (result.isEmpty()) {
            throw new PokemonNotFoundException("No Pokemon found with Special Attack EVs: " + specialAttackEv + ".");
        }

        return result;
    }

    public List<Pokemon> findBySpecialAttackEvBetween(int minSpecialAttackEv, int maxSpecialAttackEv) {
        validateEvRangeBetween(minSpecialAttackEv, maxSpecialAttackEv, "Special Attack");

        if (minSpecialAttackEv == maxSpecialAttackEv) {
            return findBySpecialAttackEv(minSpecialAttackEv);
        }

        List<Pokemon> result = pokemonRepo.findBySpecialAttackEvBetween(minSpecialAttackEv, maxSpecialAttackEv);

        if (result.isEmpty()) {
            throw new PokemonNotFoundException("No Pokemon found with Special Attack EVs between " + minSpecialAttackEv + " and " + maxSpecialAttackEv + ".");
        }

        return result;
    }

    public List<Pokemon> findBySpecialDefenseEv(int specialDefenseEv) {
        validateEvRange(specialDefenseEv, "Special Defense");

        List<Pokemon> result = pokemonRepo.findBySpecialDefenseEv(specialDefenseEv);

        if (result.isEmpty()) {
            throw new PokemonNotFoundException("No Pokemon found with Special Defense EVs: " + specialDefenseEv + ".");
        }

        return result;
    }

    public List<Pokemon> findBySpecialDefenseEvBetween(int minSpecialDefenseEv, int maxSpecialDefenseEv) {
        validateEvRangeBetween(minSpecialDefenseEv, maxSpecialDefenseEv, "Special Defense");

        if (minSpecialDefenseEv == maxSpecialDefenseEv) {
            return findBySpecialDefenseEv(minSpecialDefenseEv);
        }

        List<Pokemon> result = pokemonRepo.findBySpecialDefenseEvBetween(minSpecialDefenseEv, maxSpecialDefenseEv);

        if (result.isEmpty()) {
            throw new PokemonNotFoundException("No Pokemon found with Special Defense EVs between " + minSpecialDefenseEv + " and " + maxSpecialDefenseEv + ".");
        }

        return result;
    }

    public List<Pokemon> findBySpeedEv(int speedEv) {
        validateEvRange(speedEv, "Speed");

        List<Pokemon> result = pokemonRepo.findBySpeedEv(speedEv);

        if (result.isEmpty()) {
            throw new PokemonNotFoundException("No Pokemon found with Speed EVs: " + speedEv + ".");
        }

        return result;
    }

    public List<Pokemon> findBySpeedEvBetween(int minSpeedEv, int maxSpeedEv) {
        validateEvRangeBetween(minSpeedEv, maxSpeedEv, "Speed");

        if (minSpeedEv == maxSpeedEv) {
            return findBySpeedEv(minSpeedEv);
        }

        List<Pokemon> result = pokemonRepo.findBySpeedEvBetween(minSpeedEv, maxSpeedEv);

        if (result.isEmpty()) {
            throw new PokemonNotFoundException("No Pokemon found with Speed EVs between " + minSpeedEv + " and " + maxSpeedEv + ".");
        }

        return result;
    }

    public boolean existsById(Long id) {
        return pokemonRepo.existsById(id);
    }
}