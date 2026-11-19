package dev.trainerforge.dto.response;

import java.util.Set;

import org.jspecify.annotations.NullMarked;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record AchievementDto(
    String name,
    String description
) {
    @NullMarked
    public record Completion(@NotEmpty Set<@NotNull @Positive Long> achievementIds) {}

    public record Progress(Long id, String name, String description, boolean unlocked) {}
}
