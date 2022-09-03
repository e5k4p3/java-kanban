package service.exceptions;

public class SubtaskWithoutEpicException extends Exception {
    public SubtaskWithoutEpicException(final String message) {
        super(message);
    }
}
