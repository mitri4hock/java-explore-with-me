package ru.practicum.service;

import ru.practicum.dto.*;
import ru.practicum.enums.SortEnum;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public interface EventService {
    EventFullDto createEvent(Long userId, NewEventDto newEventDto);

    EventFullDto patchEvent(Long userId, Long eventId, UpdateEventUserRequestDto updateEventUserRequestDto);

    List<EventShortDto> getEventForUser(Long userId, Integer from, Integer size);

    EventFullDto findEventCreatedByUser(Long userId, Long eventId);

    List<ParticipationRequestDto> getParticipationForUser(Long userId, Long eventId);

    EventFullDto findPublishedEvent(Long id, HttpServletRequest request);

    List<EventFullDto> findEventByAdmin(ArrayList<Long> users, ArrayList<String> states, ArrayList<Long> categories,
                                        LocalDateTime rangeStart, LocalDateTime rangeEnd, Integer from, Integer size);

    List<EventShortDto> findEventByUser(String text, ArrayList<Long> categories, Boolean paid, LocalDateTime rangeStart,
                                        LocalDateTime rangeEnd, Boolean onlyAvailable, SortEnum sortEnum, Integer from,
                                        Integer size, HttpServletRequest request);
}
