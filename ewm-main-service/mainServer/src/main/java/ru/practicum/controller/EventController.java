package ru.practicum.controller;

import lombok.AllArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ConstantsUtil;
import ru.practicum.dto.*;
import ru.practicum.enums.SortEnum;
import ru.practicum.service.EventService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping
@AllArgsConstructor
@Validated
public class EventController {

    private final EventService eventService;

    /**
     * @operation Добавление нового события
     * Обратите внимание: дата и время на которые намечено событие не может быть раньше,
     * чем через два часа от текущего момента
     */
    @PostMapping("/users/{userId}/events")
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto createEvent(@PathVariable @PositiveOrZero Long userId,
                                    @RequestBody @Valid NewEventDto newEventDto) {
        return eventService.createEvent(userId, newEventDto);
    }

    /**
     * @operation Изменение события добавленного текущим пользователем
     * Обратите внимание:
     * изменить можно только отмененные события или события в состоянии ожидания модерации (Ожидается код ошибки 409)
     * дата и время на которые намечено событие не может быть раньше, чем через два часа от текущего момента
     * (Ожидается код ошибки 409)
     */
    @PatchMapping("/users/{userId}/events/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto patchEvent(@PathVariable @PositiveOrZero Long userId,
                                   @PathVariable @PositiveOrZero Long eventId,
                                   @RequestBody @Valid UpdateEventUserRequestDto updateEventUserRequestDto) {
        return eventService.patchEvent(userId, eventId, updateEventUserRequestDto);

    }

    /**
     * @operation Получение событий, добавленных текущим пользователем
     * В случае, если по заданным фильтрам не найдено ни одного события, возвращает пустой список
     */
    @GetMapping("/users/{userId}/events")
    @ResponseStatus(HttpStatus.OK)
    public List<EventShortDto> getEventForUser(@PathVariable @PositiveOrZero Long userId,
                                               @RequestParam(value = "from", defaultValue = "0") @PositiveOrZero Integer from,
                                               @RequestParam(value = "size", defaultValue = "10") @Positive Integer size) {
        return eventService.getEventForUser(userId, from, size);
    }

    /**
     * @operation Получение полной информации о событии добавленном текущим пользователем
     * В случае, если события с заданным id не найдено, возвращает статус код 404
     */
    @GetMapping("/users/{userId}/events/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto findEventCreatedByUser(@PathVariable @PositiveOrZero Long userId,
                                               @PathVariable @PositiveOrZero Long eventId) {
        return eventService.findEventCreatedByUser(userId, eventId);
    }

    /**
     * @operation Получение информации о запросах на участие в событии текущего пользователя
     * В случае, если по заданным фильтрам не найдено ни одной заявки, возвращает пустой список
     */
    @GetMapping("/users/{userId}/events/{eventId}/requests")
    @ResponseStatus(HttpStatus.OK)
    public List<ParticipationRequestDto> getParticipationForUser(@PathVariable @PositiveOrZero Long userId,
                                                                 @PathVariable @PositiveOrZero Long eventId) {
        return eventService.getParticipationForUser(userId, eventId);
    }

    /**
     * @operation Получение подробной информации об опубликованном событии по его идентификатору
     * Обратите внимание:
     * событие должно быть опубликовано
     * информация о событии должна включать в себя количество просмотров и количество подтвержденных запросов
     * информацию о том, что по этому эндпоинту был осуществлен и обработан запрос, нужно сохранить в сервисе статистики
     * В случае, если события с заданным id не найдено, возвращает статус код 404
     */
    @GetMapping("/events/{id}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto findPublishedEvent(@PathVariable @PositiveOrZero Long id,
                                           HttpServletRequest request) {
        return eventService.findPublishedEvent(id, request);
    }

    /**
     * @operation Поиск событий
     * Эндпоинт возвращает полную информацию обо всех событиях подходящих под переданные условия     *
     * В случае, если по заданным фильтрам не найдено ни одного события, возвращает пустой список
     */
    @GetMapping("/admin/events")
    @ResponseStatus(HttpStatus.OK)
    public List<EventFullDto> findEventByAdmin(@RequestParam(value = "users", required = false) ArrayList<Long> users,
                                               @RequestParam(value = "states", required = false) ArrayList<String> states,
                                               @RequestParam(value = "categories", required = false) ArrayList<Long> categories,
                                               @RequestParam(value = "rangeStart", required = false) @DateTimeFormat(pattern = ConstantsUtil.FORMAT_DATE) LocalDateTime rangeStart,
                                               @RequestParam(value = "rangeEnd", required = false) @DateTimeFormat(pattern = ConstantsUtil.FORMAT_DATE) LocalDateTime rangeEnd,
                                               @RequestParam(value = "from", defaultValue = "0") @PositiveOrZero Integer from,
                                               @RequestParam(value = "size", defaultValue = "10") @Positive Integer size) {

        return eventService.findEventByAdmin(users, states, categories, rangeStart, rangeEnd, from, size);
    }

    /**
     * @operation Получение событий с возможностью фильтрации
     * Обратите внимание:
     * это публичный эндпоинт, соответственно в выдаче должны быть только опубликованные события
     * текстовый поиск (по аннотации и подробному описанию) должен быть без учета регистра букв
     * если в запросе не указан диапазон дат [rangeStart-rangeEnd], то нужно выгружать события, которые произойдут позже текущей даты и времени
     * информация о каждом событии должна включать в себя количество просмотров и количество уже одобренных заявок на участие
     * информацию о том, что по этому эндпоинту был осуществлен и обработан запрос, нужно сохранить в сервисе статистики
     * В случае, если по заданным фильтрам не найдено ни одного события, возвращает пустой список
     */
    @GetMapping("/events")
    @ResponseStatus(HttpStatus.OK)
    public List<EventShortDto> findEventByUser(@RequestParam(value = "text", required = false) String text,
                                               @RequestParam(value = "categories", required = false) ArrayList<Long> categories,
                                               @RequestParam(value = "paid", required = false) Boolean paid,
                                               @RequestParam(value = "rangeStart", defaultValue = "0001-01-01 01:01:01") @DateTimeFormat(pattern = ConstantsUtil.FORMAT_DATE) LocalDateTime rangeStart,
                                               @RequestParam(value = "rangeEnd", defaultValue = "9999-12-31 23:59:59") @DateTimeFormat(pattern = ConstantsUtil.FORMAT_DATE) LocalDateTime rangeEnd,
                                               @RequestParam(value = "onlyAvailable", defaultValue = "false") Boolean onlyAvailable,
                                               @RequestParam(value = "sort", defaultValue = "EVENT_DATE") String sort,
                                               @RequestParam(value = "from", defaultValue = "0") @PositiveOrZero Integer from,
                                               @RequestParam(value = "size", defaultValue = "10") @Positive Integer size,
                                               HttpServletRequest request) {
        return eventService.findEventByUser(text, categories, paid, rangeStart, rangeEnd, onlyAvailable,
                SortEnum.valueOf(sort), from, size, request);
    }
}

