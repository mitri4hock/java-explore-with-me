package ru.practicum.exception;

import lombok.Getter;

@Getter
public class NotFoundException extends RuntimeException {
    ErrorDtoUtil errorDtoUtil;

    public NotFoundException(String message) {
        super(message);
    }

    public NotFoundException(String message, ErrorDtoUtil errorDtoUtil) {
        super(message);
        this.errorDtoUtil = errorDtoUtil;
    }
}