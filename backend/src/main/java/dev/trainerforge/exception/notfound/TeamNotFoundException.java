package dev.trainerforge.exception.notfound;

import dev.trainerforge.exception.TrainerForgeException;

public class TeamNotFoundException extends TrainerForgeException {
    public TeamNotFoundException(Long id) {
        super("Team not found with id: " + id + ".");
    }
    
    public TeamNotFoundException(String message) {
        super(message);
    }
}