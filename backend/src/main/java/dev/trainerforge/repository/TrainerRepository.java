package dev.trainerforge.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import dev.trainerforge.model.entities.Trainer;
import dev.trainerforge.model.enumerated.TrainerClass;

public interface TrainerRepository extends JpaRepository<Trainer, Long> {
    Optional<Trainer> findByUsername(String username);
    Optional<Trainer> findByEmail(String email);
    Optional<Trainer> findByRealName(String realName);
    Optional<Trainer> findByFriendCode(String friendCode);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    boolean existsByFriendCode(String friendCode);
    List<Trainer> findByRegionId(Long regionId);
    List<Trainer> findByFavoriteGameId(Long favoriteGameId);
    List<Trainer> findByFavoritePokemonId(Long favoritePokemonId);
    List<Trainer> findByBestFriendId(Long bestFriendId);
    List<Trainer> findByTrainerClass(TrainerClass trainerClass);
}
