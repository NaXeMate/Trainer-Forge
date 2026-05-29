package dev.trainerforge.exception.notfound;

import dev.trainerforge.exception.ResourceNotFoundException;

public class VideogamePokedexNotFoundException extends ResourceNotFoundException {
    public VideogamePokedexNotFoundException(Long id) {
        super("VideogamePokedex entry not found with id: " + id + ".");
    }
    
    public VideogamePokedexNotFoundException(String message) {
        super(message);
    }
}