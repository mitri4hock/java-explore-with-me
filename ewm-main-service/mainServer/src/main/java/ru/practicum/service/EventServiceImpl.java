package ru.practicum.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.client.StatisticModuleClient;
import ru.practicum.dto.*;
import ru.practicum.enums.EventRequestStatusEnum;
import ru.practicum.enums.SortEnum;
import ru.practicum.enums.StateActionEnum;
import ru.practicum.enums.StateEnum;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.ErrorDtoUtil;
import ru.practicum.exception.NotFoundException;
import ru.practicum.mapper.UtilitMapper;
import ru.practicum.model.Category;
import ru.practicum.model.Event;
import ru.practicum.model.User;
import ru.practicum.storage.CategoriesStorage;
import ru.practicum.storage.EventRequestStorage;
import ru.practicum.storage.EventStorage;
import ru.practicum.storage.UserStorage;
import ru.practicum.util.UtilClass;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional(readOnly = true)
@AllArgsConstructor
public class EventServiceImpl implements EventService {

    private final EventStorage eventStorage;
    private final UserStorage userStorage;
    private final CategoriesStorage categoriesStorage;
    private final EventRequestStorage eventRequestStorage;
    private final StatisticModuleClient statisticModuleClient;

    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(UtilClass.FORMAT_DATE);

    @Override
    @Transactional
    public EventFullDto createEvent(Long userId, NewEventDto newEventDto) {
        User user = userStorage.findById(userId).orElseThrow(() -> {
            log.info("запрошено добавление события несуществующим пользователем с id={}", userId);
            throw new NotFoundException(String.join("", "User with id=", userId.toString(),
                    " was not found"), new ErrorDtoUtil("The required object was not found.",
                    LocalDateTime.now()));
        });
        Category category = categoriesStorage.findById(newEventDto.getCategory()).orElseThrow(() -> {
            log.info("запрошено добавление события с несуществующей категорией с id={}", newEventDto.getCategory());
            throw new NotFoundException(String.join("", "Category with id=",
                    newEventDto.getCategory().toString(), " was not found"),
                    new ErrorDtoUtil("The required object was not found.", LocalDateTime.now()));
        });
        if (LocalDateTime.parse(newEventDto.getEventDate(), dateTimeFormatter)
                .isBefore(LocalDateTime.now().plusHours(2))) {
            log.info("Попытка создать событие со временем старта неудовлетворяющем логике прилоения (сейчас + 2 часа)." +
                    " EventDate={}", newEventDto.getEventDate());
            throw new ConflictException(String.join("", "Field: eventDate. Error: должно содержать" +
                    " дату, которая еще не наступила. Value: ", newEventDto.getEventDate()),
                    new ErrorDtoUtil("For the requested operation the conditions are not met.",
                            LocalDateTime.now()));
        }
        Event event = UtilitMapper.toEvent(newEventDto, category, user);
        eventStorage.save(event);
        log.info("создано новое событие: {}", event);
        return UtilitMapper.toEventFullDto(event, 0L, 0L);
    }

