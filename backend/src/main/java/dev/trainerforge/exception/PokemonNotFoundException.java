package dev.trainerforge.exception;

public class PokemonNotFoundException extends TrainerForgeException {
    public PokemonNotFoundException(Long id) {
        super("Pokemon not found with id: " + id + ".");
    }
    
    public PokemonNotFoundException(String message) {
        super(message);
    }
}
