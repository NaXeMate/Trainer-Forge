package dev.trainerforge.service;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Pattern;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import dev.trainerforge.dto.input.TrainerInputDto;
import dev.trainerforge.exception.InvalidFilterValueException;
import dev.trainerforge.exception.notfound.TrainerNotFoundException;
import dev.trainerforge.mapper.TrainerMapper;
import dev.trainerforge.model.entities.GamePossesion;
import dev.trainerforge.model.entities.Trainer;
import dev.trainerforge.model.enumerated.TrainerClass;
import dev.trainerforge.repository.GamePossesionRepository;
import dev.trainerforge.repository.TrainerRepository;

@Transactional(readOnly = true)
@Service
public class TrainerService {
    
    private final TrainerRepository trainerRepo;
    private final GamePossesionRepository gamePossesionRepo;

    private final TrainerMapper trainerMapper;

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    private static final Pattern FRIEND_CODE_PATTERN = Pattern.compile("^TF-\\d{4}-\\d{4}$");

    // private static final Pattern FRIEND_CODE_PATTERN = Pattern.compile("^TF-[A-Z0-9]{4}-[A-Z0-9]{4}$");
    // When TrainerForge scales up, it might allow letters in friend codes to increase the number of possible combinations. For now, it'll stick to digits for simplicity.

    private static final Long MAX_USERNAME_LENGTH = 30L;
    private static final Long MAX_REALNAME_LENGTH = 50L;
    private static final Long MAX_EMAIL_LENGTH = 254L;

    private static final Long REGION_MAX_ID = 10L;

    private boolean isValidFriendCode(String friendCode) {
        return friendCode != null && FRIEND_CODE_PATTERN.matcher(friendCode).matches();
    }

    private void validateUsername(String username) {
        if (username == null || username.isBlank()) {
            throw new InvalidFilterValueException("The username cannot be empty.");
        }
        
        if (username.length() > MAX_USERNAME_LENGTH) {
            throw new InvalidFilterValueException("The username exceeds the maximum allowed length.");
        }
    }

