package dev.trainerforge.exception.notfound;

import dev.trainerforge.exception.ResourceNotFoundException;

public class ItemNotFoundException extends ResourceNotFoundException {
    public ItemNotFoundException(Long id) {
        super("Item not found with id: " + id + ".");
    }
    
    public ItemNotFoundException(String message) {
        super(message);
    }
}