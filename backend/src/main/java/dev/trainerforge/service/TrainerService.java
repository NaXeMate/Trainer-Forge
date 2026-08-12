package dev.trainerforge.service;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import dev.trainerforge.dto.input.TrainerInputDto;
import dev.trainerforge.exception.notfound.TrainerNotFoundException;
import dev.trainerforge.mapper.TrainerMapper;
import dev.trainerforge.model.entities.GamePossession;
import dev.trainerforge.model.entities.Trainer;
import dev.trainerforge.model.enumerated.TrainerClass;
import dev.trainerforge.repository.GamePossessionRepository;
import dev.trainerforge.repository.TrainerRepository;
import dev.trainerforge.validator.TrainerValidator;

@Transactional(readOnly = true)
@Service
public class TrainerService {
    
    private final TrainerRepository trainerRepo;
    private final GamePossessionRepository gamePossessionRepo;

    private final TrainerMapper trainerMapper;
    private final PasswordEncoder passwordEncoder;

    private String generateFriendCode() {
        int part1 = ThreadLocalRandom.current().nextInt(1000, 10000);
        int part2 = ThreadLocalRandom.current().nextInt(1000, 10000);
        return "TF-%04d-%04d".formatted(part1, part2);
    } 

    public TrainerService(TrainerRepository trainerRepo, GamePossessionRepository gamePossessionRepo, TrainerMapper trainerMapper, PasswordEncoder passwordEncoder) {
        this.trainerRepo = trainerRepo;
        this.gamePossessionRepo = gamePossessionRepo;
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
        TrainerValidator.validateUsernameUniqueness(trainerRepo.findByUsername(dto.username()).isPresent());
        TrainerValidator.validateEmail(dto.email());
        TrainerValidator.validateEmailUniqueness(trainerRepo.findByEmail(dto.email()).isPresent());
        TrainerValidator.validateRealName(dto.realName());
        TrainerValidator.validatePassword(dto.password());
        
        String friendCode;
        do {
            friendCode = generateFriendCode();
        } while (trainerRepo.findByFriendCode(friendCode).isPresent());
        
        Trainer newTrainer = new Trainer();
        trainerMapper.updateEntityFromDto(dto, newTrainer);
        
        newTrainer.setFriendCode(friendCode);
        
        newTrainer.setPasswordHash(passwordEncoder.encode(dto.password()));
        return trainerRepo.save(newTrainer);
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
            TrainerValidator.validateUsernameUniqueness(trainerRepo.findByUsername(dto.username()).isPresent());
        }
        if (!trainer.getEmail().equals(dto.email())) {
            TrainerValidator.validateEmailUniqueness(trainerRepo.findByEmail(dto.email()).isPresent());
        }
        
        trainerMapper.updateEntityFromDto(dto, trainer);
        
        return trainerRepo.save(trainer);
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
