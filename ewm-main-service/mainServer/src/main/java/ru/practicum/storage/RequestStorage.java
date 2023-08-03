package ru.practicum.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.enums.EventRequestStatusEnum;
import ru.practicum.model.EventRequest;

import java.util.List;
import java.util.Optional;

@Repository
public interface RequestStorage extends JpaRepository<EventRequest, Long> {

    Optional<EventRequest> findByEvent_IdAndRequester_Id(Long eventId, Long requesterId);

    long countByEvent_IdAndStatus(Long id, EventRequestStatusEnum status);

    @Override
    EventRequest save(EventRequest eventRequest);

    @Override
    Optional<EventRequest> findById(Long aLong);

    List<EventRequest> findByRequester_IdOrderByCreatedDesc(Long id);

    List<EventRequest> findByEvent_IdAndStatus(Long eventId, EventRequestStatusEnum status);

    @Query("select e.event.id from EventRequest e where e.requester.id = ?1 and e.status = ?2 order by e.event.id")
    List<Long> findByRequester_IdAndStatusOrderByEvent_IdAsc(Long userId, EventRequestStatusEnum status);
}
