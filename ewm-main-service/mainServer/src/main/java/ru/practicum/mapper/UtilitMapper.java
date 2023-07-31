package ru.practicum.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.dto.EventFullDto;
import ru.practicum.dto.EventShortDto;
import ru.practicum.dto.NewEventDto;
import ru.practicum.dto.ParticipationRequestDto;
import ru.practicum.enums.StateEnum;
import ru.practicum.model.Category;
import ru.practicum.model.Event;
import ru.practicum.model.EventRequest;
import ru.practicum.model.User;
import ru.practicum.util.UtilClass;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@UtilityClass
public class UtilitMapper {
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(UtilClass.FORMAT_DATE);

    public Event toEvent(NewEventDto newEventDto, Category category, User initiator) {
        Event result = new Event();

        result.setAnnotation(newEventDto.getAnnotation());
        result.setCategory(category);
        result.setCreatedOn(LocalDateTime.now());
        result.setDescription(newEventDto.getDescription());
        result.setEventDate(LocalDateTime.parse(newEventDto.getEventDate(), dateTimeFormatter));
        result.setInitiator(initiator);
        result.setLocation(newEventDto.getLocation());
        result.setPaid(newEventDto.getPaid());
        result.setParticipantLimit(newEventDto.getParticipantLimit());
        result.setRequestModeration(newEventDto.getRequestModeration());
        result.setState(StateEnum.PENDING);
        result.setTitle(newEventDto.getTitle());

        return result;
    }

    public EventFullDto toEventFullDto(Event event, Long confirmedRequests, Long views) {
        EventFullDto result = new EventFullDto();

        String publishedOn = null;
        if (event.getRequestModeration() == false) {
            publishedOn = LocalDateTime.now().format(dateTimeFormatter);
        }

        result.setId(event.getId());
        result.setAnnotation(event.getAnnotation());
        result.setCategory(CustomMapper.INSTANCE.toCategoryDto(event.getCategory()));
        result.setConfirmedRequests(confirmedRequests);
        result.setCreatedOn(event.getCreatedOn().format(dateTimeFormatter));
        result.setDescription(event.getDescription());
        result.setEventDate(event.getEventDate().format(dateTimeFormatter));
        result.setInitiator(CustomMapper.INSTANCE.toUserSortDto(event.getInitiator()));
        result.setLocation(event.getLocation());
        result.setPaid(event.getPaid());
        result.setParticipantLimit(event.getParticipantLimit());
        result.setPublishedOn(publishedOn);
        result.setRequestModeration(event.getRequestModeration());
        result.setState(event.getState().toString());
        result.setTitle(event.getTitle());
        result.setViews(views);

        return result;
    }

    public EventShortDto toEventShortDto(Event event, Long confirmedRequests, Long views) {
        EventShortDto result = new EventShortDto();

        result.setId(event.getId());
        result.setAnnotation(event.getAnnotation());
        result.setCategory(CustomMapper.INSTANCE.toCategoryDto(event.getCategory()));
        result.setConfirmedRequests(confirmedRequests);
        result.setDescription(event.getDescription());
        result.setEventDate(event.getEventDate().format(dateTimeFormatter));
        result.setInitiator(CustomMapper.INSTANCE.toUserSortDto(event.getInitiator()));
        result.setPaid(event.getPaid());
        result.setTitle(event.getTitle());
        result.setViews(views);

        return result;
    }

    public ParticipationRequestDto toParticipationRequestDto(EventRequest eventRequest) {
        ParticipationRequestDto rez = new ParticipationRequestDto();

        rez.setCreated(eventRequest.getCreated().format(dateTimeFormatter));
        rez.setEvent(eventRequest.getEvent().getId());
        rez.setId(eventRequest.getId());
        rez.setRequester(eventRequest.getRequester().getId());
        rez.setStatus(eventRequest.getStatus().name());

        return rez;
    }
}






























