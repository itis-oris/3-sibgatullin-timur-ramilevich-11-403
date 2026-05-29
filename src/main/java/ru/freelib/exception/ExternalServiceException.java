package ru.freelib.exception;

import org.springframework.http.HttpStatus;

public class ExternalServiceException extends FreeLibException {
    public ExternalServiceException(String message, Throwable cause) {
        super(message, HttpStatus.BAD_GATEWAY, cause);
    }
}