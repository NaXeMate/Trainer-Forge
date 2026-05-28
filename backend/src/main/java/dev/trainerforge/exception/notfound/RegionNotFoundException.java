package dev.trainerforge.exception.notfound;

import dev.trainerforge.exception.TrainerForgeException;

public class RegionNotFoundException extends TrainerForgeException {
    public RegionNotFoundException(Long id) {
        super("Region not found with id: " + id);
    }

    public RegionNotFoundException(String message) {
        super(message);
    }
}