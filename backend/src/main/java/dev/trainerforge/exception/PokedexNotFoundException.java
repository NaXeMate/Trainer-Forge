package dev.trainerforge.exception;

public class PokedexNotFoundException extends TrainerForgeException {
    public PokedexNotFoundException(Long id) {
        super("Pokedex entry not found with id: " + id + ".");
    }
    
    public PokedexNotFoundException(String message) {
        super(message);
    }
}
