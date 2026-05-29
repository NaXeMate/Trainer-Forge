package dev.trainerforge.exception.notfound;

import dev.trainerforge.exception.ResourceNotFoundException;

public class PokemonNotFoundException extends ResourceNotFoundException {
    public PokemonNotFoundException(Long id) {
        super("Pokemon not found with id: " + id + ".");
    }
    
    public PokemonNotFoundException(String message) {
        super(message);
    }
}