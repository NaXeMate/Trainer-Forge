package dev.trainerforge.model.entities;

import java.util.ArrayList;
import java.util.List;

import dev.trainerforge.model.enumerated.TrainerClass;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "region_id")
    private Region region;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "favorite_game_id")
    private Videogame favoriteGame;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "favorite_pokemon_id")
    private Pokedex favoritePokemon;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "best_friend_id")
    private Trainer bestFriend;

    @Column(name = "friend_code", length = 12, unique = true, nullable = false)
    private String friendCode;

    @Enumerated(EnumType.STRING)
    @Column(name = "trainer_class", nullable = false)
    private TrainerClass trainerClass;

    @OneToMany(mappedBy = "trainer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Team> teams = new ArrayList<>();

    @OneToMany(mappedBy = "trainer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TrainerAchievement> trainerAchievements = new ArrayList<>();

    @OneToMany(mappedBy = "trainer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<GamePossesion> gamePossesions = new ArrayList<>();

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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getProfilePictureUrl() {
        return profilePictureUrl;
    }

    public void setProfilePictureUrl(String profilePictureUrl) {
        this.profilePictureUrl = profilePictureUrl;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public Region getRegion() {
        return region;
    }

    public void setRegion(Region region) {
        this.region = region;
    }

    public Videogame getFavoriteGame() {
        return favoriteGame;
    }

    public void setFavoriteGame(Videogame favoriteGame) {
        this.favoriteGame = favoriteGame;
    }

    public Pokedex getFavoritePokemon() {
        return favoritePokemon;
    }

    public void setFavoritePokemon(Pokedex favoritePokemon) {
        this.favoritePokemon = favoritePokemon;
    }

    public Trainer getBestFriend() {
        return bestFriend;
    }

    public void setBestFriend(Trainer bestFriend) {
        this.bestFriend = bestFriend;
    }

    public String getFriendCode() {
        return friendCode;
    }

    public void setFriendCode(String friendCode) {
        this.friendCode = friendCode;
    }

    public TrainerClass getTrainerClass() {
        return trainerClass;
    }

    public void setTrainerClass(TrainerClass trainerClass) {
        this.trainerClass = trainerClass;
    }

    public List<Team> getTeams() {
        return teams;
    }

    public void setTeams(List<Team> teams) {
        this.teams = teams;
    }

    public List<TrainerAchievement> getTrainerAchievements() {
        return trainerAchievements;
    }

    public void setTrainerAchievements(List<TrainerAchievement> trainerAchievements) {
        this.trainerAchievements = trainerAchievements;
    }

    public List<GamePossesion> getGamePossesions() {
        return gamePossesions;
    }

    public void setGamePossesions(List<GamePossesion> gamePossesions) {
        this.gamePossesions = gamePossesions;
    }

    



}
