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

    @PostMapping("/users/{userId}/requests")
    @ResponseStatus(HttpStatus.CREATED)
    public ParticipationRequestDto createRequest(@PathVariable @PositiveOrZero Long userId,
                                                 @RequestParam(value = "eventId") @PositiveOrZero Long eventId) {
        return requestService.createRequest(userId, eventId);
    }

    @PatchMapping("/users/{userId}/requests/{requestId}/cancel")
    @ResponseStatus(HttpStatus.OK)
    public ParticipationRequestDto cancelMyRequest(@PathVariable @PositiveOrZero Long userId,
                                                   @PathVariable @PositiveOrZero Long requestId) {
        return requestService.cancelMyRequest(userId, requestId);
    }

    @PatchMapping("/users/{userId}/events/{eventId}/requests")
    @ResponseStatus(HttpStatus.OK)
    public EventRequestStatusUpdateResultDto patchRequestStatus(@PathVariable @PositiveOrZero Long userId,
                                                                @PathVariable @PositiveOrZero Long eventId,
                                                                @RequestBody EventRequestStatusUpdateRequestDto eventRequestStatusUpdateRequestDto) {
        return requestService.patchRequestStatus(userId, eventId, eventRequestStatusUpdateRequestDto);
    }

    @PatchMapping("/admin/events/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto patchRequestByAdmin(@PathVariable @PositiveOrZero Long eventId,
                                            @RequestBody @Valid UpdateEventAdminRequestDto updateEventAdminRequestDto) {
        return requestService.patchRequestByAdmin(eventId, updateEventAdminRequestDto);
    }

    @GetMapping("/users/{userId}/requests")
    @ResponseStatus(HttpStatus.OK)
    public List<ParticipationRequestDto> findMyRequests(@PathVariable @PositiveOrZero Long userId) {
        return requestService.findMyRequests(userId);
    }
}
