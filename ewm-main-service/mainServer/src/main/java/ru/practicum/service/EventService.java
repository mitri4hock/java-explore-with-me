package ru.practicum.service;

import ru.practicum.dto.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface EventService {
    EventFullDto createEvent(Long userId, NewEventDto newEventDto);

    EventFullDto patchEvent(Long userId, Long eventId, UpdateEventUserRequestDto updateEventUserRequestDto);

    List<EventShortDto> getEventForUser(Long userId, Integer from, Integer size);

    EventFullDto findEventCreatedByUser(Long userId, Long eventId);

    List<ParticipationRequestDto> getParticipationForUser(Long userId, Long eventId);

    EventFullDto findPublishedEvent(Long id, HttpServletRequest request);
}
