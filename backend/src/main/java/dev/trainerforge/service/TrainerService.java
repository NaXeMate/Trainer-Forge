package dev.trainerforge.service;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Pattern;

import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import tools.jackson.databind.JsonNode;

import dev.trainerforge.dto.input.TrainerInputDto;
import dev.trainerforge.dto.response.TrainerPrivateDto;
import dev.trainerforge.exception.InvalidFilterValueException;
import dev.trainerforge.exception.TrainerNameConflictException;
import dev.trainerforge.exception.notfound.TrainerNotFoundException;
import dev.trainerforge.mapper.TrainerMapper;
import dev.trainerforge.model.entities.GamePossession;
import dev.trainerforge.model.entities.Pokedex;
import dev.trainerforge.model.entities.Trainer;
import dev.trainerforge.model.entities.Videogame;
import dev.trainerforge.model.enumerated.TrainerClass;
import dev.trainerforge.repository.GamePossessionRepository;
import dev.trainerforge.repository.PokedexRepository;
import dev.trainerforge.repository.TrainerRepository;
import dev.trainerforge.repository.VideogameRepository;
import dev.trainerforge.validator.TrainerValidator;

@Transactional(readOnly = true)
@Service
public class TrainerService {

    private static final Pattern SPRITE_PATH = Pattern.compile("/trainer-sprites/[A-Za-z0-9_-]+\\.png");
    private final TrainerRepository trainerRepo;
    private final GamePossessionRepository gamePossessionRepo;
    private final PokedexRepository pokedexRepo;
    private final VideogameRepository videogameRepo;

    private final TrainerMapper trainerMapper;
    private final PasswordEncoder passwordEncoder;

    private String generateFriendCode() {
        int part1 = ThreadLocalRandom.current().nextInt(1000, 10000);
        int part2 = ThreadLocalRandom.current().nextInt(1000, 10000);
        return "TF-%04d-%04d".formatted(part1, part2);
    } 

