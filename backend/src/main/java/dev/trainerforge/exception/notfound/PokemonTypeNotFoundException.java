package dev.trainerforge.exception.notfound;

import dev.trainerforge.exception.TrainerForgeException;

public class PokemonTypeNotFoundException extends TrainerForgeException {
    public PokemonTypeNotFoundException(Long id) {
        super("Pokemon type not found with id: " + id + ".");
    }
    
    public PokemonTypeNotFoundException(String message) {
        super(message);
    }
}