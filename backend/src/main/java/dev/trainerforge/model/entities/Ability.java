package dev.trainerforge.model.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "abilities")
public class Ability {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 100, nullable = false, unique = true)
    private String name;

    @Column(columnDefinition = "TEXT", name = "description", nullable = false)
    private String description;

    private Generation generationId;

    public Ability() {}

    public Ability(String name, String description, Generation generationId) {
        this.name = name;
        this.description = description;
        this.generationId = generationId;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Generation getGenerationId() {
        return generationId;
    }

    public void setGenerationId(Generation generationId) {
        this.generationId = generationId;
    }
}
