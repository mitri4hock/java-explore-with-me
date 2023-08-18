package ru.practicum.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentDto {
    @NotNull
    private Long id;
    @NotNull
    private Long eventId; // комментируемое событие
    @NotNull
    private Long commentatorId; //комментатор
    @NotBlank
    @Length(max = 5000)
    private String commentText; // текст комментария
    @NotNull
    private LocalDateTime createdDate; // Дата и время создания комментария
}
