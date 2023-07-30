package ru.practicum.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventShortDto {
    private String description;//Краткая информация о событии
    private String annotation;//Краткое описание
    private CategoryDto category;
    private Long confirmedRequests;//Количество одобренных заявок на участие в данном событии
    private String eventDate;//Дата и время на которые намечено событие (в формате "yyyy-MM-dd HH:mm:ss")
    private Long id;
    private UserSortDto initiator;
    private Boolean paid;//Нужно ли оплачивать участие
    private String title;//Заголовок
    private Long views;//Количество просмотрев события
}
