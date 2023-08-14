package ru.practicum.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;
import ru.practicum.util.CustomLocation;

import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateEventAdminRequestDto {
    @Length(max = 2000, min = 20)
    private String annotation; //описание
    private Long category; //Категория
    @Length(max = 7000, min = 20)
    private String description;//Полное описание события
    private String eventDate;//Дата и время на которые намечено событие (в формате "yyyy-MM-dd HH:mm:ss")
    private CustomLocation location; //Широта и долгота места проведения события
    private Boolean paid;//Нужно ли оплачивать участие
    private Integer participantLimit;//Ограничение на количество участников. Значение 0 - означает отсутствие ограничения
    private Boolean requestModeration;//Нужна ли пре-модерация заявок на участие
    private String stateAction; //Новое состояние события
    @Length(max = 120, min = 3)
    private String title;//Заголовок
}
