package dev.trainerforge.exception.notfound;

import dev.trainerforge.exception.TrainerForgeException;

public class TrainerAchievementNotFoundException extends TrainerForgeException {
    public TrainerAchievementNotFoundException(Long id) {
        super("Trainer achievement not found with id: " + id + ".");
    }
    
    public TrainerAchievementNotFoundException(String message) {
        super(message);
    }
}