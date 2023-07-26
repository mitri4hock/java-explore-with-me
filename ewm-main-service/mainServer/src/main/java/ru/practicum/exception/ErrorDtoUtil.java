package ru.practicum.exception;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ErrorDtoUtil {
    private String reason;
    private LocalDateTime timestamp;
}
