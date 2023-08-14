package ru.practicum.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompilationDto {
    private List<EventShortDto> events;
    @NotNull
    private Long id;
    @NotNull
    private Boolean pinned;
    @NotBlank
    private String title;
}
