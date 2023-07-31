package ru.practicum.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.ParticipationRequestDto;
import ru.practicum.enums.EventRequestStatusEnum;
import ru.practicum.enums.StateEnum;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.ErrorDtoUtil;
import ru.practicum.exception.NotFoundException;
import ru.practicum.mapper.UtilitMapper;
import ru.practicum.model.EventRequest;
import ru.practicum.storage.EventStorage;
import ru.practicum.storage.RequestStorage;
import ru.practicum.storage.UserStorage;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional(readOnly = true)
@AllArgsConstructor
public class RequestServiceImpl implements RequestService {

    private final RequestStorage requestStorage;
    private final EventStorage eventStorage;
    private final UserStorage userStorage;

    @Override
    @Transactional
    public ParticipationRequestDto createRequest(Long userId, Long eventId) {
        var event = eventStorage.findById(eventId).orElseThrow(() -> {
            log.info("Попытка подписаться на отсутствующее событие. Id={}", eventId);
            throw new NotFoundException(String.join("", "Event with id=", eventId.toString(),
                    " was not found"),
                    new ErrorDtoUtil("The required object was not found.", LocalDateTime.now()));
        });
        if (event.getInitiator().getId().equals(userId)) {
            log.info("Попытка подписаться на собственное событие. EventId={}, UserId={}", eventId, userId);
            throw new ConflictException("попытка подписаться на соственное событие",
                    new ErrorDtoUtil("Incorrectly made request.", LocalDateTime.now()));
        }
        if (!event.getState().equals(StateEnum.PUBLISHED)) {
            log.info("Попытка подписаться на неопубликованное событие. EventId={}", eventId);
            throw new ConflictException("попытка подписаться на неопубликованное событие",
                    new ErrorDtoUtil("Incorrectly made request.", LocalDateTime.now()));
        }
        if (requestStorage.findByEvent_IdAndRequester_Id(eventId, userId).isPresent()) {
            log.info("Попытка повторной подписки на событие. EventId={}, UserId={}", eventId, userId);
            throw new ConflictException("Попытка повторной подписки на событие",
                    new ErrorDtoUtil("Incorrectly made request.", LocalDateTime.now()));
        }
        if (event.getParticipantLimit() != 0 &&
                event.getParticipantLimit() == requestStorage.countByEvent_IdAndStatus(eventId,
                        EventRequestStatusEnum.PENDING)) {
            log.info("Попытка подписки на событие c заполненным лимитом. EventId={}", eventId);
            throw new ConflictException("Попытка подписки на событие c заполненным лимитом.",
                    new ErrorDtoUtil("Incorrectly made request.", LocalDateTime.now()));
        }
        var user = userStorage.findById(userId).orElseThrow(() -> {
            log.info("Попытка подписаться на событие отсутствующем пользователем. UserId={}", userId);
            throw new NotFoundException(String.join("", "User with id=", userId.toString(),
                    " was not found"),
                    new ErrorDtoUtil("The required object was not found.", LocalDateTime.now()));
        });

        EventRequest rez = new EventRequest();
        rez.setCreated(LocalDateTime.now());
        rez.setEvent(event);
        rez.setRequester(user);
        if (event.getRequestModeration() == false) {
            rez.setStatus(EventRequestStatusEnum.CONFIRMED);
        } else {
            rez.setStatus(EventRequestStatusEnum.PENDING);
        }
        requestStorage.save(rez);
        log.info("создана заявка на событие: {}", rez);
        return UtilitMapper.toParticipationRequestDto(rez);
    }

    @Override
    @Transactional
    public ParticipationRequestDto cancelMyRequest(Long userId, Long requestId) {
        var request = requestStorage.findById(requestId).orElseThrow(() -> {
            log.info("Попытка изменить отсутствующую подписку. Id={}", requestId);
            throw new NotFoundException(String.join("", "Request with id=", requestId.toString(),
                    " was not found"),
                    new ErrorDtoUtil("The required object was not found.", LocalDateTime.now()));
        });
        if (!request.getRequester().getId().equals(userId)) {
            log.info("Попытка отменить подписку на чужоесобытие. RequestId={}, userId={}", requestId, userId);
            throw new ConflictException("Попытка отменить подписку на чужоесобытие.",
                    new ErrorDtoUtil("Incorrectly made request.", LocalDateTime.now()));
        }
        request.setStatus(EventRequestStatusEnum.REJECTED);
        requestStorage.save(request);
        log.info("заявка на событие отменена: {}", request);
        return UtilitMapper.toParticipationRequestDto(request);
    }

    @Override
    public List<ParticipationRequestDto> findMyRequests(Long userId) {
        if (userStorage.findById(userId).isEmpty()) {
            log.info("Попытка подписаться на событие отсутствующем пользователем. UserId={}", userId);
            throw new NotFoundException(String.join("", "User with id=", userId.toString(),
                    " was not found"),
                    new ErrorDtoUtil("The required object was not found.", LocalDateTime.now()));
        }

        var rez = requestStorage.findByRequester_IdOrderByCreatedDesc(userId);
        return rez.stream()
                .map(UtilitMapper::toParticipationRequestDto)
                .collect(Collectors.toList());
    }
}
