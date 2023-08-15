package ru.practicum.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;
import org.springframework.beans.factory.annotation.Value;
import ru.practicum.util.CustomLocation;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NewEventDto {
    @NotBlank
    @Length(max = 2000, min = 20)
    private String annotation;
    @NotNull
    @PositiveOrZero
    private Long category;
    @NotBlank
    @Length(max = 7000, min = 20)
    private String description;
    @NotNull
    private String eventDate;
    @NotNull
    private CustomLocation location;
    @Value("false")
    private Boolean paid;
    @Value("0")
    private Integer participantLimit;
    @Value("true")
    private Boolean requestModeration;
    @NotBlank
    @Length(max = 120, min = 3)
    private String title;
}
