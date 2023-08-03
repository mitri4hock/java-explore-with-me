package ru.practicum.storage;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.model.Compilation;

import java.util.List;
import java.util.Optional;

@Repository
public interface CompilationStorage extends JpaRepository<Compilation, Long> {
    @Override
    Compilation save(Compilation compilation);

    @Override
    Optional<Compilation> findById(Long aLong);

    List<Compilation> findByPinned(Boolean pinned, Pageable pageable);

    @Override
    void deleteById(Long aLong);


}
