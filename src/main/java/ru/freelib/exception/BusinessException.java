package ru.freelib.exception;

import org.springframework.http.HttpStatus;

public class BusinessException extends FreeLibException {
    public BusinessException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
}