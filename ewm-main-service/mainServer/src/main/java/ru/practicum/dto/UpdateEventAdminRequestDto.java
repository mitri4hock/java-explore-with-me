package ru.practicum.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.util.CustomLocation;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateEventAdminRequestDto {
    private String annotation; //описание
    private Long category; //Категория
    private String description;//Полное описание события
    private String eventDate;//Дата и время на которые намечено событие (в формате "yyyy-MM-dd HH:mm:ss")
    private CustomLocation location; //Широта и долгота места проведения события
    private Boolean paid;//Нужно ли оплачивать участие
    private Integer participantLimit;//Ограничение на количество участников. Значение 0 - означает отсутствие ограничения
    private Boolean requestModeration;//Нужна ли пре-модерация заявок на участие
    private String stateAction; //Новое состояние события
    private String title;//Заголовок
}
