package ru.practicum.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.CommentDto;
import ru.practicum.dto.CreatePatchCommentDto;
import ru.practicum.enums.StateEnum;
import ru.practicum.exception.BadParametrException;
import ru.practicum.exception.ErrorDtoUtil;
import ru.practicum.exception.NotFoundException;
import ru.practicum.mapper.CustomMapper;
import ru.practicum.model.Comment;
import ru.practicum.model.Event;
import ru.practicum.model.User;
import ru.practicum.storage.CommentsRepository;
import ru.practicum.storage.EventRepository;
import ru.practicum.storage.UserRepository;
import ru.practicum.util.UtilClass;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional(readOnly = true)
@AllArgsConstructor
public class CommentsServiceImpl implements CommentsService {

    private final CommentsRepository commentsRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(UtilClass.FORMAT_DATE);

    @Override
    @Transactional
    public CommentDto createComment(Long eventId, Long commentatorId,
                                    CreatePatchCommentDto createPatchCommentDto) {
        Event event = eventRepository.findById(eventId).orElseThrow(() -> {
            log.info("Попытка прокомментировать отсутствующее событие. Id={}", eventId);
            throw new NotFoundException(String.join("", "Event with id=", eventId.toString(),
                    " was not found"),
                    new ErrorDtoUtil("The required object was not found.", LocalDateTime.now()));
        });
        if (!event.getState().equals(StateEnum.PUBLISHED)) {
            log.info("Попытка прокомментировать не опубликованное событие. Id={}", eventId);
            throw new NotFoundException(String.join("", "Event with id=", eventId.toString(),
                    " was not published"),
                    new ErrorDtoUtil("The required object was not found.", LocalDateTime.now()));
        }
        User commentator = userRepository.findById(commentatorId).orElseThrow(() -> {
            log.info("Попытка комментировать событие отсутствующем пользователем. UserId={}", commentatorId);
            throw new NotFoundException(String.join("", "User with id=", commentatorId.toString(),
                    " was not found"),
                    new ErrorDtoUtil("The required object was not found.", LocalDateTime.now()));
        });

        Comment newComment = new Comment();
        newComment.setEvent(event);
        newComment.setCommentator(commentator);
        newComment.setCommentText(createPatchCommentDto.getCommentText());
        newComment.setCreatedDate(LocalDateTime.now());
        commentsRepository.save(newComment);
        log.info("создан комментарий: {}", newComment);

        return CustomMapper.INSTANCE.toCommentDto(newComment);
    }

    @Override
    @Transactional
    public CommentDto patchCommentByOwner(Long commentId, Long commentatorId, CreatePatchCommentDto createPatchCommentDto) {
        Comment comment = commentsRepository.findById(commentId).orElseThrow(() -> {
            log.info("Попытка изменить несуществующий комментарий. CommentId={}", commentId);
            throw new NotFoundException(String.join("", "Comment with id=", commentId.toString(),
                    " was not found"),
                    new ErrorDtoUtil("The required object was not found.", LocalDateTime.now()));
        });
        // следующая проверка на-всякий-случай. По большому счёту- она проверяет целостность БД.
        userRepository.findById(commentatorId).orElseThrow(() -> {
            log.info("Попытка изменить комментарий от несуществующего пользователя. UserId={}", commentatorId);
            throw new NotFoundException(String.join("", "User with id=", commentatorId.toString(),
                    " was not found"),
                    new ErrorDtoUtil("The required object was not found.", LocalDateTime.now()));
        });
        if (!comment.getCommentator().getId().equals(commentatorId)) {
            log.info("запрошено изменение комментария не его владельцем");
            throw new BadParametrException("запрошено изменение комментария не его владельцем",
                    new ErrorDtoUtil("Bad Param query",
                            LocalDateTime.now()));
        }
        // следующая проверка на-всякий-случай. По большому счёту- она проверяет целостность БД.
        Event event = eventRepository.findById(comment.getEvent().getId()).orElseThrow(() -> {
            log.info("Попытка изменить комментарий отсутствующего события. Id={}", comment.getEvent().getId());
            throw new NotFoundException(String.join("", "Event with id=", comment.getEvent().getId().toString(),
                    " was not found"),
                    new ErrorDtoUtil("The required object was not found.", LocalDateTime.now()));
        });
        if (!event.getState().equals(StateEnum.PUBLISHED)) {
            log.info("Попытка прокомментировать не опубликованное событие. Id={}", comment.getEvent().getId());
            throw new NotFoundException(String.join("", "Event with id=",
                    comment.getEvent().getId().toString(),
                    " was not published"),
                    new ErrorDtoUtil("The required object was not found.", LocalDateTime.now()));
        }

        comment.setCommentText(createPatchCommentDto.getCommentText());
        log.info("комментарий изменён: {}", comment);

        return CustomMapper.INSTANCE.toCommentDto(comment);
    }

