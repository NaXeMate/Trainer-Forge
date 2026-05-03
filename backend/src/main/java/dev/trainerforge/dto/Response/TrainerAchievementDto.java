package dev.trainerforge.dto.response;

import java.time.LocalDateTime;

public record TrainerAchievementDto(
    String trainerUsername,
    String achievement,
    LocalDateTime dateObtained
) {}
