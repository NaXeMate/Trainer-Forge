package dev.trainerforge.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import dev.trainerforge.model.entities.Trainer;
import dev.trainerforge.model.enumerated.TrainerClass;

public interface TrainerRepository extends JpaRepository<Trainer, Long> {
    
    Trainer findByUsername(String username);
    Trainer findByEmail(String email);
    Trainer findByRealName(String realName);
    Trainer findByFriendCode(String friendCode);
    List<Trainer> findByRegionId(Long regionId);
    List<Trainer> findByFavoriteGameId(Long favoriteGameId);
    List<Trainer> findByFavoritePokemonId(Long favoritePokemonId);
    List<Trainer> findByBestFriendId(Long bestFriendId);
    List<Trainer> findByTrainerClass(TrainerClass trainerClass);
}
