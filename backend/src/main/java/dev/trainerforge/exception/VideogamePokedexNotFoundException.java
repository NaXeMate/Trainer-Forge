package dev.trainerforge.exception;

public class VideogamePokedexNotFoundException extends TrainerForgeException {
    public VideogamePokedexNotFoundException(Long id) {
        super("VideogamePokedex entry not found with id: " + id + ".");
    }
    
    public VideogamePokedexNotFoundException(String message) {
        super(message);
    }
}
