package dev.trainerforge.exception.notfound;

import dev.trainerforge.exception.ResourceNotFoundException;

public class MoveNotFoundException extends ResourceNotFoundException {
    public MoveNotFoundException(Long id) {
        super("Move not found with id: " + id + ".");
    }

    public MoveNotFoundException(String message) {
        super(message);
    }
}