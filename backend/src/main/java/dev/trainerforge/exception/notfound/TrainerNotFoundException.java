package dev.trainerforge.exception.notfound;

import dev.trainerforge.exception.TrainerForgeException;

public class TrainerNotFoundException extends TrainerForgeException {
    public TrainerNotFoundException(Long id) {
        super("Trainer not found with id: " + id);
    }

    public TrainerNotFoundException(String message) {
        super(message);
    }
}