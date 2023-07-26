package ru.practicum.service;

import ru.practicum.dto.EventFullDto;
import ru.practicum.dto.NewEventDto;

public interface EventService {
    EventFullDto createEvent(Long userId, NewEventDto newEventDto);
}
