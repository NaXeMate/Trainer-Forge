package dev.trainerforge.dto.Response;

import dev.trainerforge.model.enumerated.TrainerClass;

public record TrainerPrivateDto(
    String username,
    String email,
    String profilePictureUrl,
    String realName,
    String regionName,
    String favoriteGameName,
    String favoritePokemonName,
    String bestFriendUsername,
    String friendCode,
    TrainerClass trainerClass
) {}
