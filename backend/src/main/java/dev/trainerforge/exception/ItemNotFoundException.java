package dev.trainerforge.exception;

public class ItemNotFoundException extends TrainerForgeException {
    public ItemNotFoundException(Long id) {
        super("Item not found with id: " + id + ".");
    }
    
    public ItemNotFoundException(String message) {
        super(message);
    }
}
