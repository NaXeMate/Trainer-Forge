package dev.trainerforge.exception.notfound;

import dev.trainerforge.exception.ResourceNotFoundException;

public class VideogameNotFoundException extends ResourceNotFoundException {
    public VideogameNotFoundException(Long id) {
        super("Videogame not found with id: " + id + ".");
    }
    
    public VideogameNotFoundException(String message) {
        super(message);
    }
}