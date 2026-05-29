package dev.trainerforge.exception.notfound;

import dev.trainerforge.exception.ResourceNotFoundException;

public class PokemonTypeNotFoundException extends ResourceNotFoundException {
    public PokemonTypeNotFoundException(Long id) {
        super("Pokemon type not found with id: " + id + ".");
    }
    
    public PokemonTypeNotFoundException(String message) {
        super(message);
    }
}