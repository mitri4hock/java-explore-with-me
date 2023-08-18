package ru.practicum.storage;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.model.Comment;

import java.util.List;

@Repository
public interface CommentsRepository extends JpaRepository<Comment, Long> {
    List<Comment> removeById(Long id);

    List<Comment> findByCommentatorIdOrderByCreatedDateDesc(Long commentatorId, Pageable pageable);

    List<Comment> findByEventIdOrderByCreatedDateDesc(Long eventId, Pageable pageable);
}
