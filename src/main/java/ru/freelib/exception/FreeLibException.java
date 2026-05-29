package ru.freelib.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public abstract class FreeLibException extends RuntimeException {
    private final HttpStatus status;

    protected FreeLibException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

    protected FreeLibException(String message, HttpStatus status, Throwable cause) {
        super(message, cause);
        this.status = status;
    }
}