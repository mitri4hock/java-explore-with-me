package ru.practicum.service;

import ru.practicum.dto.EventFullDto;
import ru.practicum.dto.EventShortDto;
import ru.practicum.dto.NewEventDto;
import ru.practicum.dto.UpdateEventUserRequestDto;

import java.util.List;

public interface EventService {
    EventFullDto createEvent(Long userId, NewEventDto newEventDto);

    EventFullDto patchEvent(Long userId, Long eventId, UpdateEventUserRequestDto updateEventUserRequestDto);

    List<EventShortDto> getEventForUser(Long userId, Integer from, Integer size);

    EventFullDto findEventCreatedByUser(Long userId, Long eventId);
}
