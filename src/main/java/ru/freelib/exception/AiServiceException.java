package ru.freelib.exception;

public class AiServiceException extends ExternalServiceException {
    public AiServiceException(String message) {
        super(message, null);
    }
    public AiServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}