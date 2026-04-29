package dev.trainerforge.dto.Response;

import dev.trainerforge.model.enumerated.TrainerClass;

public record TrainerDto(
    String username,
    String profilePictureUrl,
    String regionName,
    String favoriteGameName,
    String favoritePokemonName,
    String bestFriendUsername,
    String friendCode,
    TrainerClass trainerClass
) {}
