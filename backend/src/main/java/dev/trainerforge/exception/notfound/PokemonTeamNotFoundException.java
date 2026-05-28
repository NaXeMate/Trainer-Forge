package dev.trainerforge.exception.notfound;

import dev.trainerforge.exception.TrainerForgeException;

public class PokemonTeamNotFoundException extends TrainerForgeException {
    public PokemonTeamNotFoundException(Long id) {
        super("Team-Pokemon association not found with id: " + id + ".");
    }
    
    public PokemonTeamNotFoundException(String message) {
        super(message);
    }
}