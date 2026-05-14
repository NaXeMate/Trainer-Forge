package dev.trainerforge.exception;

public class NatureNotFoundException extends RuntimeException {
    public NatureNotFoundException(Long id) {
        super("Nature not found with id: " + id + ".");
    }

    public NatureNotFoundException(String message) {
        super(message);
    }
}
