package dev.trainerforge.dto.response;

import dev.trainerforge.model.enumerated.TrainerClass;

public record TrainerPrivateDto(
    Long id,
    String username,
    String email,
    String profilePictureUrl,
    String realName,
    String regionName,
    String favoriteGameName,
    Long favoritePokemonId,
    String favoritePokemonName,
    String bestFriendUsername,
    String friendCode,
    TrainerClass trainerClass
) {}
