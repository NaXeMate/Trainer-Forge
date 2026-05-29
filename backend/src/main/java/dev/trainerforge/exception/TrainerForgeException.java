package dev.trainerforge.exception;

public abstract class TrainerForgeException extends RuntimeException {
    public TrainerForgeException(String message) {
        super(message);
    }
}
