package dev.trainerforge.exception;

public class TeamNotFoundException extends TrainerForgeException {
    public TeamNotFoundException(Long id) {
        super("Team not found with id: " + id + ".");
    }
    
    public TeamNotFoundException(String message) {
        super(message);
    }
}
