package dev.trainerforge.exception;

public class TrainerNameConflictException extends TrainerForgeException {
    public TrainerNameConflictException() {
        super("Another trainer already uses that real name.");
    }
}
