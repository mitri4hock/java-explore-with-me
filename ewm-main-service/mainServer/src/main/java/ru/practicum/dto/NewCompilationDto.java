package ru.practicum.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NewCompilationDto {
    @NotNull
    private Set<Long> events;//Список идентификаторов событий входящих в подборку
    @NotNull
    private Boolean pinned;//Закреплена ли подборка на главной странице сайта
    @NotBlank
    @Size(max = 50, min = 1)
    private String title;//Заголовок подборки
}