    public TrainerService(TrainerRepository trainerRepo, GamePossessionRepository gamePossessionRepo,
            PokedexRepository pokedexRepo, VideogameRepository videogameRepo, TrainerMapper trainerMapper,
            PasswordEncoder passwordEncoder) {
        this.trainerRepo = trainerRepo;
        this.gamePossessionRepo = gamePossessionRepo;
        this.pokedexRepo = pokedexRepo;
        this.videogameRepo = videogameRepo;
        this.trainerMapper = trainerMapper;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Creates a trainer after validating profile fields and uniqueness constraints.
     *
     * A unique friend code is generated in a retry loop before persistence.
     *
     * @param dto payload containing trainer profile data.
     * @return the newly persisted trainer.
     * @throws InvalidFilterValueException when mandatory fields are invalid, duplicated, or missing.
     */
    @Transactional
    public Trainer createTrainer(TrainerInputDto dto) {
        TrainerValidator.validateUsername(dto.username());
        TrainerValidator.validateUsernameUniqueness(trainerRepo.existsByUsername(dto.username()));
        TrainerValidator.validateEmail(dto.email());
        TrainerValidator.validateEmailUniqueness(trainerRepo.existsByEmail(dto.email()));
        String realName = normalizeRealName(dto.realName());
        TrainerValidator.validateRealName(realName);
        TrainerValidator.validatePassword(dto.password());
        
        String friendCode;
        do {
            friendCode = generateFriendCode();
        } while (trainerRepo.existsByFriendCode(friendCode));
        
        Trainer newTrainer = new Trainer();
        trainerMapper.updateEntityFromDto(dto, newTrainer);
        newTrainer.setRealName(realName);
        if (newTrainer.getTrainerClass() == null) {
            newTrainer.setTrainerClass(TrainerClass.NOVICE);
        }
        
        newTrainer.setFriendCode(friendCode);
        
        newTrainer.setPasswordHash(passwordEncoder.encode(dto.password()));
        return saveWithNameConflict(newTrainer);
    }
    
    /**
     * Updates a trainer while preserving uniqueness rules for username and email.
     *
     * @param id identifier of the trainer to update.
     * @param dto payload containing updated trainer profile data.
     * @return the updated trainer entity.
     * @throws TrainerNotFoundException when no trainer exists for the provided identifier.
     * @throws InvalidFilterValueException when the new data violates format or uniqueness constraints.
     */
    @Transactional
    public Trainer updateTrainer(Long id, TrainerInputDto dto) {
        Trainer trainer = this.findById(id);
        TrainerValidator.validateUsername(dto.username());
        TrainerValidator.validateEmail(dto.email());
        TrainerValidator.validateRealName(dto.realName());

        if (!trainer.getUsername().equals(dto.username())) {
            throw new InvalidFilterValueException("The username cannot be changed.");
        }
        if (!trainer.getEmail().equals(dto.email())) {
            TrainerValidator.validateEmailUniqueness(trainerRepo.existsByEmail(dto.email()));
        }
        
        trainerMapper.updateEntityFromDto(dto, trainer);
        
        return trainerRepo.save(trainer);
    }

    public TrainerPrivateDto getCurrentTrainer(String username) {
        return trainerMapper.toPrivateDto(findAuthenticatedTrainer(username));
    }

    @Transactional
    public TrainerPrivateDto updateCurrentTrainer(String username, JsonNode changes) {
        if (changes == null || !changes.isObject()) {
            throw new InvalidFilterValueException("Profile changes must be a JSON object.");
        }

        Trainer trainer = findAuthenticatedTrainer(username);
        
        for (var field : changes.properties()) {
            JsonNode value = field.getValue();
            switch (field.getKey()) {
                case "realName" -> {
                    String name = normalizeRealName(readText(value, "realName"));
                    TrainerValidator.validateRealName(name);
                    trainer.setRealName(name);
                }
                
                case "trainerClass" -> trainer.setTrainerClass(readTrainerClass(value));
                
                case "profilePictureUrl" -> {
                    String sprite = readText(value, "profilePictureUrl");
                    if (sprite != null && (sprite.length() > 255 || !SPRITE_PATH.matcher(sprite).matches())) {
                        throw new InvalidFilterValueException("Choose a trainer sprite from the catalogue.");
                    }
                    trainer.setProfilePictureUrl(sprite);
                }
                
                case "favoritePokemonId" -> trainer.setFavoritePokemon(readFavoritePokemon(value));
                
                case "favoriteGameName" -> trainer.setFavoriteGame(readFavoriteGame(value));
                
                default -> throw new InvalidFilterValueException("This request contains a field that cannot be edited.");
            }
        }
        return trainerMapper.toPrivateDto(saveWithNameConflict(trainer));
    }

    private Trainer findAuthenticatedTrainer(String username) {
        try {
            return findByUsername(username);
        } catch (TrainerNotFoundException ex) {
            throw new UsernameNotFoundException("Trainer account no longer exists.", ex);
        }
    }

    private static String normalizeRealName(String name) {
        return name == null || name.isBlank() ? null : name.strip();
    }

    private static String readText(JsonNode value, String field) {
        if (value.isNull()) return null;
        
        if (!value.isString()) {
            throw new InvalidFilterValueException(field + " must be text or null.");
        }
        
        return value.stringValue();
    }

    private static TrainerClass readTrainerClass(JsonNode value) {
        String trainerClass = readText(value, "trainerClass");
        
        if (trainerClass == null) {
            throw new InvalidFilterValueException("The trainer title is required.");
        }
        
        try {
            return TrainerClass.valueOf(trainerClass);
        } catch (IllegalArgumentException ex) {
            throw new InvalidFilterValueException("Choose a valid trainer title.");
        }
    }

    private Pokedex readFavoritePokemon(JsonNode value) {
        if (value.isNull()) return null;
        
        if (!value.isIntegralNumber() || !value.canConvertToLong() || value.longValue() <= 0) {
            throw new InvalidFilterValueException("The favorite Pokémon must use a valid Pokédex entry ID.");
        }
        
        return pokedexRepo.findById(value.longValue())
                .orElseThrow(() -> new InvalidFilterValueException("The selected Pokémon does not exist."));
    }

    private Videogame readFavoriteGame(JsonNode value) {
        String name = readText(value, "favoriteGameName");
        
        if (name == null) return null;
        
        return videogameRepo.findByName(name)
                .orElseThrow(() -> new InvalidFilterValueException("The selected game does not exist."));
    }

    private Trainer saveWithNameConflict(Trainer trainer) {
        try {
            return trainerRepo.saveAndFlush(trainer);
        } catch (DataIntegrityViolationException ex) {
            if (ex.getCause() instanceof ConstraintViolationException violation
                    && "trainers_real_name_key".equals(violation.getConstraintName())) {
                throw new TrainerNameConflictException();
            }
            throw ex;
        }
    }

    @Transactional
    public void deleteTrainer(Long id) {
        Trainer trainer = this.findById(id);
        trainerRepo.delete(trainer);
        System.out.println("Deleted Trainer with id: " + id);
    }
    
    public List<Trainer> findAll() {
        return trainerRepo.findAll();
    }

    public Trainer findById(Long id) {
        return trainerRepo.findById(id)
        .orElseThrow(() -> new TrainerNotFoundException(id));
    }

    public Trainer findByUsername(String username) {
        TrainerValidator.validateUsername(username);

        return trainerRepo.findByUsername(username)
        .orElseThrow(() -> new TrainerNotFoundException("Trainer not found with username: " + username + "."));
    }

    public Trainer findByEmail(String email) {
        TrainerValidator.validateEmail(email);

        return trainerRepo.findByEmail(email)
        .orElseThrow(() -> new TrainerNotFoundException("Trainer not found with email: " + email + "."));
    }

    public Trainer findByRealName(String realName) {
        TrainerValidator.validateRealName(realName);


        
        return trainerRepo.findByRealName(realName)
        .orElseThrow(() -> new TrainerNotFoundException("Trainer not found with real name: " + realName + "."));
    }

    /**
     * Resolves a trainer by friend code after validating TrainerForge friend code format.
     *
     * @param friendCode TrainerForge friend code expected in TF-XXXX-XXXX format.
     * @return the trainer associated with the provided friend code.
     * @throws InvalidFilterValueException when the friend code is blank or does not match the expected format.
     * @throws TrainerNotFoundException when no trainer uses the provided friend code.
     */
    public Trainer findByFriendCode(String friendCode) {
        TrainerValidator.validateFriendCode(friendCode);

        return trainerRepo.findByFriendCode(friendCode)
        .orElseThrow(() -> new TrainerNotFoundException("Trainer not found with friend code: " + friendCode + "."));
    }

    /**
     * Retrieves trainers from a region after validating region boundaries.
     *
     * @param regionId identifier of the region used to filter trainers.
     * @return all trainers registered in the requested region.
     * @throws InvalidFilterValueException when the region identifier is outside the supported range.
     * @throws TrainerNotFoundException when no trainers are found for the provided region.
     */
    public List<Trainer> findByRegionId(Long regionId) {
        TrainerValidator.validateRegionId(regionId);

        List<Trainer> result = trainerRepo.findByRegionId(regionId);
        TrainerValidator.validateByRegionResult(result, regionId);

        return result;
    }

    public List<Trainer> findByFavoriteGameId(Long favoriteGameId) {
        List<Trainer> result = trainerRepo.findByFavoriteGameId(favoriteGameId);
        TrainerValidator.validateByFavoriteGameResult(result, favoriteGameId);

        return result;
    }

    public List<Trainer> findByFavoritePokemonId(Long favoritePokemonId) {
        List<Trainer> result = trainerRepo.findByFavoritePokemonId(favoritePokemonId);
        TrainerValidator.validateByFavoritePokemonResult(result, favoritePokemonId);

        return result;
    }

    public List<Trainer> findByBestFriendId(Long bestFriendId) {
        List<Trainer> result = trainerRepo.findByBestFriendId(bestFriendId);
        TrainerValidator.validateByBestFriendResult(result, bestFriendId);

        return result;
    }

    public List<Trainer> findByTrainerClass(TrainerClass trainerClass) {
        List<Trainer> result = trainerRepo.findByTrainerClass(trainerClass);
        TrainerValidator.validateByTrainerClassResult(result, trainerClass);

        return result;
    }    

    public boolean existsById(Long id) {
        return trainerRepo.existsById(id);
    }

    /**
     * Retrieves all game possession records linked to a trainer.
     *
     * @param trainerId identifier of the trainer whose game possession records are requested.
     * @return all game possession entries for the provided trainer.
     * @throws TrainerNotFoundException when the trainer does not exist or has no possession records.
     */
    public List<GamePossession> findGamesByTrainerId(Long trainerId) {
        TrainerValidator.validateTrainerExists(trainerRepo.existsById(trainerId), trainerId);

        List<GamePossession> result = gamePossessionRepo.findByTrainerId(trainerId);
        TrainerValidator.validateByTrainerGamesResult(result, trainerId);

        return result;
    }

    /**
     * Retrieves trainer possession records for a specific videogame.
     *
     * @param videogameId identifier of the videogame used to filter possession records.
     * @return all game possession entries that reference the provided videogame.
     * @throws TrainerNotFoundException when no trainer possession records exist for the videogame.
     */
    public List<GamePossession> findTrainersByVideogameId(Long videogameId) {
        List<GamePossession> result = gamePossessionRepo.findByVideogameId(videogameId);
        TrainerValidator.validateByVideogameResult(result, videogameId);

        return result;
    }
}
