package ru.practicum.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.EventFullDto;
import ru.practicum.dto.EventShortDto;
import ru.practicum.dto.NewEventDto;
import ru.practicum.dto.UpdateEventUserRequestDto;
import ru.practicum.service.EventService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping
@AllArgsConstructor
@Validated
public class EventController {

    private final EventService eventService;

    /**
     * Добавление нового события
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
     * Изменение события добавленного текущим пользователем
     * Обратите внимание:
     * изменить можно только отмененные события или события в состоянии ожидания модерации (Ожидается код ошибки 409)
     * дата и время на которые намечено событие не может быть раньше, чем через два часа от текущего момента
     * (Ожидается код ошибки 409)
     */
    @PatchMapping("/users/{userId}/events/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto patchEvent(@PathVariable @PositiveOrZero Long userId,
                                   @PathVariable @PositiveOrZero Long eventId,
                                   @RequestBody UpdateEventUserRequestDto updateEventUserRequestDto) {
        return eventService.patchEvent(userId, eventId, updateEventUserRequestDto);

    }

    /**
     * Получение событий, добавленных текущим пользователем
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
     * Получение полной информации о событии добавленном текущим пользователем
     * В случае, если события с заданным id не найдено, возвращает статус код 404
     */
    @GetMapping("/users/{userId}/events/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto findEventCreatedByUser(@PathVariable @PositiveOrZero Long userId,
                                               @PathVariable @PositiveOrZero Long eventId) {
        return eventService.findEventCreatedByUser(userId, eventId);
    }

}
