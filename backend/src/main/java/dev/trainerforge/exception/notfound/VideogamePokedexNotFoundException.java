package dev.trainerforge.exception.notfound;

import dev.trainerforge.exception.TrainerForgeException;

public class VideogamePokedexNotFoundException extends TrainerForgeException {
    public VideogamePokedexNotFoundException(Long id) {
        super("VideogamePokedex entry not found with id: " + id + ".");
    }
    
    public VideogamePokedexNotFoundException(String message) {
        super(message);
    }
}