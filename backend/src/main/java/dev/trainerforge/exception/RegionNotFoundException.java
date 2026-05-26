package dev.trainerforge.exception;

public class RegionNotFoundException extends TrainerForgeException {
    public RegionNotFoundException(Long id) {
        super("Region not found with id: " + id);
    }

    public RegionNotFoundException(String message) {
        super(message);
    }
}
