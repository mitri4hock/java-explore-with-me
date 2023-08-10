package ru.practicum.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Size;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateCompilationRequestDto {
    private Set<Long> events;
    private Boolean pinned;
    @Size(max = 50, min = 1)
    private String title;
}
