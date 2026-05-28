package dev.trainerforge.exception.notfound;

import dev.trainerforge.exception.ResourceNotFoundException;

public class MovePokedexNotFoundException extends ResourceNotFoundException {
    public MovePokedexNotFoundException(Long id) {
        super("MovePokedex entry not found with id: " + id + ".");
    }
    
    public MovePokedexNotFoundException(String message) {
        super(message);
    }
}