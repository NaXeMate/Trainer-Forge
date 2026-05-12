package dev.trainerforge.model.entities;

import dev.trainerforge.model.enumerated.MoveClass;
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
@Table(name = "moves")
public class Move {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 100, nullable = false, unique = true)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "type_id", nullable = false)
    private PokemonType type;

    @Enumerated(EnumType.STRING)
    @Column(name = "class", nullable = false)
    private MoveClass moveClass;

    @Column(nullable = false)
    private int power;

    @Column(nullable = false)
    private int accuracy;

    @Column(nullable = false)
    private boolean contact;

    @Column(nullable = false)
    private int priority;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "target_id", nullable = false)
    private MoveTarget target;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "secondary_effect_id")
    private MoveSecondaryEffect secondaryEffect;

    @Column(name = "secondary_effect_chance", nullable = true)
    private int secondaryEffectChance;

    @Column(nullable = false)
    private int pp;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "generation_id", nullable = false)
    private Generation generation;

    public Move() {}

    public Move(Long id, String name, PokemonType type, MoveClass moveClass, int power, int accuracy, boolean contact,
            int priority, MoveTarget target, MoveSecondaryEffect secondaryEffect, int secondaryEffectChance, int pp,
            Generation generation) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.moveClass = moveClass;
        this.power = power;
        this.accuracy = accuracy;
        this.contact = contact;
        this.priority = priority;
        this.target = target;
        this.secondaryEffect = secondaryEffect;
        this.secondaryEffectChance = secondaryEffectChance;
        this.pp = pp;
        this.generation = generation;
    }

    public Move(String name, PokemonType type, MoveClass moveClass, int power, int accuracy, boolean contact,
            int priority, MoveTarget target, MoveSecondaryEffect secondaryEffect, int secondaryEffectChance, int pp,
            Generation generation) {
        this.name = name;
        this.type = type;
        this.moveClass = moveClass;
        this.power = power;
        this.accuracy = accuracy;
        this.contact = contact;
        this.priority = priority;
        this.target = target;
        this.secondaryEffect = secondaryEffect;
        this.secondaryEffectChance = secondaryEffectChance;
        this.pp = pp;
        this.generation = generation;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public PokemonType getType() {
        return type;
    }

    public void setType(PokemonType type) {
        this.type = type;
    }

    public MoveClass getMoveClass() {
        return moveClass;
    }

    public void setMoveClass(MoveClass moveClass) {
        this.moveClass = moveClass;
    }

    public int getPower() {
        return power;
    }

    public void setPower(int power) {
        this.power = power;
    }

    public int getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(int accuracy) {
        this.accuracy = accuracy;
    }

    public boolean isContact() {
        return contact;
    }

    public void setContact(boolean contact) {
        this.contact = contact;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public MoveTarget getTarget() {
        return target;
    }

    public void setTarget(MoveTarget target) {
        this.target = target;
    }

    public MoveSecondaryEffect getSecondaryEffect() {
        return secondaryEffect;
    }

    public void setSecondaryEffect(MoveSecondaryEffect secondaryEffect) {
        this.secondaryEffect = secondaryEffect;
    }

    public int getPp() {
        return pp;
    }

    public void setPp(int pp) {
        this.pp = pp;
    }

    public Generation getGeneration() {
        return generation;
    }

    public void setGeneration(Generation generation) {
        this.generation = generation;
    }

    public int getSecondaryEffectChance() {
        return secondaryEffectChance;
    }

    public void setSecondaryEffectChance(int secondaryEffectChance) {
        this.secondaryEffectChance = secondaryEffectChance;
    }
}
