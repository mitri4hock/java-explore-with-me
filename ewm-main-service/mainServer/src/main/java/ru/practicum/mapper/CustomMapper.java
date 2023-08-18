package ru.practicum.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import ru.practicum.dto.*;
import ru.practicum.enums.EventRequestStatusEnum;
import ru.practicum.enums.StateEnum;
import ru.practicum.model.*;
import ru.practicum.util.UtilClass;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Mapper
public interface CustomMapper {
    CustomMapper INSTANCE = Mappers.getMapper(CustomMapper.class);

    Category toCategory(CategoryDto categoryDto);

    CategoryDto toCategoryDto(Category category);

    User toUser(UserDto userDto);

    UserDto toUserDto(User user);

    UserSortDto toUserSortDto(User user);

    Compilation toCompilation(NewCompilationDto newCompilationDto);

    default CommentDto toCommentDto(Comment comment) {
        CommentDto rez = new CommentDto();

        rez.setId(comment.getId());
        rez.setEventId(comment.getEvent().getId());
        rez.setCommentatorId((comment.getCommentator().getId()));
        rez.setCommentText(comment.getCommentText());
        rez.setCreatedDate(comment.getCreatedDate());

        return rez;
    }

    default Event toEvent(NewEventDto newEventDto, Category category, User initiator) {
        final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(UtilClass.FORMAT_DATE);
        Event result = new Event();

        result.setAnnotation(newEventDto.getAnnotation());
        result.setCategory(category);
        result.setCreatedOn(LocalDateTime.now());
        result.setDescription(newEventDto.getDescription());
        result.setEventDate(LocalDateTime.parse(newEventDto.getEventDate(), dateTimeFormatter));
        result.setInitiator(initiator);
        result.setLocation(newEventDto.getLocation());
        result.setPaid(newEventDto.getPaid() == null ? false : newEventDto.getPaid());
        result.setParticipantLimit(newEventDto.getParticipantLimit() == null ? 0 : newEventDto.getParticipantLimit());
        result.setRequestModeration(newEventDto.getRequestModeration() == null ? true : newEventDto.getRequestModeration());
        result.setState(StateEnum.PENDING);
        result.setTitle(newEventDto.getTitle());

        return result;
    }

    default EventFullDto toEventFullDto(Event event, Long confirmedRequests, Long views) {
        final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(UtilClass.FORMAT_DATE);
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

    default EventShortDto toEventShortDto(Event event, Long confirmedRequests, Long views) {
        final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(UtilClass.FORMAT_DATE);
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

    default ParticipationRequestDto toParticipationRequestDto(EventRequest eventRequest,
                                                              boolean isRejectedStatusIsCanceled) { // из-за тестов ПОСТМАН - в разных тестах должно быть разное именование для одного и того-же
        final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(UtilClass.FORMAT_DATE);
        ParticipationRequestDto rez = new ParticipationRequestDto();

        rez.setCreated(eventRequest.getCreated().format(dateTimeFormatter));
        rez.setEvent(eventRequest.getEvent().getId());
        rez.setId(eventRequest.getId());
        rez.setRequester(eventRequest.getRequester().getId());
        if (isRejectedStatusIsCanceled == true) {
            rez.setStatus(eventRequest.getStatus().equals(EventRequestStatusEnum.REJECTED) ? "CANCELED" :
                    eventRequest.getStatus().name());
        } else {
            rez.setStatus(eventRequest.getStatus().name());
        }

        return rez;
    }

    default CompilationDto toCompilationDto(Compilation compilation, List<EventShortDto> listEventShortDto) {
        CompilationDto rez = new CompilationDto();
        rez.setId(compilation.getId());
        rez.setPinned(compilation.getPinned());
        rez.setTitle(compilation.getTitle());
        rez.setEvents(listEventShortDto);
        return rez;
    }
}
