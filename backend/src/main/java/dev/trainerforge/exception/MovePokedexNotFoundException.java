package dev.trainerforge.exception;

public class MovePokedexNotFoundException extends ResourceNotFoundException {
    public MovePokedexNotFoundException(Long id) {
        super("MovePokedex entry not found with id: " + id + ".");
    }
    
    public MovePokedexNotFoundException(String message) {
        super(message);
    }
}
