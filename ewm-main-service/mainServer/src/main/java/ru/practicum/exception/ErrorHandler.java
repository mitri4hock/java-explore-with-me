package ru.practicum.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.util.UtilClass;

import java.time.format.DateTimeFormatter;
import java.util.Map;

@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> badParamException(final BadParametrException e) {
        return Map.of("status", HttpStatus.BAD_REQUEST.name(),
                "reason", e.getErrorDtoUtil().getReason(),
                "message", e.getMessage(),
                "timestamp", e.getErrorDtoUtil().getTimestamp()
                        .format(DateTimeFormatter.ofPattern(UtilClass.FORMAT_DATE)));
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> notFoundException(final NotFoundException e) {
        return Map.of("status", HttpStatus.NOT_FOUND.name(),
                "reason", e.getErrorDtoUtil().getReason(),
                "message", e.getMessage(),
                "timestamp", e.getErrorDtoUtil().getTimestamp()
                        .format(DateTimeFormatter.ofPattern(UtilClass.FORMAT_DATE)));
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String, String> conflictException(final ConflictException e) {
        return Map.of("status", "FORBIDDEN",
                "reason", e.getErrorDtoUtil().getReason(),
                "message", e.getMessage(),
                "timestamp", e.getErrorDtoUtil().getTimestamp()
                        .format(DateTimeFormatter.ofPattern(UtilClass.FORMAT_DATE)));
    }
}
