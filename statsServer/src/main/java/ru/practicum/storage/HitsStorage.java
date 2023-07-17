package ru.practicum.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.model.Hits;

@Repository
public interface HitsStorage extends JpaRepository<Hits, Long> {
    Hits save(Hits hits);

}
