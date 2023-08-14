package ru.practicum.storage;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.enums.StateEnum;
import ru.practicum.model.Event;

import java.util.List;
import java.util.Optional;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    List<Event> findByInitiator_Id(Long id, Pageable pageable);

    Optional<Event> findByIdAndState(Long id, StateEnum state);

    List<Event> findByCategory_Id(Long id);
}
