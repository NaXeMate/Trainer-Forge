package dev.trainerforge.exception.notfound;

import dev.trainerforge.exception.ResourceNotFoundException;

public class NatureNotFoundException extends ResourceNotFoundException {
    public NatureNotFoundException(Long id) {
        super("Nature not found with id: " + id + ".");
    }

    public NatureNotFoundException(String message) {
        super(message);
    }
}