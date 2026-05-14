package dev.trainerforge.exception;

public class GenerationNotFoundException extends ResourceNotFoundException {
    public GenerationNotFoundException(Long id) {
        super("Generation not found with id: " + id + ".");
    }

    public GenerationNotFoundException(String message) {
        super(message);
    }

}
