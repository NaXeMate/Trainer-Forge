package dev.trainerforge.model.entities;

import dev.trainerforge.model.enumerated.TrainerClass;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "trainers")
public class Trainer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 55, nullable = false, unique = true)
    private String username;

    @Column(length = 255, nullable = false)
    private String email;

    @Column(name = "profile_picture_url", length = 255)
    private String profilePictureUrl;

    @Column(name = "password_hash", length = 255, nullable = false)
    private String passwordHash;

    @Column(name = "real_name", length = 55, unique = true)
    private String realName;

    private Region region;

    private Videogame favoriteGame;

    private Pokedex favoritePokemon;

    private Trainer bestFriend;

    @Column(name = "friend_code", length = 12, unique = true, nullable = false)
    private String friendCode;

    @Enumerated(EnumType.STRING)
    @Column(name = "trainer_class", nullable = false)
    private TrainerClass trainerClass;

    public Trainer() {}

    public Trainer(String username, String email, String profilePictureUrl, String passwordHash, String realName,
            Region region, Videogame favoriteGame, Pokedex favoritePokemon, Trainer bestFriend, String friendCode,
            TrainerClass trainerClass) {
        this.username = username;
        this.email = email;
        this.profilePictureUrl = profilePictureUrl;
        this.passwordHash = passwordHash;
        this.realName = realName;
        this.region = region;
        this.favoriteGame = favoriteGame;
        this.favoritePokemon = favoritePokemon;
        this.bestFriend = bestFriend;
        this.friendCode = friendCode;
        this.trainerClass = trainerClass;
    }

    



}
