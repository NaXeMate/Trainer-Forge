package dev.trainerforge.dto.response;

import dev.trainerforge.model.enumerated.TrainerClass;

public record TrainerDto(
    String username,
    String profilePictureUrl,
    String region,
    String favoriteGame,
    String favoritePokemon,
    String bestFriendUsername,
    String friendCode,
    TrainerClass trainerClass
) {}