    @Override
    @Transactional
    public CommentDto patchCommentByAdmin(Long commentId, CreatePatchCommentDto createPatchCommentDto) {
        Comment comment = commentsRepository.findById(commentId).orElseThrow(() -> {
            log.info("Попытка изменить несуществующий комментарий. CommentId={}", commentId);
            throw new NotFoundException(String.join("", "Comment with id=", commentId.toString(),
                    " was not found"),
                    new ErrorDtoUtil("The required object was not found.", LocalDateTime.now()));
        });

        comment.setCommentText(createPatchCommentDto.getCommentText());
        log.info("сомментарий изменён админом: {}", comment);

        return CustomMapper.INSTANCE.toCommentDto(comment);
    }

    @Override
    @Transactional
    public CommentDto deleteCommentByAdmin(Long commentId) {
        commentsRepository.findById(commentId).orElseThrow(() -> {
            log.info("Попытка удалить несуществующий комментарий админом. CommentId={}", commentId);
            throw new NotFoundException(String.join("", "Comment with id=", commentId.toString(),
                    " was not found"),
                    new ErrorDtoUtil("The required object was not found.", LocalDateTime.now()));
        });

        var rez = commentsRepository.removeById(commentId);
        log.info("администратором удалено событие: {}", rez);

        return CustomMapper.INSTANCE.toCommentDto(rez.get(0));
    }

    @Override
    @Transactional
    public CommentDto deleteCommentByOwner(Long commentId, Long commentatorId) {
        Comment comment = commentsRepository.findById(commentId).orElseThrow(() -> {
            log.info("Попытка удалить несуществующий комментарий. CommentId={}", commentId);
            throw new NotFoundException(String.join("", "Comment with id=", commentId.toString(),
                    " was not found"),
                    new ErrorDtoUtil("The required object was not found.", LocalDateTime.now()));
        });
        if (!comment.getCommentator().getId().equals(commentatorId)) {
            log.info("запрошено удаление комментария не его владельцем");
            throw new BadParametrException("запрошено удаление комментария не его владельцем",
                    new ErrorDtoUtil("Bad Param query",
                            LocalDateTime.now()));
        }

        var rez = commentsRepository.removeById(commentId);
        log.info("удалено событие: {}", rez);

        return CustomMapper.INSTANCE.toCommentDto(rez.get(0));
    }

    @Override
    public List<CommentDto> findAllMyComment(Long commentatorId, Integer from, Integer size) {
        userRepository.findById(commentatorId).orElseThrow(() -> {
            log.info("Попытка получить все комментарии отсутствующем пользователем. UserId={}", commentatorId);
            throw new NotFoundException(String.join("", "User with id=", commentatorId.toString(),
                    " was not found"),
                    new ErrorDtoUtil("The required object was not found.", LocalDateTime.now()));
        });

        Sort sortBy = Sort.by(Sort.Order.asc("id"));
        Pageable page = PageRequest.of(from / size, size, sortBy);
        var rez = commentsRepository.findByCommentatorIdOrderByCreatedDateDesc(commentatorId, page);
        log.info("получили список всех комментариев для пользователя c Id {}", commentatorId);

        return rez.stream()
                .map(CustomMapper.INSTANCE::toCommentDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<CommentDto> findAllCommentsForEvent(Long eventId, Integer from, Integer size) {
        eventRepository.findById(eventId).orElseThrow(() -> {
            log.info("Попытка изменить комментарий отсутствующего события. Id={}", eventId);
            throw new NotFoundException(String.join("", "Event with id=", eventId.toString(),
                    " was not found"),
                    new ErrorDtoUtil("The required object was not found.", LocalDateTime.now()));
        });

        Sort sortBy = Sort.by(Sort.Order.asc("id"));
        Pageable page = PageRequest.of(from / size, size, sortBy);
        var rez = commentsRepository.findByEventIdOrderByCreatedDateDesc(eventId, page);
        log.info("получили все комментарии для события EventId={}", eventId);

        return rez.stream()
                .map(CustomMapper.INSTANCE::toCommentDto)
                .collect(Collectors.toList());
    }

    @Override
    public CommentDto findComment(Long commentId) {
        Comment comment = commentsRepository.findById(commentId).orElseThrow(() -> {
            log.info("Попытка изменить несуществующий комментарий. CommentId={}", commentId);
            throw new NotFoundException(String.join("", "Comment with id=", commentId.toString(),
                    " was not found"),
                    new ErrorDtoUtil("The required object was not found.", LocalDateTime.now()));
        });

        log.info("получили комментарий: {}", comment);
        return CustomMapper.INSTANCE.toCommentDto(comment);
    }
}
