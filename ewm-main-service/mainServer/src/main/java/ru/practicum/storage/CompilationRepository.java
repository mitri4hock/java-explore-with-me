package ru.practicum.storage;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.model.Compilation;

import java.util.List;
import java.util.Optional;

@Repository
public interface CompilationRepository extends JpaRepository<Compilation, Long> {

    @Query("select c from Compilation c where (:pinned is null or c.pinned = :pinned)")
    List<Compilation> findByPinned(@Param("pinned") Boolean pinned, Pageable pageable);
}
