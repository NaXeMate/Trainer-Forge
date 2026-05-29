package dev.trainerforge.exception.notfound;

import dev.trainerforge.exception.ResourceNotFoundException;

public class MoveSecondaryEffectNotFoundException extends ResourceNotFoundException {
    public MoveSecondaryEffectNotFoundException(Long id) {
        super("Secondary effect not found with id: " + id + ".");
    }

    public MoveSecondaryEffectNotFoundException(String message) {
        super(message);
    }
}