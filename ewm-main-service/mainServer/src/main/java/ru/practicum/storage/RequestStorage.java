package ru.practicum.storage;

import org.springframework.data.jpa.repository.JpaRepository;
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
}
