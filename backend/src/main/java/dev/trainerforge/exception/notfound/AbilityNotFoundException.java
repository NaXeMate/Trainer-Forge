package dev.trainerforge.exception.notfound;

import dev.trainerforge.exception.ResourceNotFoundException;

public class AbilityNotFoundException extends ResourceNotFoundException {
    public AbilityNotFoundException(Long id) {
        super("Ability not found with id: " + id + ".");
    }
    
    public AbilityNotFoundException(String message) {
        super(message);
    }
}