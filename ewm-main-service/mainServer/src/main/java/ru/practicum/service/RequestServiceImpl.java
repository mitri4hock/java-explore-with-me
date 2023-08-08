package ru.practicum.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.client.StatisticModuleClient;
import ru.practicum.dto.EventFullDto;
import ru.practicum.dto.EventRequestStatusUpdateRequestDto;
import ru.practicum.dto.ParticipationRequestDto;
import ru.practicum.dto.UpdateEventAdminRequestDto;
import ru.practicum.enums.EventRequestStatusEnum;
import ru.practicum.enums.StateActionEnum;
import ru.practicum.enums.StateEnum;
import ru.practicum.exception.BadParametrException;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.ErrorDtoUtil;
import ru.practicum.exception.NotFoundException;
import ru.practicum.mapper.UtilitMapper;
import ru.practicum.model.Event;
import ru.practicum.model.EventRequest;
import ru.practicum.storage.*;
import ru.practicum.util.UtilClass;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional(readOnly = true)
@AllArgsConstructor
public class RequestServiceImpl implements RequestService {
    private final EventRequestStorage eventRequestStorage;

    private final RequestStorage requestStorage;
    private final EventStorage eventStorage;
    private final UserStorage userStorage;
    private final CategoriesStorage categoriesStorage;
    private final StatisticModuleClient statisticModuleClient;

    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(UtilClass.FORMAT_DATE);

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
                        EventRequestStatusEnum.CONFIRMED)) {
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
        if (event.getRequestModeration() == false || event.getParticipantLimit() == 0) {
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

    @Override
    @Transactional
    public EventRequestStatusUpdateRequestDto patchRequestStatus(Long userId, Long eventId) {
        if (userStorage.findById(userId).isEmpty()) {
            log.info("запрошено изменение статуса бронирования для несуществующего пользователя с id={}", userId);
            throw new NotFoundException(String.join("", "User with id=", userId.toString(),
                    " was not found"), new ErrorDtoUtil("The required object was not found.",
                    LocalDateTime.now()));
        }
        Event event = eventStorage.findById(eventId).orElseThrow(() -> {
            log.info("запрошено изменение статуса бронирования несуществующего события с id={}", eventId);
            throw new NotFoundException(String.join("", "Event with id=", eventId.toString(),
                    " was not found"), new ErrorDtoUtil("The required object was not found.",
                    LocalDateTime.now()));
        });
        EventRequest eventRequest = requestStorage.findByEvent_IdAndRequester_Id(eventId, userId)
                .orElseThrow(() -> {
                    log.info("запрошено изменение статуса бронирования несуществующего запроса");
                    throw new NotFoundException(String.join("", "Request was not found"),
                            new ErrorDtoUtil("The required object was not found.", LocalDateTime.now()));
                });

        if (!eventRequest.getStatus().equals(EventRequestStatusEnum.PENDING)) {
            throw new ConflictException("The participant limit has been reached",
                    new ErrorDtoUtil("For the requested operation the conditions are not met.",
                            LocalDateTime.now()));
        }
        long countOfConfirmed = requestStorage.countByEvent_IdAndStatus(eventId,
                EventRequestStatusEnum.CONFIRMED);
        if (event.getParticipantLimit() != 0 && countOfConfirmed >= event.getParticipantLimit()) {
            throw new ConflictException("The participant limit has been reached",
                    new ErrorDtoUtil("For the requested operation the conditions are not met.",
                            LocalDateTime.now()));
        }
        if (event.getParticipantLimit().equals(0) || event.getRequestModeration().equals(false)) {
            eventRequest.setStatus(EventRequestStatusEnum.CONFIRMED);
        }
        eventRequest.setStatus(EventRequestStatusEnum.CONFIRMED);
        requestStorage.save(eventRequest);
        if (event.getParticipantLimit() - 1 == countOfConfirmed) {
            requestStorage.findByEvent_IdAndStatus(eventId, EventRequestStatusEnum.PENDING).stream()
                    .forEach(x -> {
                        x.setStatus(EventRequestStatusEnum.REJECTED);
                        requestStorage.save(x);
                    });
        }
        return new EventRequestStatusUpdateRequestDto(requestStorage
                .findByRequester_IdAndStatusOrderByEvent_IdAsc(userId, EventRequestStatusEnum.CONFIRMED),
                EventRequestStatusEnum.CONFIRMED);
    }

    @Override
    @Transactional
    public EventFullDto patchRequestByAdmin(Long eventId, UpdateEventAdminRequestDto updateEventAdminRequestDto) {
        Event event = eventStorage.findById(eventId).orElseThrow(() -> {
            log.info("запрошено изменение статуса бронирования несуществующего события с id={}", eventId);
            throw new NotFoundException(String.join("", "Event with id=", eventId.toString(),
                    " was not found"), new ErrorDtoUtil("The required object was not found.",
                    LocalDateTime.now()));
        });
        if (updateEventAdminRequestDto.getAnnotation() != null) {
            event.setAnnotation(updateEventAdminRequestDto.getAnnotation());
        }
        if (updateEventAdminRequestDto.getCategory() != null) {
            event.setCategory(categoriesStorage.findById(updateEventAdminRequestDto.getCategory())
                    .orElseThrow(() -> {
                        throw new NotFoundException(String.join("", "Category with id=",
                                updateEventAdminRequestDto.getCategory().toString(), " was not found"),
                                new ErrorDtoUtil("The required object was not found.",
                                        LocalDateTime.now()));
                    }));
        }
        if (updateEventAdminRequestDto.getDescription() != null) {
            event.setDescription(updateEventAdminRequestDto.getDescription());
        }
        if (updateEventAdminRequestDto.getEventDate() != null) {
            LocalDateTime parseTime = LocalDateTime.parse(updateEventAdminRequestDto.getEventDate(),
                    dateTimeFormatter);
            if (parseTime.isBefore(LocalDateTime.now().plusHours(1L))) {
                throw new BadParametrException("событие начнётся меньше чем через 1 час после публикации",
                        new ErrorDtoUtil("For the requested operation the conditions are not met.",
                                LocalDateTime.now()));
            }
            event.setEventDate(parseTime);
        }
        if (updateEventAdminRequestDto.getLocation() != null) {
            event.setLocation(updateEventAdminRequestDto.getLocation());
        }
        if (updateEventAdminRequestDto.getPaid() != null) {
            event.setPaid(updateEventAdminRequestDto.getPaid());
        }
        if (updateEventAdminRequestDto.getParticipantLimit() != null) {
            event.setParticipantLimit(updateEventAdminRequestDto.getParticipantLimit());
        }
        if (updateEventAdminRequestDto.getStateAction() != null) {
            if (updateEventAdminRequestDto.getStateAction().equals(StateActionEnum.PUBLISH_EVENT.toString()) &&
                    !event.getState().equals(StateEnum.PENDING)) {
                throw new ConflictException("попытка публикации события, которое находится не на ожидании",
                        new ErrorDtoUtil("For the requested operation the conditions are not met.",
                                LocalDateTime.now()));
            }
            if (updateEventAdminRequestDto.getStateAction().equals(StateActionEnum.REJECT_EVENT.toString()) &&
                    event.getState().equals(StateEnum.PUBLISHED)) {
                throw new ConflictException("попытка отклонения опубликованного события",
                        new ErrorDtoUtil("For the requested operation the conditions are not met.",
                                LocalDateTime.now()));
            }
            if (updateEventAdminRequestDto.getStateAction().equals(StateActionEnum.PUBLISH_EVENT.toString())) {
                event.setState(StateEnum.PUBLISHED);
            }
            if (updateEventAdminRequestDto.getStateAction().equals(StateActionEnum.REJECT_EVENT.toString())) {
                event.setState(StateEnum.CANCELED);
            }
        }
        if (updateEventAdminRequestDto.getTitle() != null) {
            event.setTitle(updateEventAdminRequestDto.getTitle());
        }

        eventStorage.save(event);
        log.info("обновлено событие: {}", event);
        return UtilitMapper.toEventFullDto(event,
                eventRequestStorage.countByStatusAndEvent_Id(EventRequestStatusEnum.CONFIRMED, eventId),
                statisticModuleClient.getCountViewsOfHit(String.join("", "/events/",
                        eventId.toString())));
    }
}