    @Override
    @Transactional
    public EventFullDto patchEvent(Long userId, Long eventId, UpdateEventUserRequestDto updateEventUserRequestDto) {
        if (userStorage.findById(userId).isEmpty()) {
            log.info("запрошено изменение события несуществующим пользователем с id={}", userId);
            throw new NotFoundException(String.join("", "User with id=", userId.toString(),
                    " was not found"), new ErrorDtoUtil("The required object was not found.",
                    LocalDateTime.now()));
        }
        Event event = eventStorage.findById(eventId).orElseThrow(() -> {
            log.info("запрошено изменение несуществующего события с id={}", eventId);
            throw new NotFoundException(String.join("", "Event with id=", eventId.toString(),
                    " was not found"), new ErrorDtoUtil("The required object was not found.",
                    LocalDateTime.now()));
        });
        if (updateEventUserRequestDto.getEventDate() != null &&
                LocalDateTime.parse(updateEventUserRequestDto.getEventDate(), dateTimeFormatter)
                        .isBefore(LocalDateTime.now().plusHours(2))) {
            log.info("Попытка обновить событие со временем старта неудовлетворяющем логике прилоения (сейчас + 2 часа)." +
                    " EventDate={}", updateEventUserRequestDto.getEventDate());
            throw new ConflictException(String.join("", "Field: eventDate. Error: должно содержать" +
                    " дату, которая еще не наступила. Value: ", updateEventUserRequestDto.getEventDate()),
                    new ErrorDtoUtil("For the requested operation the conditions are not met.",
                            LocalDateTime.now()));
        }
        if (event.getState().equals(StateEnum.PENDING) || event.getState().equals(StateEnum.CANCELED)) {
            if (updateEventUserRequestDto.getAnnotation() != null) {
                event.setAnnotation(updateEventUserRequestDto.getAnnotation());
            }
            if (updateEventUserRequestDto.getCategory() != null) {
                Category newCategory = categoriesStorage.findById(updateEventUserRequestDto.getCategory())
                        .orElseThrow(() -> {
                            log.info("запрошено bpvtytybизменение события на несуществующую категорию с id={}",
                                    updateEventUserRequestDto.getCategory());
                            throw new NotFoundException(String.join("", "Category with id=",
                                    updateEventUserRequestDto.getCategory().toString(), " was not found"),
                                    new ErrorDtoUtil("The required object was not found.",
                                            LocalDateTime.now()));
                        });
                event.setCategory(newCategory);
            }
            if (updateEventUserRequestDto.getDescription() != null) {
                event.setDescription(updateEventUserRequestDto.getDescription());
            }
            if (updateEventUserRequestDto.getEventDate() != null) {
                event.setEventDate(LocalDateTime.parse(updateEventUserRequestDto.getEventDate(),
                        dateTimeFormatter));
            }
            if (updateEventUserRequestDto.getLocation() != null) {
                event.setLocation(updateEventUserRequestDto.getLocation());
            }
            if (updateEventUserRequestDto.getPaid() != null) {
                event.setPaid(updateEventUserRequestDto.getPaid());
            }
            if (updateEventUserRequestDto.getParticipantLimit() != null) {
                event.setParticipantLimit(updateEventUserRequestDto.getParticipantLimit());
            }
            if (updateEventUserRequestDto.getRequestModeration() != null) {
                event.setRequestModeration(updateEventUserRequestDto.getRequestModeration());
            }
            if (updateEventUserRequestDto.getTitle() != null) {
                event.setTitle(updateEventUserRequestDto.getTitle());
            }
            if (updateEventUserRequestDto.getStateAction() != null) {
                StateActionEnum stateActionEnum = StateActionEnum.valueOf(updateEventUserRequestDto
                        .getStateAction().toUpperCase());
                if (stateActionEnum.equals(StateActionEnum.SEND_TO_REVIEW)) {
                    event.setState(StateEnum.PENDING);
                } else if (stateActionEnum.equals(StateActionEnum.CANCEL_REVIEW)) {
                    event.setState(StateEnum.CANCELED);
                }
            }
        } else {
            log.info("запрошено изменение события находящегося не на ожидании модерации и не отменённое. id={}",
                    eventId);
            throw new NotFoundException("Only pending or canceled events can be changed",
                    new ErrorDtoUtil("For the requested operation the conditions are not met.",
                            LocalDateTime.now()));
        }

        eventStorage.save(event);
        log.info("обновлено событие: {}", event);
        return UtilitMapper.toEventFullDto(event,
                eventRequestStorage.countByStatusAndEvent_Id(EventRequestStatusEnum.CONFIRMED, eventId),
                statisticModuleClient.getCountViewsOfHit(String.join("", "/events/",
                        eventId.toString())));
    }

    @Override
    public List<EventShortDto> getEventForUser(Long userId, Integer from, Integer size) {
        Sort sortBy = Sort.by(Sort.Order.desc("id"));
        Pageable page = PageRequest.of(from / size, size, sortBy);
        List<Event> rez = eventStorage.findByInitiator_Id(userId, page);
        return rez.stream()
                .map(x -> UtilitMapper.toEventShortDto(x,
                        eventRequestStorage.countByStatusAndEvent_Id(EventRequestStatusEnum.CONFIRMED,
                                x.getId()),
                        statisticModuleClient.getCountViewsOfHit(String.join("", "/events/",
                                x.getId().toString()))))
                .collect(Collectors.toList());
    }

    @Override
    public EventFullDto findEventCreatedByUser(Long userId, Long eventId) {
        if (userStorage.findById(userId).isEmpty()) {
            log.info("запрошено изменение события несуществующим пользователем с id={}", userId);
            throw new NotFoundException(String.join("", "User with id=", userId.toString(),
                    " was not found"), new ErrorDtoUtil("The required object was not found.",
                    LocalDateTime.now()));
        }
        Event event = eventStorage.findById(eventId).orElseThrow(() -> {
            log.info("запрошено изменение несуществующего события с id={}", eventId);
            throw new NotFoundException(String.join("", "Event with id=", eventId.toString(),
                    " was not found"), new ErrorDtoUtil("The required object was not found.",
                    LocalDateTime.now()));
        });
        if (event.getInitiator().getId() != userId) {
            log.info("Пользователем с Id={} запрошен просмотре не своего события с Id={}", userId, eventId);
            throw new NotFoundException(String.join("", "User with id=", userId.toString(),
                    " have not event with id=", eventId.toString()), new ErrorDtoUtil("The required object was not found.",
                    LocalDateTime.now()));
        }
        return UtilitMapper.toEventFullDto(event,
                eventRequestStorage.countByStatusAndEvent_Id(EventRequestStatusEnum.CONFIRMED,
                        eventId),
                statisticModuleClient.getCountViewsOfHit(String.join("", "/events/",
                        eventId.toString())));
    }

