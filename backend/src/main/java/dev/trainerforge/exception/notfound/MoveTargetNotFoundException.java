package dev.trainerforge.exception.notfound;

import dev.trainerforge.exception.ResourceNotFoundException;

public class MoveTargetNotFoundException extends ResourceNotFoundException {
    public MoveTargetNotFoundException(Long id) {
        super("Target not found with id: " + id + ".");
    }

    public MoveTargetNotFoundException(String message) {
        super(message);
    }
}