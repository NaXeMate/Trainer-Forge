package dev.trainerforge.exception;

public class TrainerNotFoundException extends TrainerForgeException {
    public TrainerNotFoundException(Long id) {
        super("Trainer not found with id: " + id);
    }

    public TrainerNotFoundException(String message) {
        super(message);
    }
}
