package ru.practicum.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ParticipationRequestDto {
    private String created; //Дата и время создания заявки
    private Long event; //Идентификатор события
    private Long id;//Идентификатор заявки
    private Long requester; //Идентификатор пользователя, отправившего заявку
    private String status; //Статус заявки
}
