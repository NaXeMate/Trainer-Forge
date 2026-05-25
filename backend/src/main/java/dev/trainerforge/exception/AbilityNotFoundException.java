package dev.trainerforge.exception;

public class AbilityNotFoundException extends ResourceNotFoundException {
    public AbilityNotFoundException(Long id) {
        super("Ability not found with id: " + id + ".");
    }
    
    public AbilityNotFoundException(String message) {
        super(message);
    }
}
