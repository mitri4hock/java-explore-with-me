package ru.practicum.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.*;
import ru.practicum.service.RequestService;

import javax.validation.Valid;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping
@AllArgsConstructor
@Validated
public class RequestController {

    private final RequestService requestService;

    /**
     * Добавление запроса от текущего пользователя на участие в событии
     * Обратите внимание:     *
     * нельзя добавить повторный запрос (Ожидается код ошибки 409)
     * инициатор события не может добавить запрос на участие в своём событии (Ожидается код ошибки 409)
     * нельзя участвовать в неопубликованном событии (Ожидается код ошибки 409)
     * если у события достигнут лимит запросов на участие - необходимо вернуть ошибку (Ожидается код ошибки 409)
     * если для события отключена пре-модерация запросов на участие, то запрос должен автоматически перейти в состояние подтвержденного
     */
    @PostMapping("/users/{userId}/requests")
    @ResponseStatus(HttpStatus.CREATED)
    public ParticipationRequestDto createRequest(@PathVariable @PositiveOrZero Long userId,
                                                 @RequestParam(value = "eventId") @PositiveOrZero Long eventId) {
        return requestService.createRequest(userId, eventId);
    }

    /**
     * Отмена своего запроса на участие в событии
     */
    @PatchMapping("/users/{userId}/requests/{requestId}/cancel")
    @ResponseStatus(HttpStatus.OK)
    public ParticipationRequestDto cancelMyRequest(@PathVariable @PositiveOrZero Long userId,
                                                   @PathVariable @PositiveOrZero Long requestId) {
        return requestService.cancelMyRequest(userId, requestId);
    }

    /**
     * Изменение статуса (подтверждена, отменена) заявок на участие в событии текущего пользователя
     * Обратите внимание:     *
     * если для события лимит заявок равен 0 или отключена пре-модерация заявок, то подтверждение заявок не требуется
     * нельзя подтвердить заявку, если уже достигнут лимит по заявкам на данное событие (Ожидается код ошибки 409)
     * статус можно изменить только у заявок, находящихся в состоянии ожидания (Ожидается код ошибки 409)
     * если при подтверждении данной заявки, лимит заявок для события исчерпан, то все неподтверждённые заявки необходимо отклонить
     */
    @PatchMapping("/users/{userId}/events/{eventId}/requests")
    @ResponseStatus(HttpStatus.OK)
    public EventRequestStatusUpdateResultDto patchRequestStatus(@PathVariable @PositiveOrZero Long userId,
                                                                @PathVariable @PositiveOrZero Long eventId,
                                                                @RequestBody EventRequestStatusUpdateRequestDto eventRequestStatusUpdateRequestDto) {
        return requestService.patchRequestStatus(userId, eventId, eventRequestStatusUpdateRequestDto);
    }

    /**
     * Редактирование данных события и его статуса (отклонение/публикация).
     * Редактирование данных любого события администратором. Валидация данных не требуется. Обратите внимание:
     * дата начала изменяемого события должна быть не ранее чем за час от даты публикации. (Ожидается код ошибки 409)
     * событие можно публиковать, только если оно в состоянии ожидания публикации (Ожидается код ошибки 409)
     * событие можно отклонить, только если оно еще не опубликовано (Ожидается код ошибки 409)
     */
    @PatchMapping("/admin/events/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto patchRequestByAdmin(@PathVariable @PositiveOrZero Long eventId,
                                            @RequestBody @Valid UpdateEventAdminRequestDto updateEventAdminRequestDto) {
        return requestService.patchRequestByAdmin(eventId, updateEventAdminRequestDto);
    }

    /**
     * Получение информации о заявках текущего пользователя на участие в чужих событиях
     * В случае, если по заданным фильтрам не найдено ни одной заявки, возвращает пустой список
     */
    @GetMapping("/users/{userId}/requests")
    @ResponseStatus(HttpStatus.OK)
    public List<ParticipationRequestDto> findMyRequests(@PathVariable @PositiveOrZero Long userId) {
        return requestService.findMyRequests(userId);
    }
}
