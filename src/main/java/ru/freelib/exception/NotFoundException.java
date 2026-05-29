package ru.freelib.exception;

import org.springframework.http.HttpStatus;

public class NotFoundException extends FreeLibException {
    public NotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND);
    }

    public NotFoundException(String entityName, Object id) {
        super(entityName + " не найден(а): " + id, HttpStatus.NOT_FOUND);
    }
}