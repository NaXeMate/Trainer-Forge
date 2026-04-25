package dev.trainerforge.model.entities;

import dev.trainerforge.model.enumerated.MoveClass;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "moves")
public class Move {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 100, nullable = false, unique = true)
    private String name;

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

    private MoveTarget target;

    private MoveSecondaryEffect secondaryEffect;

    private Generation generationId;

    public Move() {}

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

    public Generation getGenerationId() {
        return generationId;
    }

    public void setGenerationId(Generation generationId) {
        this.generationId = generationId;
    }

    
}
