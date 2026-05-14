package dev.trainerforge.exception;

public abstract class ResourceNotFoundException extends TrainerForgeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
