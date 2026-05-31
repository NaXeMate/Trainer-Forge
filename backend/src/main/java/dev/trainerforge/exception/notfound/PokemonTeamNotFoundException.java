package dev.trainerforge.exception.notfound;

import dev.trainerforge.exception.ResourceNotFoundException;

public class PokemonTeamNotFoundException extends ResourceNotFoundException {
    public PokemonTeamNotFoundException(Long id) {
        super("Team-Pokemon association not found with id: " + id + ".");
    }
    
    public PokemonTeamNotFoundException(String message) {
        super(message);
    }
}