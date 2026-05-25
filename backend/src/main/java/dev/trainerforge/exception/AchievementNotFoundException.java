package dev.trainerforge.exception;

public class AchievementNotFoundException extends ResourceNotFoundException {
    public AchievementNotFoundException(Long id) {
        super("Achievement not found with id: " + id + ".");
    }
    
    public AchievementNotFoundException(String message) {
        super(message);
    }
}
