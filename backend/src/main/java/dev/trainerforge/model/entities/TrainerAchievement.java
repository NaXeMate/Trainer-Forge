package dev.trainerforge.model.entities;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "trainer_achievements")
public class TrainerAchievement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Trainer trainer;

    private Achievement achievement;

    private LocalDateTime dateObtained;

    public TrainerAchievement() {}

    public TrainerAchievement(Trainer trainer, Achievement achievement, LocalDateTime dateObtained) {
        this.trainer = trainer;
        this.achievement = achievement;
        this.dateObtained = dateObtained;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Trainer getTrainer() {
        return trainer;
    }

    public void setTrainer(Trainer trainer) {
        this.trainer = trainer;
    }

    public Achievement getAchievement() {
        return achievement;
    }

    public void setAchievement(Achievement achievement) {
        this.achievement = achievement;
    }

    public LocalDateTime getDateObtained() {
        return dateObtained;
    }

    public void setDateObtained(LocalDateTime dateObtained) {
        this.dateObtained = dateObtained;
    }
}
