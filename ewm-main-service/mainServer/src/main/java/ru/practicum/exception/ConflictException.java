package ru.practicum.exception;

import lombok.Getter;

@Getter
public class ConflictException extends RuntimeException {
    ErrorDtoUtil errorDtoUtil;

    public ConflictException(String message) {
        super(message);
    }

    public ConflictException(String message, ErrorDtoUtil errorDtoUtil) {
        super(message);
        this.errorDtoUtil = errorDtoUtil;
    }
}