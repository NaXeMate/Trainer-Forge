package dev.trainerforge.dto.input;

import dev.trainerforge.model.enumerated.TrainerClass;
import jakarta.validation.constraints.NotBlank;

public record TrainerInputDto(
    @NotBlank String username,
    String email,
    String profilePictureUrl,
    @NotBlank String password,
    String realName,
    String region,
    String favoriteGame,
    String favoritePokemon,
    String bestFriendUsername,
    TrainerClass trainerClass
) {}
