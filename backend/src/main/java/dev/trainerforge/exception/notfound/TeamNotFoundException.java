package dev.trainerforge.exception.notfound;

import dev.trainerforge.exception.ResourceNotFoundException;

public class TeamNotFoundException extends ResourceNotFoundException {
    public TeamNotFoundException(Long id) {
        super("Team not found with id: " + id + ".");
    }
    
    public TeamNotFoundException(String message) {
        super(message);
    }
}