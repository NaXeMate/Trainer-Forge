package dev.trainerforge.exception.notfound;

import dev.trainerforge.exception.ResourceNotFoundException;

public class PokedexItemNotFoundException extends ResourceNotFoundException {
    public PokedexItemNotFoundException(Long id) {
        super("PokedexItem entry not found with id: " + id + ".");
    }

    public PokedexItemNotFoundException(String message) {
        super(message);
    }
}
