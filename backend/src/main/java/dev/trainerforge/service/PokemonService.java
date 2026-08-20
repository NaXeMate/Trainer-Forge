package dev.trainerforge.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import dev.trainerforge.dto.response.PokemonDto;
import dev.trainerforge.exception.notfound.PokemonNotFoundException;
import dev.trainerforge.mapper.PokemonMapper;
import dev.trainerforge.model.entities.Nature;
import dev.trainerforge.model.entities.Pokedex;
import dev.trainerforge.model.entities.Pokemon;
import dev.trainerforge.model.enumerated.Gender;
import dev.trainerforge.repository.PokemonRepository;
import dev.trainerforge.validator.PokemonValidator;

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

    /**
     * Resolves relation fields from DTO identifiers and names into managed entity references.
     *
     * @param dto payload containing relation identifiers and names to resolve.
     * @param pokemon pokemon entity that receives resolved relation references.
     * @param species species already resolved while validating the payload.
     */
    private void updateRelationsFromDto(PokemonDto dto, Pokemon pokemon, Pokedex species) {
        pokemon.setSpecies(species);
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

    private Pokedex validateAndResolveSpecies(PokemonDto dto) {
        Optional<Pokedex> species = pokedexService.findOptionalById(dto.species());
        PokemonValidator.validatePokemonFromDto(dto, species.isPresent());
        return species.orElseThrow();
    }

    /**
     * Creates a pokemon from DTO data, validating business rules and resolving entity relations.
     *
     * If nickname is blank, the species name is used as a default nickname before persistence.
     *
     * @param dto payload containing pokemon attributes and relation references.
     * @return the newly persisted pokemon entity.
     * @throws InvalidFilterValueException when DTO values violate required constraints or uniqueness rules for move slots.
     */
    @Transactional
    public Pokemon createPokemon(PokemonDto dto) {
        Pokedex species = validateAndResolveSpecies(dto);

        Pokemon newPokemon = new Pokemon();
        pokemonMapper.updateEntityFromDto(dto, newPokemon);
        updateRelationsFromDto(dto, newPokemon, species);
        
        // The Pokemon species name will be used as default "nickname" if it's not provided
        if (dto.nickname() == null || dto.nickname().isBlank()) {
            newPokemon.setNickname(species.getName());
        }

        return pokemonRepo.save(newPokemon);
    }

    /**
     * Updates a pokemon with DTO data after applying the same validation and relation resolution used on creation.
     *
     * @param id identifier of the pokemon to update.
     * @param dto payload containing new pokemon values.
     * @return the updated pokemon entity.
     * @throws PokemonNotFoundException when the pokemon identifier does not exist.
     * @throws InvalidFilterValueException when DTO values violate domain constraints.
     */
    @Transactional
    public Pokemon updatePokemon(Long id, PokemonDto dto) {
        Pokemon pokemon = this.findById(id);
        Pokedex species = validateAndResolveSpecies(dto);

        pokemonMapper.updateEntityFromDto(dto, pokemon);
        updateRelationsFromDto(dto, pokemon, species);

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
        PokemonValidator.validateSpecies(pokedexService.existsById(speciesId), speciesId);
        return pokemonRepo.findBySpeciesId(speciesId);
    }

    /**
     * Retrieves pokemon by nickname after validating non-empty nickname input.
     *
     * @param nickname nickname used as filter criteria.
     * @return all pokemon that use the provided nickname.
     * @throws InvalidFilterValueException when the nickname is null or blank.
     * @throws PokemonNotFoundException when no pokemon match the provided nickname.
     */
    public List<Pokemon> findByNickname(String nickname) {
        PokemonValidator.validateNickname(nickname);

        List<Pokemon> result = pokemonRepo.findByNickname(nickname);
        PokemonValidator.validateByNicknameResult(result, nickname);

        return result;
    }

    /**
     * Retrieves pokemon captured at a specific location.
     *
     * @param locationFound capture location text used as filter criteria.
     * @return all pokemon registered with the provided capture location.
     * @throws InvalidFilterValueException when the location text is null or blank.
     * @throws PokemonNotFoundException when no pokemon are registered at the provided location.
     */
    public List<Pokemon> findByLocationFound(String locationFound) {
        PokemonValidator.validateLocationFound(locationFound);

        List<Pokemon> result = pokemonRepo.findByLocationFound(locationFound);
        PokemonValidator.validateByLocationResult(result, locationFound);

        return result;
    }

    public List<Pokemon> findByLevel(int level) {
        PokemonValidator.validateLevel(level);

        return pokemonRepo.findByLevel(level);
    }

    /**
     * Retrieves pokemon whose levels are within an inclusive interval.
     *
     * When both bounds are equal, this method delegates to exact-level filtering.
     *
     * @param minLevel lower level bound used for filtering.
     * @param maxLevel upper level bound used for filtering.
     * @return all pokemon whose level is inside the provided range.
     * @throws InvalidFilterValueException when a bound is outside allowed levels or interval order is invalid.
     */
    public List<Pokemon> findByLevelBetween(int minLevel, int maxLevel) {
        PokemonValidator.validateLevelBetween(minLevel, maxLevel);
        
        if (PokemonValidator.isExactRange(minLevel, maxLevel)) {
            return findByLevel(minLevel);
        }

        return pokemonRepo.findByLevelBetween(minLevel, maxLevel);
    }

    public List<Pokemon> findByShiny(boolean shiny) {
        List<Pokemon> result = pokemonRepo.findByShiny(shiny);
        PokemonValidator.validateByShinyResult(result);

        return result;
    }


    public List<Pokemon> findByGender(Gender gender) {
        PokemonValidator.validateGender(gender);

        List<Pokemon> result = pokemonRepo.findByGender(gender);
        PokemonValidator.validateByGenderResult(result, gender);

        return result;
    }

    public List<Pokemon> findByAbilityId(Long abilityId) {
        List<Pokemon> result = pokemonRepo.findByAbilityId(abilityId);
        PokemonValidator.validateByAbilityResult(result, abilityId);

        return result;
    }

    public List<Pokemon> findByMoveId(Long moveId) {
        List<Pokemon> result = pokemonRepo.findByMoveId(moveId);
        PokemonValidator.validateByMoveResult(result, moveId);

        return result;
    }

    public List<Pokemon> findByEquippedItemId(Long itemId) {
        List<Pokemon> result = pokemonRepo.findByEquippedItemId(itemId);
        PokemonValidator.validateByEquippedItemResult(result, itemId);

        return result;
    }

    public List<Pokemon> findByNature(Nature nature) {
        List<Pokemon> result = pokemonRepo.findByNature(nature);
        PokemonValidator.validateByNatureResult(result, nature);

        return result;
    }

    public List<Pokemon> findByHpEv(int hpEv) {
        PokemonValidator.validateEvRange(hpEv, "HP");

        List<Pokemon> result = pokemonRepo.findByHpEv(hpEv);
        PokemonValidator.validateByEvResult(result, "HP", hpEv);

        return result;
    }

    public List<Pokemon> findByHpEvBetween(int minHpEv, int maxHpEv) {
        PokemonValidator.validateEvRangeBetween(minHpEv, maxHpEv, "HP");

        if (PokemonValidator.isExactRange(minHpEv, maxHpEv)) {
            return findByHpEv(minHpEv);
        }

        List<Pokemon> result = pokemonRepo.findByHpEvBetween(minHpEv, maxHpEv);
        PokemonValidator.validateByEvRangeResult(result, "HP", minHpEv, maxHpEv);

        return result;
    }

    public List<Pokemon> findByAttackEv(int attackEv) {
        PokemonValidator.validateEvRange(attackEv, "Attack");

        List<Pokemon> result = pokemonRepo.findByAttackEv(attackEv);
        PokemonValidator.validateByEvResult(result, "Attack", attackEv);

        return result;
    }

    public List<Pokemon> findByAttackEvBetween(int minAttackEv, int maxAttackEv) {
        PokemonValidator.validateEvRangeBetween(minAttackEv, maxAttackEv, "Attack");

        if (PokemonValidator.isExactRange(minAttackEv, maxAttackEv)) {
            return findByAttackEv(minAttackEv);
        }

        List<Pokemon> result = pokemonRepo.findByAttackEvBetween(minAttackEv, maxAttackEv);
        PokemonValidator.validateByEvRangeResult(result, "Attack", minAttackEv, maxAttackEv);

        return result;
    }

    public List<Pokemon> findByDefenseEv(int defenseEv) {
        PokemonValidator.validateEvRange(defenseEv, "Defense");

        List<Pokemon> result = pokemonRepo.findByDefenseEv(defenseEv);
        PokemonValidator.validateByEvResult(result, "Defense", defenseEv);

        return result;
    }

    public List<Pokemon> findByDefenseEvBetween(int minDefenseEv, int maxDefenseEv) {
        PokemonValidator.validateEvRangeBetween(minDefenseEv, maxDefenseEv, "Defense");

        if (PokemonValidator.isExactRange(minDefenseEv, maxDefenseEv)) {
            return findByDefenseEv(minDefenseEv);
        }

        List<Pokemon> result = pokemonRepo.findByDefenseEvBetween(minDefenseEv, maxDefenseEv);
        PokemonValidator.validateByEvRangeResult(result, "Defense", minDefenseEv, maxDefenseEv);

        return result;
    }

    public List<Pokemon> findBySpecialAttackEv(int specialAttackEv) {
        PokemonValidator.validateEvRange(specialAttackEv, "Special Attack");

        List<Pokemon> result = pokemonRepo.findBySpecialAttackEv(specialAttackEv);
        PokemonValidator.validateByEvResult(result, "Special Attack", specialAttackEv);

        return result;
    }

    public List<Pokemon> findBySpecialAttackEvBetween(int minSpecialAttackEv, int maxSpecialAttackEv) {
        PokemonValidator.validateEvRangeBetween(minSpecialAttackEv, maxSpecialAttackEv, "Special Attack");

        if (PokemonValidator.isExactRange(minSpecialAttackEv, maxSpecialAttackEv)) {
            return findBySpecialAttackEv(minSpecialAttackEv);
        }

        List<Pokemon> result = pokemonRepo.findBySpecialAttackEvBetween(minSpecialAttackEv, maxSpecialAttackEv);
        PokemonValidator.validateByEvRangeResult(result, "Special Attack", minSpecialAttackEv, maxSpecialAttackEv);

        return result;
    }

    public List<Pokemon> findBySpecialDefenseEv(int specialDefenseEv) {
        PokemonValidator.validateEvRange(specialDefenseEv, "Special Defense");

        List<Pokemon> result = pokemonRepo.findBySpecialDefenseEv(specialDefenseEv);
        PokemonValidator.validateByEvResult(result, "Special Defense", specialDefenseEv);

        return result;
    }

    public List<Pokemon> findBySpecialDefenseEvBetween(int minSpecialDefenseEv, int maxSpecialDefenseEv) {
        PokemonValidator.validateEvRangeBetween(minSpecialDefenseEv, maxSpecialDefenseEv, "Special Defense");

        if (PokemonValidator.isExactRange(minSpecialDefenseEv, maxSpecialDefenseEv)) {
            return findBySpecialDefenseEv(minSpecialDefenseEv);
        }

        List<Pokemon> result = pokemonRepo.findBySpecialDefenseEvBetween(minSpecialDefenseEv, maxSpecialDefenseEv);
        PokemonValidator.validateByEvRangeResult(result, "Special Defense", minSpecialDefenseEv, maxSpecialDefenseEv);

        return result;
    }

    public List<Pokemon> findBySpeedEv(int speedEv) {
        PokemonValidator.validateEvRange(speedEv, "Speed");

        List<Pokemon> result = pokemonRepo.findBySpeedEv(speedEv);
        PokemonValidator.validateByEvResult(result, "Speed", speedEv);

        return result;
    }

    public List<Pokemon> findBySpeedEvBetween(int minSpeedEv, int maxSpeedEv) {
        PokemonValidator.validateEvRangeBetween(minSpeedEv, maxSpeedEv, "Speed");

        if (PokemonValidator.isExactRange(minSpeedEv, maxSpeedEv)) {
            return findBySpeedEv(minSpeedEv);
        }

        List<Pokemon> result = pokemonRepo.findBySpeedEvBetween(minSpeedEv, maxSpeedEv);
        PokemonValidator.validateByEvRangeResult(result, "Speed", minSpeedEv, maxSpeedEv);

        return result;
    }

    public boolean existsById(Long id) {
        return pokemonRepo.existsById(id);
    }
}
