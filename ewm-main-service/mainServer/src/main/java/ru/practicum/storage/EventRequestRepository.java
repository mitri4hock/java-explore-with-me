package ru.practicum.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.enums.EventRequestStatusEnum;
import ru.practicum.model.EventRequest;

import java.util.List;

@Repository
public interface EventRequestRepository extends JpaRepository<EventRequest, Long> {

    long countByStatusAndEventId(EventRequestStatusEnum status, Long id);

    List<EventRequest> findByEventIdAndEventInitiatorIdOrderByCreatedDesc(Long eventId, Long inicializatorId);
}
