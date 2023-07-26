package ru.practicum.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.model.Event;

@Repository
public interface EventStorage extends JpaRepository<Event, Long> {

    @Override
    Event save(Event event);
}
