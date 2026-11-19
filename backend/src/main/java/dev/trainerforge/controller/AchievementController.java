package dev.trainerforge.controller;

import java.util.List;

import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dev.trainerforge.dto.response.AchievementDto.Completion;
import dev.trainerforge.dto.response.AchievementDto.Progress;
import dev.trainerforge.service.TrainerAchievementService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/trainers/me/achievements")
public class AchievementController {

    private final TrainerAchievementService trainerAchievementService;

    public AchievementController(TrainerAchievementService trainerAchievementService) {
        this.trainerAchievementService = trainerAchievementService;
    }

    @GetMapping
    public ResponseEntity<List<Progress>> getCurrentTrainerAchievements(Authentication authentication) {
        return ResponseEntity.ok().cacheControl(CacheControl.noStore())
                .body(trainerAchievementService.getProgress(authentication.getName()));
    }

    @PostMapping
    public ResponseEntity<List<Progress>> completeAchievements(Authentication authentication,
            @Valid @RequestBody Completion completion) {
        return ResponseEntity.ok(
                trainerAchievementService.completeAchievements(authentication.getName(), completion.achievementIds()));
    }
}
