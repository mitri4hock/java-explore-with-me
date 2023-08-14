package ru.practicum.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.enums.EventRequestStatusEnum;
import ru.practicum.model.EventRequest;

import java.util.List;

@Repository
public interface EventRequestRepository extends JpaRepository<EventRequest, Long> {

    long countByStatusAndEvent_Id(EventRequestStatusEnum status, Long id);

    List<EventRequest> findByEvent_IdAndEvent_Initiator_IdOrderByCreatedDesc(Long eventId, Long inicializatorId);
}
