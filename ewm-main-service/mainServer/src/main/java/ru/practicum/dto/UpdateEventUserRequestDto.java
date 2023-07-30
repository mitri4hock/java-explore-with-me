package ru.practicum.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.util.CustomLocation;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateEventUserRequestDto {
    private String annotation;
    private Long category;
    private String description;
    private String eventDate;
    private CustomLocation location;
    private Boolean paid;
    private Integer participantLimit;
    private Boolean requestModeration;
    private String stateAction;
    private String title;
}
