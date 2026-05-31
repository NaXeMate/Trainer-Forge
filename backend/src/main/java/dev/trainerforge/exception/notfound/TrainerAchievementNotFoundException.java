package dev.trainerforge.exception.notfound;

import dev.trainerforge.exception.ResourceNotFoundException;

public class TrainerAchievementNotFoundException extends ResourceNotFoundException {
    public TrainerAchievementNotFoundException(Long id) {
        super("Trainer achievement not found with id: " + id + ".");
    }
    
    public TrainerAchievementNotFoundException(String message) {
        super(message);
    }
}