package ru.practicum.service;

import ru.practicum.dto.EventFullDto;
import ru.practicum.dto.EventRequestStatusUpdateRequestDto;
import ru.practicum.dto.ParticipationRequestDto;
import ru.practicum.dto.UpdateEventAdminRequestDto;

import java.util.List;

public interface RequestService {
    ParticipationRequestDto createRequest(Long userId, Long eventId);

    ParticipationRequestDto cancelMyRequest(Long userId, Long requestId);

    List<ParticipationRequestDto> findMyRequests(Long userId);

    EventRequestStatusUpdateRequestDto patchRequestStatus(Long userId, Long eventId);

    EventFullDto patchRequestByAdmin(Long eventId, UpdateEventAdminRequestDto updateEventAdminRequestDto);
}
