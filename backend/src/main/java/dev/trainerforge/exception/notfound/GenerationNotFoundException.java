package dev.trainerforge.exception.notfound;

import dev.trainerforge.exception.ResourceNotFoundException;

public class GenerationNotFoundException extends ResourceNotFoundException {
    public GenerationNotFoundException(Long id) {
        super("Generation not found with id: " + id + ".");
    }

    public GenerationNotFoundException(String message) {
        super(message);
    }

}