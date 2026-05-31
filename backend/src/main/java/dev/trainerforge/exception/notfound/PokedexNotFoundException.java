package dev.trainerforge.exception.notfound;

import dev.trainerforge.exception.ResourceNotFoundException;

public class PokedexNotFoundException extends ResourceNotFoundException {
    public PokedexNotFoundException(Long id) {
        super("Pokedex entry not found with id: " + id + ".");
    }
    
    public PokedexNotFoundException(String message) {
        super(message);
    }
}