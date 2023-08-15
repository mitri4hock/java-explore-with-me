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
import ru.practicum.exception.BadParametrException;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.ErrorDtoUtil;
import ru.practicum.exception.NotFoundException;
import ru.practicum.mapper.CustomMapper;
import ru.practicum.model.Category;
import ru.practicum.model.Event;
import ru.practicum.model.User;
import ru.practicum.storage.*;
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

    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final CategoriesRepository categoriesRepository;
    private final EventRequestRepository eventRequestRepository;
    private final StatisticModuleClient statisticModuleClient;
    private final CustomRepository customRepository;

    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(UtilClass.FORMAT_DATE);

    @Override
    @Transactional
    public EventFullDto createEvent(Long userId, NewEventDto newEventDto) {
        User user = userRepository.findById(userId).orElseThrow(() -> {
            log.info("запрошено добавление события несуществующим пользователем с id={}", userId);
            throw new NotFoundException(String.join("", "User with id=", userId.toString(),
                    " was not found"), new ErrorDtoUtil("The required object was not found.",
                    LocalDateTime.now()));
        });
        Category category = categoriesRepository.findById(newEventDto.getCategory()).orElseThrow(() -> {
            log.info("запрошено добавление события с несуществующей категорией с id={}", newEventDto.getCategory());
            throw new NotFoundException(String.join("", "Category with id=",
                    newEventDto.getCategory().toString(), " was not found"),
                    new ErrorDtoUtil("The required object was not found.", LocalDateTime.now()));
        });
        if (LocalDateTime.parse(newEventDto.getEventDate(), dateTimeFormatter)
                .isBefore(LocalDateTime.now().plusHours(2))) {
            log.info("Попытка создать событие со временем старта неудовлетворяющем логике прилоения (сейчас + 2 часа)." +
                    " EventDate={}", newEventDto.getEventDate());
            throw new BadParametrException(String.join("", "Field: eventDate. Error: должно содержать" +
                    " дату, которая еще не наступила. Value: ", newEventDto.getEventDate()),
                    new ErrorDtoUtil("For the requested operation the conditions are not met.",
                            LocalDateTime.now()));
        }
        Event event = CustomMapper.INSTANCE.toEvent(newEventDto, category, user);
        eventRepository.save(event);
        log.info("создано новое событие: {}", event);
        return CustomMapper.INSTANCE.toEventFullDto(event, 0L, 0L);
    }

    @Override
    @Transactional
    public EventFullDto patchEvent(Long userId, Long eventId, UpdateEventUserRequestDto updateEventUserRequestDto) {
        userRepository.findById(userId).orElseThrow(() -> {
            log.info("запрошено изменение события несуществующим пользователем с id={}", userId);
            throw new NotFoundException(String.join("", "User with id=", userId.toString(),
                    " was not found"), new ErrorDtoUtil("The required object was not found.",
                    LocalDateTime.now()));
        });
        Event event = eventRepository.findById(eventId).orElseThrow(() -> {
            log.info("запрошено изменение несуществующего события с id={}", eventId);
            throw new NotFoundException(String.join("", "Event with id=", eventId.toString(),
                    " was not found"), new ErrorDtoUtil("fThe required object was not found.",
                    LocalDateTime.now()));
        });
        if (updateEventUserRequestDto.getEventDate() != null &&
                LocalDateTime.parse(updateEventUserRequestDto.getEventDate(), dateTimeFormatter)
                        .isBefore(LocalDateTime.now().plusHours(2))) {
            log.info("Попытка обновить событие со временем старта неудовлетворяющем логике прилоения (сейчас + 2 часа)." +
                    " EventDate={}", updateEventUserRequestDto.getEventDate());
            throw new BadParametrException(String.join("", "Field: eventDate. Error: должно содержать" +
                    " дату, которая еще не наступила. Value: ", updateEventUserRequestDto.getEventDate()),
                    new ErrorDtoUtil("For the requested operation the conditions are not met.",
                            LocalDateTime.now()));
        }
        if (event.getState().equals(StateEnum.PENDING) || event.getState().equals(StateEnum.CANCELED)) {
            if (updateEventUserRequestDto.getAnnotation() != null) {
                event.setAnnotation(updateEventUserRequestDto.getAnnotation());
            }
            if (updateEventUserRequestDto.getCategory() != null) {
                Category newCategory = categoriesRepository.findById(updateEventUserRequestDto.getCategory())
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
            if (updateEventUserRequestDto.getDescription() != null &&
                    !updateEventUserRequestDto.getDescription().isBlank()) {
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
            if (updateEventUserRequestDto.getTitle() != null && !updateEventUserRequestDto.getTitle().isBlank()) {
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
            throw new ConflictException("Only pending or canceled events can be changed",
                    new ErrorDtoUtil("For the requested operation the conditions are not met.",
                            LocalDateTime.now()));
        }

        log.info("обновлено событие: {}", event);
        return CustomMapper.INSTANCE.toEventFullDto(event,
                eventRequestRepository.countByStatusAndEventId(EventRequestStatusEnum.CONFIRMED, eventId),
                statisticModuleClient.getCountViewsOfHit(String.join("", "/events/",
                        eventId.toString())));
    }

    @Override
    public List<EventShortDto> getEventForUser(Long userId, Integer from, Integer size) {
        Sort sortBy = Sort.by(Sort.Order.desc("id"));
        Pageable page = PageRequest.of(from / size, size, sortBy);
        List<Event> rez = eventRepository.findByInitiatorId(userId, page);
        return rez.stream()
                .map(x -> CustomMapper.INSTANCE.toEventShortDto(x,
                        eventRequestRepository.countByStatusAndEventId(EventRequestStatusEnum.CONFIRMED,
                                x.getId()),
                        statisticModuleClient.getCountViewsOfHit(String.join("", "/events/",
                                x.getId().toString()))))
                .collect(Collectors.toList());
    }

    @Override
    public EventFullDto findEventCreatedByUser(Long userId, Long eventId) {
        userRepository.findById(userId).orElseThrow(() -> {
            log.info("запрошено изменение события несуществующим пользователем с id={}", userId);
            throw new NotFoundException(String.join("", "User with id=", userId.toString(),
                    " was not found"), new ErrorDtoUtil("The required object was not found.",
                    LocalDateTime.now()));
        });
        Event event = eventRepository.findById(eventId).orElseThrow(() -> {
            log.info("запрошено изменение несуществующего события с id={}", eventId);
            throw new NotFoundException(String.join("", "Event with id=", eventId.toString(),
                    " was not found"), new ErrorDtoUtil("The required object was not found.",
                    LocalDateTime.now()));
        });
        if (!event.getInitiator().getId().equals(userId)) {
            log.info("Пользователем с Id={} запрошен просмотре не своего события с Id={}", userId, eventId);
            throw new NotFoundException(String.join("", "User with id=", userId.toString(),
                    " have not event with id=", eventId.toString()), new ErrorDtoUtil("The required object was not found.",
                    LocalDateTime.now()));
        }
        return CustomMapper.INSTANCE.toEventFullDto(event,
                eventRequestRepository.countByStatusAndEventId(EventRequestStatusEnum.CONFIRMED,
                        eventId),
                statisticModuleClient.getCountViewsOfHit(String.join("", "/events/",
                        eventId.toString())));
    }

    @Override
    public EventFullDto findPublishedEvent(Long id, HttpServletRequest request) {
        var rez = eventRepository.findByIdAndState(id, StateEnum.PUBLISHED)
                .orElseThrow(() -> {
                    log.info("Опубликованное событие не найдено. id={}", id);
                    throw new NotFoundException(String.join("", "Published Event with id=", id.toString(),
                            " was not found"), new ErrorDtoUtil("The required object was not found.",
                            LocalDateTime.now()));
                });
        statisticModuleClient.postRequestToHit(request);
        return CustomMapper.INSTANCE.toEventFullDto(rez,
                eventRequestRepository.countByStatusAndEventId(EventRequestStatusEnum.CONFIRMED,
                        rez.getId()),
                statisticModuleClient.getCountViewsOfHit(String.join("", "/events/",
                        rez.getId().toString())));
    }

    @Override
    public List<ParticipationRequestDto> getParticipationForUser(Long userId, Long eventId) {
        userRepository.findById(userId).orElseThrow(() -> {
            log.info("запрошено наличие заявки в события несуществующим пользователем с id={}", userId);
            throw new NotFoundException(String.join("", "User with id=", userId.toString(),
                    " was not found"), new ErrorDtoUtil("The required object was not found.",
                    LocalDateTime.now()));
        });
        var event = eventRepository.findById(eventId);
        if (event.isEmpty()) {
            log.info("запрошено наличие заявки в несуществующее событие с id={}", eventId);
            throw new NotFoundException(String.join("", "Event with id=", eventId.toString(),
                    " was not found"), new ErrorDtoUtil("The required object was not found.",
                    LocalDateTime.now()));
        }
        if (!event.get().getInitiator().getId().equals(userId)) {
            log.info("запрошено наличие заявки в не своём событие с id={}", eventId);
            throw new ConflictException(String.join("", "Event with id=", eventId.toString(),
                    " do not belong User with id=", userId.toString()),
                    new ErrorDtoUtil("conflict parameters",
                            LocalDateTime.now()));
        }
        var rez = eventRequestRepository.findByEventIdAndEventInitiatorIdOrderByCreatedDesc(
                eventId, userId);
        return rez.stream()
                .map(x -> CustomMapper.INSTANCE.toParticipationRequestDto(x, true))
                .collect(Collectors.toList());
    }

    @Override
    public List<EventFullDto> findEventByAdmin(ArrayList<Long> users, ArrayList<String> states,
                                               ArrayList<Long> categories, LocalDateTime rangeStart,
                                               LocalDateTime rangeEnd, Integer from, Integer size) {
        Sort sortBy = Sort.by(Sort.Order.asc("id"));
        Pageable page = PageRequest.of(from / size, size, sortBy);
        List<StateEnum> statesEnum = null;
        if (states != null) {
            statesEnum = states.stream()
                    .map(StateEnum::valueOf).collect(Collectors.toList());
        }
        var rez = customRepository.findEventByFilters(users,
                statesEnum, categories, rangeStart, rangeEnd, page);

        return rez.stream()
                .map(x -> CustomMapper.INSTANCE.toEventFullDto(x,
                        eventRequestRepository.countByStatusAndEventId(EventRequestStatusEnum.CONFIRMED,
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
        if (rangeStart.isEqual(LocalDateTime.parse("0001-01-01 01:01:01", dateTimeFormatter))) {
            rangeStart = LocalDateTime.now();
        }
        if (rangeStart.isAfter(rangeEnd)) {
            throw new BadParametrException("RangeStart should not be after that rangeEnd", new ErrorDtoUtil(
                    "The required object is bed", LocalDateTime.now())
            );
        }
        Sort sortBy = Sort.by(Sort.Order.desc("eventDate"));
        Pageable page = PageRequest.of(from / size, size, sortBy);
        List<Event> rez = customRepository.findEventsByUsers(text, categories, paid,
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
            rez = preRez.stream().filter(x -> x.getParticipantLimit() > eventRequestRepository
                            .countByStatusAndEventId(EventRequestStatusEnum.CONFIRMED, x.getId()))
                    .collect(Collectors.toList());
        } else {
            rez = preRez;
        }
        statisticModuleClient.postRequestToHit(request);
        return rez.stream()
                .map(x -> CustomMapper.INSTANCE.toEventShortDto(x,
                        eventRequestRepository.countByStatusAndEventId(EventRequestStatusEnum.CONFIRMED,
                                x.getId()),
                        statisticModuleClient.getCountViewsOfHit(String.join("", "/events/",
                                x.getId().toString()))))
                .collect(Collectors.toList());
    }
}
