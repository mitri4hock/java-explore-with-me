package ru.practicum.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.model.CompilationsEvents;

import java.util.List;

@Repository
public interface CompilationsEventsRepository extends JpaRepository<CompilationsEvents, Long> {

    CompilationsEvents save(CompilationsEvents compilationsEvents);

    @Query("select c.id from CompilationsEvents c where c.compilation.id = :comId")
    List<Long> findByCompilationId(@Param("comId") Long comId);

    @Query("select c.event.id from CompilationsEvents c where c.compilation.id = :comId")
    List<Long> findEventIdByCompilationId(@Param("comId")Long comId);
}