    @Override
    public EventFullDto findPublishedEvent(Long id, HttpServletRequest request) {
        var rez = eventStorage.findByIdAndState(id, StateEnum.PUBLISHED).orElseThrow(() -> {
            log.info("Опубликованное событие не найдено. id={}", id);
            throw new NotFoundException(String.join("", "User with id=", id.toString(),
                    " was not found"), new ErrorDtoUtil("The required object was not found.",
                    LocalDateTime.now()));
        });
        statisticModuleClient.postRequestToHit(request);
        return UtilitMapper.toEventFullDto(rez,
                eventRequestStorage.countByStatusAndEvent_Id(EventRequestStatusEnum.CONFIRMED,
                        rez.getId()),
                statisticModuleClient.getCountViewsOfHit(String.join("", "/events/",
                        rez.getId().toString())));
    }

    @Override
    public List<ParticipationRequestDto> getParticipationForUser(Long userId, Long eventId) {
        if (userStorage.findById(userId).isEmpty()) {
            log.info("запрошено наличие заявки в события несуществующим пользователем с id={}", userId);
            throw new NotFoundException(String.join("", "User with id=", userId.toString(),
                    " was not found"), new ErrorDtoUtil("The required object was not found.",
                    LocalDateTime.now()));
        }
        if (eventStorage.findById(eventId).isEmpty()) {
            log.info("запрошено наличие заявки в несуществующее событие с id={}", eventId);
            throw new NotFoundException(String.join("", "Event with id=", eventId.toString(),
                    " was not found"), new ErrorDtoUtil("The required object was not found.",
                    LocalDateTime.now()));
        }
        var rez = eventRequestStorage.findByRequester_IdAndEvent_IdOrderByCreatedDesc(userId,
                eventId);
        return rez.stream()
                .map(UtilitMapper::toParticipationRequestDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<EventFullDto> findEventByAdmin(ArrayList<Long> users, ArrayList<String> states,
                                               ArrayList<Long> categories, LocalDateTime rangeStart,
                                               LocalDateTime rangeEnd, Integer from, Integer size) {
        Sort sortBy = Sort.by(Sort.Order.asc("id"));
        Pageable page = PageRequest.of(from / size, size, sortBy);
        var statesEnum = states.stream()
                .map(StateEnum::valueOf).collect(Collectors.toList());

        var rez = eventStorage.findByInitiator_IdInAndStateInAndCategory_IdInAndEventDateBetween(users,
                statesEnum, categories, rangeStart, rangeEnd, page);

        return rez.stream()
                .map(x -> UtilitMapper.toEventFullDto(x,
                        eventRequestStorage.countByStatusAndEvent_Id(EventRequestStatusEnum.CONFIRMED,
                                x.getId()),
                        statisticModuleClient.getCountViewsOfHit(String.join("", "/events/",
                                x.getId().toString()))))
                .collect(Collectors.toList());
    }

    @Override
    public List<EventShortDto> findEventByUser(String text, ArrayList<Long> categories, Boolean paid,
                                               LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                               Boolean onlyAvailable, SortEnum sortEnum, Integer from, Integer size,
                                               HttpServletRequest request) {
        if (rangeStart.equals(LocalDateTime.parse("0001-01-01-01 01:01:01", dateTimeFormatter))) {
            rangeStart = LocalDateTime.now();
        }

        Sort sortBy = Sort.by(Sort.Order.asc("eventDate"));
        Pageable page = PageRequest.of(from / size, size, sortBy);
        List<Event> rez = eventStorage.findEventsByUsers(text, text, categories, paid,
                rangeStart, rangeEnd, StateEnum.PUBLISHED, page);
        List<Event> preRez;
        if (sortEnum.equals(SortEnum.VIEWS)) {
            preRez = rez.stream()
                    .sorted((x, y) -> {
                        return statisticModuleClient.getCountViewsOfHit(String.join("", "/events/",
                                x.getId().toString())).compareTo(statisticModuleClient.getCountViewsOfHit(String.join("",
                                "/events/", y.getId().toString())));
                    })
                    .collect(Collectors.toList());
        } else {
            preRez = rez;
        }
        if (Boolean.TRUE.equals(onlyAvailable)) {
            rez = preRez.stream().filter(x -> x.getParticipantLimit() > eventRequestStorage
                            .countByStatusAndEvent_Id(EventRequestStatusEnum.CONFIRMED, x.getId()))
                    .collect(Collectors.toList());
        } else {
            rez = preRez;
        }
        statisticModuleClient.postRequestToHit(request);
        return rez.stream()
                .map(x -> UtilitMapper.toEventShortDto(x,
                        eventRequestStorage.countByStatusAndEvent_Id(EventRequestStatusEnum.CONFIRMED,
                                x.getId()),
                        statisticModuleClient.getCountViewsOfHit(String.join("", "/events/",
                                x.getId().toString()))))
                .collect(Collectors.toList());
    }
}
