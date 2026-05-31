package dev.trainerforge.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dev.trainerforge.dto.input.TrainerInputDto;
import dev.trainerforge.dto.response.GamePossessionDto;
import dev.trainerforge.dto.response.TrainerDto;
import dev.trainerforge.mapper.GamePossessionMapper;
import dev.trainerforge.mapper.TrainerMapper;
import dev.trainerforge.model.entities.Trainer;
import dev.trainerforge.model.enumerated.TrainerClass;
import dev.trainerforge.service.TrainerService;

@RestController
@RequestMapping("/api/trainers")
public class TrainerController {

    private final TrainerService trainerService;
    private final TrainerMapper trainerMapper;
    private final GamePossessionMapper gamePossessionMapper;

    public TrainerController(TrainerService trainerService, TrainerMapper trainerMapper, GamePossessionMapper gamePossessionMapper) {
        this.trainerService = trainerService;
        this.trainerMapper = trainerMapper;
        this.gamePossessionMapper = gamePossessionMapper;
    }

    // GETs

    @GetMapping
    public ResponseEntity<List<TrainerDto>> getAllTrainers() {
        List<TrainerDto> trainers = trainerService.findAll()
                .stream()
                .map(trainerMapper::toDto)
                .toList();
        return ResponseEntity.ok(trainers);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TrainerDto> getTrainerById(@PathVariable Long id) {
        TrainerDto trainer = trainerMapper.toDto(trainerService.findById(id));
        return ResponseEntity.ok(trainer);
    }

    @GetMapping("/username/{username}")
    public ResponseEntity<TrainerDto> getTrainerByUsername(@PathVariable String username) {
        TrainerDto trainer = trainerMapper.toDto(trainerService.findByUsername(username));
        return ResponseEntity.ok(trainer);
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<TrainerDto> getTrainerByEmail(@PathVariable String email) {
        TrainerDto trainer = trainerMapper.toDto(trainerService.findByEmail(email));
        return ResponseEntity.ok(trainer);
    }

    @GetMapping("/real-name/{realName}")
    public ResponseEntity<TrainerDto> getTrainerByRealName(@PathVariable String realName) {
        TrainerDto trainer = trainerMapper.toDto(trainerService.findByRealName(realName));
        return ResponseEntity.ok(trainer);
    }

    @GetMapping("/friend-code/{friendCode}")
    public ResponseEntity<TrainerDto> getTrainerByFriendCode(@PathVariable String friendCode) {
        TrainerDto trainer = trainerMapper.toDto(trainerService.findByFriendCode(friendCode));
        return ResponseEntity.ok(trainer);
    }

    @GetMapping("/region/{regionId}")
    public ResponseEntity<List<TrainerDto>> getTrainersByRegion(@PathVariable Long regionId) {
        List<TrainerDto> trainers = trainerService.findByRegionId(regionId)
                .stream()
                .map(trainerMapper::toDto)
                .toList();
        return ResponseEntity.ok(trainers);
    }

    @GetMapping("/favorite-game/{favoriteGameId}")
    public ResponseEntity<List<TrainerDto>> getTrainersByFavoriteGame(@PathVariable Long favoriteGameId) {
        List<TrainerDto> trainers = trainerService.findByFavoriteGameId(favoriteGameId)
                .stream()
                .map(trainerMapper::toDto)
                .toList();
        return ResponseEntity.ok(trainers);
    }

    @GetMapping("/favorite-pokemon/{favoritePokemonId}")
    public ResponseEntity<List<TrainerDto>> getTrainersByFavoritePokemon(@PathVariable Long favoritePokemonId) {
        List<TrainerDto> trainers = trainerService.findByFavoritePokemonId(favoritePokemonId)
                .stream()
                .map(trainerMapper::toDto)
                .toList();
        return ResponseEntity.ok(trainers);
    }

    @GetMapping("/best-friend/{bestFriendId}")
    public ResponseEntity<List<TrainerDto>> getTrainersByBestFriend(@PathVariable Long bestFriendId) {
        List<TrainerDto> trainers = trainerService.findByBestFriendId(bestFriendId)
                .stream()
                .map(trainerMapper::toDto)
                .toList();
        return ResponseEntity.ok(trainers);
    }

    @GetMapping("/class/{trainerClass}")
    public ResponseEntity<List<TrainerDto>> getTrainersByClass(@PathVariable TrainerClass trainerClass) {
        List<TrainerDto> trainers = trainerService.findByTrainerClass(trainerClass)
                .stream()
                .map(trainerMapper::toDto)
                .toList();
        return ResponseEntity.ok(trainers);
    }

    // GAME POSSESSIONS QUERIES

    @GetMapping("/{trainerId}/games")
    public ResponseEntity<List<GamePossessionDto>> getGamesByTrainer(@PathVariable Long trainerId) {
        List<GamePossessionDto> games = trainerService.findGamesByTrainerId(trainerId)
                .stream()
                .map(gamePossessionMapper::toDto)
                .toList();
        return ResponseEntity.ok(games);
    }

    @GetMapping("/games/videogame/{videogameId}")
    public ResponseEntity<List<GamePossessionDto>> getTrainersByVideogame(@PathVariable Long videogameId) {
        List<GamePossessionDto> games = trainerService.findTrainersByVideogameId(videogameId)
                .stream()
                .map(gamePossessionMapper::toDto)
                .toList();
        return ResponseEntity.ok(games);
    }

    // CRUDs

    @PostMapping
    public ResponseEntity<TrainerDto> createTrainer(@RequestBody TrainerInputDto dto) {
        Trainer created = trainerService.createTrainer(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(trainerMapper.toDto(created));
    }

    @PutMapping("/{id}")
    @PreAuthorize("authentication.name == @trainerService.findById(#id).username")
    public ResponseEntity<TrainerDto> updateTrainer(@PathVariable Long id, @RequestBody TrainerInputDto dto) {
        Trainer updated = trainerService.updateTrainer(id, dto);
        return ResponseEntity.ok(trainerMapper.toDto(updated));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("authentication.name == @trainerService.findById(#id).username")
    public ResponseEntity<Void> deleteTrainer(@PathVariable Long id) {
        trainerService.deleteTrainer(id);
        return ResponseEntity.noContent().build();
    }
}
