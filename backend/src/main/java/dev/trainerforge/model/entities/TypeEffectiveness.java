package dev.trainerforge.model.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "type_effectiveness")
public class TypeEffectiveness {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "attacking_type_id", nullable = false)
    private PokemonType attackingType;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "defending_type_id", nullable = false)
    private PokemonType defendingType;

    @Column(precision = 3, scale = 2, nullable = false)
    private Double multiplier;

    public TypeEffectiveness() {}

    public TypeEffectiveness(PokemonType attackingType, PokemonType defendingType, Double multiplier) {
        this.attackingType = attackingType;
        this.defendingType = defendingType;
        this.multiplier = multiplier;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public PokemonType getAttackingType() {
        return attackingType;
    }

    public void setAttackingType(PokemonType attackingType) {
        this.attackingType = attackingType;
    }

    public PokemonType getDefendingType() {
        return defendingType;
    }

    public void setDefendingType(PokemonType defendingType) {
        this.defendingType = defendingType;
    }

    public Double getMultiplier() {
        return multiplier;
    }

    public void setMultiplier(Double multiplier) {
        this.multiplier = multiplier;
    }
}
