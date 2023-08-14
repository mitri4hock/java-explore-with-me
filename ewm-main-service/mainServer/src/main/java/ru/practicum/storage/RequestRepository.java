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

    Optional<EventRequest> findByEvent_IdAndRequester_Id(Long eventId, Long requesterId);

    Optional<EventRequest> findByEvent_IdAndId(Long eventId, Long requestId);


    long countByEvent_IdAndStatus(Long id, EventRequestStatusEnum status);

    List<EventRequest> findByRequester_IdOrderByCreatedDesc(Long id);

    List<EventRequest> findByEvent_IdAndStatus(Long eventId, EventRequestStatusEnum status);

    @Query("select e.event.id from EventRequest e " +
            " where e.requester.id = :userId " +
            " and e.status = :status " +
            " order by e.event.id")
    List<Long> findByRequester_IdAndStatusOrderByEvent_IdAsc(@Param("userId") Long userId,
                                                             @Param("status") EventRequestStatusEnum status);
}
