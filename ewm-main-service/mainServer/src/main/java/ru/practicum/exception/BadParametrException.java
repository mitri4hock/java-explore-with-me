package ru.practicum.exception;

import lombok.Getter;

@Getter
public class BadParametrException extends RuntimeException {
    ErrorDtoUtil errorDtoUtil;

    public BadParametrException(String message) {
        super(message);
    }

    public BadParametrException(String message, ErrorDtoUtil errorDtoUtil) {
        super(message);
        this.errorDtoUtil = errorDtoUtil;
    }
}