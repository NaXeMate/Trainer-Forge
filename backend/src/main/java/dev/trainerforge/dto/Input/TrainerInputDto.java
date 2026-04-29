package dev.trainerforge.dto.Input;

import dev.trainerforge.model.enumerated.TrainerClass;

public record TrainerInputDto(
    String username,
    String email,
    String profilePictureUrl,
    String password,
    String realName,
    String regionName,
    String favoriteGameName,
    String favoritePokemonName,
    String bestFriendUsername,
    String friendCode,
    TrainerClass trainerClass
) {}
