package ru.practicum.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateCompilationRequestDto {
    private Set<Long> events;
    private Boolean pinned;
    private String title;
}
