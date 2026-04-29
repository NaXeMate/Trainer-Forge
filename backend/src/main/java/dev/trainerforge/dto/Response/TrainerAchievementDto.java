package dev.trainerforge.dto.Response;

import java.time.LocalDateTime;

public record TrainerAchievementDto(
    String trainerUsername,
    String achievementName,
    LocalDateTime dateObtained
) {}
