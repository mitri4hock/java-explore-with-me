package ru.practicum.service;

import ru.practicum.dto.*;

import java.util.List;

public interface RequestService {
    ParticipationRequestDto createRequest(Long userId, Long eventId);

    ParticipationRequestDto cancelMyRequest(Long userId, Long requestId);

    List<ParticipationRequestDto> findMyRequests(Long userId);

    EventRequestStatusUpdateResultDto patchRequestStatus(Long userId, Long eventId,
                                                         EventRequestStatusUpdateRequestDto eventRequestStatusUpdateRequestDto);

    EventFullDto patchRequestByAdmin(Long eventId, UpdateEventAdminRequestDto updateEventAdminRequestDto);
}
