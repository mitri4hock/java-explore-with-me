package ru.practicum.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import ru.practicum.util.CustomLocation;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventFullDto {
    private Long id;
    private String annotation; //описание
    private CategoryDto category; //Категория
    private Integer confirmedRequests;//Количество одобренных заявок на участие в данном событии
    private String createdOn;//Дата и время создания события (в формате "yyyy-MM-dd HH:mm:ss")
    private String description;//Полное описание события
    private String eventDate;//Дата и время на которые намечено событие (в формате "yyyy-MM-dd HH:mm:ss")
    private UserSortDto initiator;//Пользователь (краткая информация)
    private CustomLocation location; //Широта и долгота места проведения события
    private Boolean paid;//Нужно ли оплачивать участие
    @Value("0")
    private Integer participantLimit;//Ограничение на количество участников. Значение 0 - означает отсутствие ограничения
    private String publishedOn;//Дата и время публикации события (в формате "yyyy-MM-dd HH:mm:ss")
    @Value("true")
    private Boolean requestModeration;//Нужна ли пре-модерация заявок на участие
    private String state; //Список состояний жизненного цикла события
    private String title;//Заголовок
    private Integer views;//Количество просмотрев события
}
