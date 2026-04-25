package dev.trainerforge.model.entities;

import dev.trainerforge.model.enumerated.NatureRiseLower;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "natures")
public class Nature {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 100, nullable = false, unique = true)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NatureRiseLower rise;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NatureRiseLower lower;

    public Nature() {}

    public Nature(String name, NatureRiseLower rise, NatureRiseLower lower) {
        this.name = name;
        this.rise = rise;
        this.lower = lower;
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

    public NatureRiseLower getRise() {
        return rise;
    }

    public void setRise(NatureRiseLower rise) {
        this.rise = rise;
    }

    public NatureRiseLower getLower() {
        return lower;
    }

    public void setLower(NatureRiseLower lower) {
        this.lower = lower;
    }
}
