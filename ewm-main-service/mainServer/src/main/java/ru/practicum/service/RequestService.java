package ru.practicum.service;

import ru.practicum.dto.ParticipationRequestDto;

import java.util.List;

public interface RequestService {
    ParticipationRequestDto createRequest(Long userId, Long eventId);

    ParticipationRequestDto cancelMyRequest(Long userId, Long requestId);

    List<ParticipationRequestDto> findMyRequests(Long userId);
}
