package dev.trainerforge.exception;

public class MoveNotFoundException extends ResourceNotFoundException {
    public MoveNotFoundException(Long id) {
        super("Move not found with id: " + id + ".");
    }

    public MoveNotFoundException(String message) {
        super(message);
    }
}
