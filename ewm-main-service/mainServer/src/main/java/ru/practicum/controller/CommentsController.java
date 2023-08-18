package ru.practicum.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.CommentDto;
import ru.practicum.dto.CreatePatchCommentDto;
import ru.practicum.service.CommentsService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RequestMapping("")
@RestController
@AllArgsConstructor
@Validated
public class CommentsController {

    private final CommentsService commentsService;

    @Operation(summary = "Cоздание комментария. Комментарий можно написать только на опубликованное событие")
    @PostMapping("/comment/{eventId}/{commentatorId}")
    @ResponseStatus(HttpStatus.CREATED)
    public CommentDto createComment(@PathVariable @PositiveOrZero Long eventId,
                                    @PathVariable @PositiveOrZero Long commentatorId,
                                    @RequestBody @Valid CreatePatchCommentDto createPatchCommentDto) {
        return commentsService.createComment(eventId, commentatorId, createPatchCommentDto);
    }

    @Operation(summary = "обновление комментария самм создавшим комментарий пользователем. Обновить моно" +
            " только свой комментарий Событие до сих пор должно быть опубликаовано")
    @PatchMapping("/comment/{commentId}/{commentatorId}")
    @ResponseStatus(HttpStatus.OK)
    public CommentDto patchCommentByOwner(@PathVariable @PositiveOrZero Long commentId,
                                          @PathVariable @PositiveOrZero Long commentatorId,
                                          @RequestBody @Valid CreatePatchCommentDto createPatchCommentDto) {
        return commentsService.patchCommentByOwner(commentId, commentatorId, createPatchCommentDto);
    }

    @Operation(summary = "правка комментария админом")
    @PatchMapping("/admin/comment/{commentId}")
    @ResponseStatus(HttpStatus.OK)
    public CommentDto patchCommentByAdmin(@PathVariable @PositiveOrZero Long commentId,
                                          @RequestBody @Valid CreatePatchCommentDto createPatchCommentDto) {
        return commentsService.patchCommentByAdmin(commentId, createPatchCommentDto);
    }

    @Operation(summary = "удаление комментария владельцем")
    @DeleteMapping("/comment/{commentId}/{commentatorId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public CommentDto deleteCommentByOwner(@PathVariable @PositiveOrZero Long commentId,
                                           @PathVariable @PositiveOrZero Long commentatorId) {
        return commentsService.deleteCommentByOwner(commentId, commentatorId);
    }

    @Operation(summary = "удаление комментария админом")
    @DeleteMapping("/admin/comment/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public CommentDto deleteCommentByAdmin(@PathVariable @PositiveOrZero Long commentId) {
        return commentsService.deleteCommentByAdmin(commentId);
    }

    @Operation(summary = "получить все комментарии пользователя")
    @GetMapping("/comment/all/{commentatorId}")
    @ResponseStatus(HttpStatus.OK)
    public List<CommentDto> findAllMyComment(@PathVariable @PositiveOrZero Long commentatorId,
                                             @RequestParam(value = "from", defaultValue = "0") @PositiveOrZero Integer from,
                                             @RequestParam(value = "size", defaultValue = "10") @Positive Integer size) {
        return commentsService.findAllMyComment(commentatorId, from, size);
    }

    @Operation(summary = "получить все комменты для конкретного события")
    @GetMapping("/comment/all/event/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public List<CommentDto> findAllCommentsForEvent(@PathVariable @PositiveOrZero Long eventId,
                                                    @RequestParam(value = "from", defaultValue = "0") @PositiveOrZero Integer from,
                                                    @RequestParam(value = "size", defaultValue = "10") @Positive Integer size) {
        return commentsService.findAllCommentsForEvent(eventId, from, size);
    }

    @Operation(summary = "получение конкретного комментария любым пользователем")
    @GetMapping("/comment/{commentId}")
    @ResponseStatus(HttpStatus.OK)
    public CommentDto findComment(@PathVariable @PositiveOrZero Long commentId) {
        return commentsService.findComment(commentId);
    }
}

