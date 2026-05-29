package dev.trainerforge.dto.input;

import dev.trainerforge.model.enumerated.TrainerClass;

public record TrainerInputDto(
    String username,
    String email,
    String profilePictureUrl,
    // TODO: Modify this to trainer.setPassword(passwordEncoder.encode(dto.password())) in the service layer.
    String password,
    String realName,
    String region,
    String favoriteGame,
    String favoritePokemon,
    String bestFriendUsername,
    TrainerClass trainerClass
) {}
