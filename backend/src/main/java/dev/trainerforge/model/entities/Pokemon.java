package dev.trainerforge.model.entities;

import dev.trainerforge.model.enumerated.Gender;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "pokemon")
public class Pokemon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Pokedex species;

    @Column(length = 64)
    private String nickname;

    @Column(name = "location_found", length = 255, nullable = false)
    private String locationFound;

    @Column(nullable = false)
    private int level;

    @Column(nullable = false)
    private boolean shiny;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Gender gender;

    
    private Ability ability;

    // TODO: Consider changing this to an array or list of moves.
    private Move move1;

    
    private Move move2;
    
    
    private Move move3;
    
    
    private Move move4;

    
    private PokemonItem equippedItem;


    private Nature nature;

    // TODO: Consider changing this to an array or list of EVs.
    // TODO: Consider changing the data type of these base stats to something more appropriate, such as short or byte (max: 32).
    @Column(name = "hp_ev", nullable = false)
    private int hpEv;
    
    @Column(name = "attack_ev", nullable = false)
    private int attackEv;

    @Column(name = "defense_ev", nullable = false)
    private int defenseEv;

    @Column(name = "special_attack_ev", nullable = false)
    private int specialAttackEv;

    @Column(name = "special_defense_ev", nullable = false)
    private int specialDefenseEv;

    @Column(name = "speed_ev", nullable = false)
    private int speedEv;

    public Pokemon() {}


    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Pokedex getSpecies() {
        return species;
    }

    public void setSpecies(Pokedex species) {
        this.species = species;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getLocationFound() {
        return locationFound;
    }

    public void setLocationFound(String locationFound) {
        this.locationFound = locationFound;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public boolean isShiny() {
        return shiny;
    }

    public void setShiny(boolean shiny) {
        this.shiny = shiny;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public Ability getAbility() {
        return ability;
    }

    public void setAbility(Ability ability) {
        this.ability = ability;
    }

    public Move getMove1() {
        return move1;
    }

    public void setMove1(Move move1) {
        this.move1 = move1;
    }

    public Move getMove2() {
        return move2;
    }

    public void setMove2(Move move2) {
        this.move2 = move2;
    }

    public Move getMove3() {
        return move3;
    }

    public void setMove3(Move move3) {
        this.move3 = move3;
    }

    public Move getMove4() {
        return move4;
    }

    public void setMove4(Move move4) {
        this.move4 = move4;
    }

    public PokemonItem getEquippedItem() {
        return equippedItem;
    }

    public void setEquippedItem(PokemonItem equippedItem) {
        this.equippedItem = equippedItem;
    }

    public Nature getNature() {
        return nature;
    }

    public void setNature(Nature nature) {
        this.nature = nature;
    }

    public int getHpEv() {
        return hpEv;
    }

    public void setHpEv(int hpEv) {
        this.hpEv = hpEv;
    }

    public int getAttackEv() {
        return attackEv;
    }

    public void setAttackEv(int attackEv) {
        this.attackEv = attackEv;
    }

    public int getDefenseEv() {
        return defenseEv;
    }

    public void setDefenseEv(int defenseEv) {
        this.defenseEv = defenseEv;
    }

    public int getSpecialAttackEv() {
        return specialAttackEv;
    }

    public void setSpecialAttackEv(int specialAttackEv) {
        this.specialAttackEv = specialAttackEv;
    }

    public int getSpecialDefenseEv() {
        return specialDefenseEv;
    }

    public void setSpecialDefenseEv(int specialDefenseEv) {
        this.specialDefenseEv = specialDefenseEv;
    }

    public int getSpeedEv() {
        return speedEv;
    }

    public void setSpeedEv(int speedEv) {
        this.speedEv = speedEv;
    }

    

}
