package dev.trainerforge.exception.notfound;

import dev.trainerforge.exception.ResourceNotFoundException;

public class RegionNotFoundException extends ResourceNotFoundException {
    public RegionNotFoundException(Long id) {
        super("Region not found with id: " + id);
    }

    public RegionNotFoundException(String message) {
        super(message);
    }
}