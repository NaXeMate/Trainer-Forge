package dev.trainerforge.exception;

public class MoveSecondaryEffectNotFoundException extends ResourceNotFoundException {
    public MoveSecondaryEffectNotFoundException(Long id) {
        super("Secondary effect not found with id: " + id + ".");
    }

    public MoveSecondaryEffectNotFoundException(String message) {
        super(message);
    }
}
