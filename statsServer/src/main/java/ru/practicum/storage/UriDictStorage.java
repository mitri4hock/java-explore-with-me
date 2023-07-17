package ru.practicum.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.model.UriDict;

import java.util.Optional;

@Repository
public interface UriDictStorage extends JpaRepository<UriDict, Long> {
    boolean existsByUri_name(String uriName);

    UriDict save(UriDict uriDict);

    Optional<UriDict> findByUri_name(String uriName);


}
