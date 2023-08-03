package ru.practicum.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.model.CompilationsEvents;

import java.util.List;

@Repository
public interface CompilationsEventsStorage extends JpaRepository<CompilationsEvents, Long> {

    CompilationsEvents save(CompilationsEvents compilationsEvents);

    @Query("select c.id from CompilationsEvents c where c.compilation.id = ?1")
    List<Long> findByCompilation_Id(Long comId);

    @Query("select c.event.id from CompilationsEvents c where c.compilation.id = ?1")
    List<Long> findEventIdByCompilation_Id(Long comId);

    @Override
    void deleteById(Long aLong);
}
