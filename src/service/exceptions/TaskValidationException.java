package service.exceptions;

public class TaskValidationException extends Exception {
    public TaskValidationException(final String message) {
        super(message);
    }
}
