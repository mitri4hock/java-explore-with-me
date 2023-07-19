package ru.practicum.exception;

public class BadParamException extends RuntimeException {
    public BadParamException(String message) {
        super(message);
    }
}
