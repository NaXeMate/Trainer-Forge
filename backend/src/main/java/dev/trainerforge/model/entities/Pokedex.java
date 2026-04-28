package dev.trainerforge.model.entities;

import dev.trainerforge.model.enumerated.PokemonClass;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "pokedex")
public class Pokedex {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "national_pokedex", nullable = false)
    private Long nationalPokedex;

    @Column(length = 64, nullable = false, unique = true)
    private String name;

    @Column(name = "image_url", nullable = false)
    private String imageUrl;

    
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "generation_id", nullable = false)
    private Generation generationId;

    
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "region_id", nullable = false)
    private Region region;

    @Enumerated(EnumType.STRING)
    @Column(name = "class", nullable = false)
    private PokemonClass pokemonClass;

    // TODO: Consider changing this to a array or list of types.
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "type_1_id", nullable = false)
    private PokemonType type1;

    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "type_2_id")
    private PokemonType type2;

    // TODO: Consider changing this to a array or list of abilities.
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ability_1_id", nullable = false)
    private Ability ability1;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ability_2_id")
    private Ability ability2;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hidden_ability_id")
    private Ability hiddenAbility;


    @Column(columnDefinition = "TEXT", name = "description", nullable = false)
    private String description;

    @Column(length = 64, nullable = false)
    private String category;

    @Column (nullable = false, precision = 5, scale = 2)
    private Double weight;

    @Column (nullable = false, precision = 5, scale = 2)
    private Double height;

    // TODO: Consider changing this to a array or list of base-stats.
    // TODO: Consider changing the data type of these base stats to something more appropriate, such as short or byte (max: 255).
    @Column(name = "hp_base", nullable = false)
    private int hpBase;

    @Column(name = "attack_base", nullable = false)
    private int attackBase;

    @Column(name = "defense_base", nullable = false)
    private int defenseBase;

    @Column(name = "special_attack_base", nullable = false)
    private int specialAttackBase;

    @Column(name = "special_defense_base", nullable = false)
    private int specialDefenseBase;

    @Column(name = "speed_base", nullable = false)
    private int speedBase;

    public Pokedex() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getNationalPokedex() {
        return nationalPokedex;
    }

    public void setNationalPokedex(Long nationalPokedex) {
        this.nationalPokedex = nationalPokedex;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Generation getGenerationId() {
        return generationId;
    }

    public void setGenerationId(Generation generationId) {
        this.generationId = generationId;
    }

    public Region getRegion() {
        return region;
    }

    public void setRegion(Region region) {
        this.region = region;
    }

    public PokemonClass getPokemonClass() {
        return pokemonClass;
    }
    
    public void setPokemonClass(PokemonClass pokemonClass) {
        this.pokemonClass = pokemonClass;
    }

    public PokemonType getType1() {
        return type1;
    }

    public void setType1(PokemonType type1) {
        this.type1 = type1;
    }

    public PokemonType getType2() {
        return type2;
    }

    public void setType2(PokemonType type2) {
        this.type2 = type2;
    }

    public Ability getAbility1() {
        return ability1;
    }

    public void setAbility1(Ability ability1) {
        this.ability1 = ability1;
    }

    public Ability getAbility2() {
        return ability2;
    }

    public void setAbility2(Ability ability2) {
        this.ability2 = ability2;
    }

    public Ability getHiddenAbility() {
        return hiddenAbility;
    }

    public void setHiddenAbility(Ability hiddenAbility) {
        this.hiddenAbility = hiddenAbility;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public Double getHeight() {
        return height;
    }

    public void setHeight(Double height) {
        this.height = height;
    }

    public int getHpBase() {
        return hpBase;
    }

    public void setHpBase(int hpBase) {
        this.hpBase = hpBase;
    }

    public int getAttackBase() {
        return attackBase;
    }

    public void setAttackBase(int attackBase) {
        this.attackBase = attackBase;
    }

    public int getDefenseBase() {
        return defenseBase;
    }

    public void setDefenseBase(int defenseBase) {
        this.defenseBase = defenseBase;
    }

    public int getSpecialAttackBase() {
        return specialAttackBase;
    }

    public void setSpecialAttackBase(int specialAttackBase) {
        this.specialAttackBase = specialAttackBase;
    }

    public int getSpecialDefenseBase() {
        return specialDefenseBase;
    }

    public void setSpecialDefenseBase(int specialDefenseBase) {
        this.specialDefenseBase = specialDefenseBase;
    }

    public int getSpeedBase() {
        return speedBase;
    }

    public void setSpeedBase(int speedBase) {
        this.speedBase = speedBase;
    }
}