    private void validateEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new InvalidFilterValueException("The email cannot be empty.");
        }
        
        if (email.length() > MAX_EMAIL_LENGTH) {
            throw new InvalidFilterValueException("The email exceeds the maximum allowed length.");
        }
        
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            throw new InvalidFilterValueException("The email does not have a valid format.");
        }
    }

    private void validateRealName(String realName) {
        if (realName != null && realName.length() > MAX_REALNAME_LENGTH) {
            throw new InvalidFilterValueException("The real name exceeds the maximum allowed length.");
        }
    }

    private void validateUsernameUniqueness(String username) {
        if (trainerRepo.findByUsername(username).isPresent()) {
            throw new InvalidFilterValueException("A trainer with that username already exists.");
        }
    }

    private void validateEmailUniqueness(String email) {
        if (trainerRepo.findByEmail(email).isPresent()) {
            throw new InvalidFilterValueException("A trainer with that email already exists.");
        }
    }

    private String generateFriendCode() {
        int part1 = ThreadLocalRandom.current().nextInt(1000, 10000);
        int part2 = ThreadLocalRandom.current().nextInt(1000, 10000);
        return "TF-%04d-%04d".formatted(part1, part2);
    } 

    public TrainerService(TrainerRepository trainerRepo, GamePossesionRepository gamePossesionRepo, TrainerMapper trainerMapper) {
        this.trainerRepo = trainerRepo;
        this.gamePossesionRepo = gamePossesionRepo;
        this.trainerMapper = trainerMapper;
    }

    @Transactional
    public Trainer createTrainer(TrainerInputDto dto) {
        validateUsername(dto.username());
        validateUsernameUniqueness(dto.username());
        validateEmail(dto.email());
        validateEmailUniqueness(dto.email());
        validateRealName(dto.realName());
        
        String friendCode;
        do {
            friendCode = generateFriendCode();
        } while (trainerRepo.findByFriendCode(friendCode).isPresent());
        
        Trainer newTrainer = new Trainer();
        trainerMapper.updateEntityFromDto(dto, newTrainer);
        
        newTrainer.setFriendCode(friendCode);
        
        if (dto.password() == null || dto.password().isBlank()) {
            throw new InvalidFilterValueException("The password cannot be empty.");
        }
        newTrainer.setPasswordHash(dto.password());
        return trainerRepo.save(newTrainer);
    }
    
    @Transactional
    public Trainer updateTrainer(Long id, TrainerInputDto dto) {
        Trainer trainer = this.findById(id);
        validateUsername(dto.username());
        validateEmail(dto.email());
        validateRealName(dto.realName());
        
        if (!trainer.getUsername().equals(dto.username())) {
            validateUsernameUniqueness(dto.username());
        }
        if (!trainer.getEmail().equals(dto.email())) {
            validateEmailUniqueness(dto.email());
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
        validateUsername(username);

        return trainerRepo.findByUsername(username)
        .orElseThrow(() -> new TrainerNotFoundException("Trainer not found with username: " + username + "."));
    }

    public Trainer findByEmail(String email) {
        validateEmail(email);

        return trainerRepo.findByEmail(email)
        .orElseThrow(() -> new TrainerNotFoundException("Trainer not found with email: " + email + "."));
    }

    public Trainer findByRealName(String realName) {
        validateRealName(realName);


        
        return trainerRepo.findByRealName(realName)
        .orElseThrow(() -> new TrainerNotFoundException("Trainer not found with real name: " + realName + "."));
    }

    public Trainer findByFriendCode(String friendCode) {
        if (friendCode == null || friendCode.isBlank()) {
            throw new InvalidFilterValueException("The friend code cannot be empty.");
        }
        
        if (!isValidFriendCode(friendCode)) {
            throw new InvalidFilterValueException("The friend code does not have a valid format. It should be in the format 'TF-XXXX-XXXX'.");
        }
        
        return trainerRepo.findByFriendCode(friendCode)
        .orElseThrow(() -> new TrainerNotFoundException("Trainer not found with friend code: " + friendCode + "."));
    }

    public List<Trainer> findByRegionId(Long regionId) {
        if (regionId < 1 || regionId > REGION_MAX_ID) {
            throw new InvalidFilterValueException("Region ID must be between 1 and " + REGION_MAX_ID + ".");
        }

        List<Trainer> result = trainerRepo.findByRegionId(regionId);

        if (result.isEmpty()) {
            throw new TrainerNotFoundException("No trainers found in region with id: " + regionId + ".");
        }

        return result;
    }

    public List<Trainer> findByFavoriteGameId(Long favoriteGameId) {
        List<Trainer> result = trainerRepo.findByFavoriteGameId(favoriteGameId);

        if (result.isEmpty()) {
            throw new TrainerNotFoundException("No trainers found with favorite game id: " + favoriteGameId + ".");
        }

        return result;
    }

    public List<Trainer> findByFavoritePokemonId(Long favoritePokemonId) {
        List<Trainer> result = trainerRepo.findByFavoritePokemonId(favoritePokemonId);

        if (result.isEmpty()) {
            throw new TrainerNotFoundException("No trainers found with favorite Pokémon id: " + favoritePokemonId + ".");
        }

        return result;
    }

    public List<Trainer> findByBestFriendId(Long bestFriendId) {
        List<Trainer> result = trainerRepo.findByBestFriendId(bestFriendId);

        if (result.isEmpty()) {
            throw new TrainerNotFoundException("No trainers found with best friend id: " + bestFriendId + ".");
        }

        return result;
    }

    public List<Trainer> findByTrainerClass(TrainerClass trainerClass) {
        List<Trainer> result = trainerRepo.findByTrainerClass(trainerClass);

        if (result.isEmpty()) {
            throw new TrainerNotFoundException("No trainers found with trainer class: " + trainerClass + ".");
        }

        return result;
    }    

    public boolean existsById(Long id) {
        return trainerRepo.existsById(id);
    }

    public List<GamePossesion> findGamesByTrainerId(Long trainerId) {

        if (!trainerRepo.existsById(trainerId)) {
            throw new TrainerNotFoundException(trainerId);
        }

        List<GamePossesion> result = gamePossesionRepo.findByTrainerId(trainerId);

        if (result.isEmpty()) {
            throw new TrainerNotFoundException("No game possessions found for trainer with id: " + trainerId + ".");
        }

        return result;
    }

    public List<GamePossesion> findTrainersByVideogameId(Long videogameId) {
        List<GamePossesion> result = gamePossesionRepo.findByVideogameId(videogameId);

        if (result.isEmpty()) {
            throw new TrainerNotFoundException("No trainers found with videogame id: " + videogameId + ".");
        }

        return result;
    }
}
