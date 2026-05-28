package dev.trainerforge.exception.notfound;

import dev.trainerforge.exception.TrainerForgeException;

public class PokemonNotFoundException extends TrainerForgeException {
    public PokemonNotFoundException(Long id) {
        super("Pokemon not found with id: " + id + ".");
    }
    
    public PokemonNotFoundException(String message) {
        super(message);
    }
}