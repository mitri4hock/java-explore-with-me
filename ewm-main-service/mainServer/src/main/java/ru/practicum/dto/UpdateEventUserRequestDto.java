package ru.practicum.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;
import ru.practicum.util.CustomLocation;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateEventUserRequestDto {
    @Length(max = 2000, min = 20)
    private String annotation;
    private Long category;
    @Length(max = 7000, min = 20)
    private String description;
    private String eventDate;
    private CustomLocation location;
    private Boolean paid;
    private Integer participantLimit;
    private Boolean requestModeration;
    private String stateAction;
    @Length(max = 120, min = 3)
    private String title;
}
