package ru.practicum.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.enums.EventRequestStatusEnum;
import ru.practicum.model.EventRequest;

import java.util.List;
import java.util.Optional;

@Repository
public interface RequestRepository extends JpaRepository<EventRequest, Long> {

    Optional<EventRequest> findByEventIdAndRequesterId(Long eventId, Long requesterId);

    Optional<EventRequest> findByEventIdAndId(Long eventId, Long requestId);


    long countByEventIdAndStatus(Long id, EventRequestStatusEnum status);

    List<EventRequest> findByRequesterIdOrderByCreatedDesc(Long id);

    List<EventRequest> findByEventIdAndStatus(Long eventId, EventRequestStatusEnum status);

    @Query("select e.event.id from EventRequest e " +
            " where e.requester.id = :userId " +
            " and e.status = :status " +
            " order by e.event.id")
    List<Long> findByRequesterIdAndStatusOrderByEventIdAsc(@Param("userId") Long userId,
                                                           @Param("status") EventRequestStatusEnum status);
}
