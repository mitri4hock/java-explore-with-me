package ru.practicum.service;

import ru.practicum.dto.CommentDto;
import ru.practicum.dto.CreatePatchCommentDto;

import java.util.List;

public interface CommentsService {
    CommentDto createComment(Long eventId, Long commentatorId, CreatePatchCommentDto createPatchCommentDto);

    CommentDto patchCommentByOwner(Long commentId, Long commentatorId, CreatePatchCommentDto createPatchCommentDto);

    CommentDto deleteCommentByOwner(Long commentId, Long commentatorId);

    List<CommentDto> findAllMyComment(Long commentatorId, Integer from, Integer size);

    List<CommentDto> findAllCommentsForEvent(Long eventId, Integer from, Integer size);

    CommentDto findComment(Long commentId);

    CommentDto patchCommentByAdmin(Long commentId, CreatePatchCommentDto createPatchCommentDto);

    CommentDto deleteCommentByAdmin(Long commentId);
}
