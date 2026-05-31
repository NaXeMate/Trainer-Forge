package dev.trainerforge.model.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "secondary_effects")
public class MoveSecondaryEffect {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT", name = "secondary_effect", nullable = false)
    private String secondaryEffect;

    public MoveSecondaryEffect() {}

    public MoveSecondaryEffect(String secondaryEffect) {
        this.secondaryEffect = secondaryEffect;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSecondaryEffect() {
        return secondaryEffect;
    }

    public void setSecondaryEffect(String secondaryEffect) {
        this.secondaryEffect = secondaryEffect;
    } 
}
