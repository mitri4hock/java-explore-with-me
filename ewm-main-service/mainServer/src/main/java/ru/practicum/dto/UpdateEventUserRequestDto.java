package ru.practicum.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.util.CustomLocation;

import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateEventUserRequestDto {
    @Size(max = 2000, min = 20)
    private String annotation;
    private Long category;
    @Size(max = 7000, min = 20)
    private String description;
    private String eventDate;
    private CustomLocation location;
    private Boolean paid;
    private Integer participantLimit;
    private Boolean requestModeration;
    private String stateAction;
    @Size(max = 120, min = 3)
    private String title;
}
