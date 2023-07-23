package ru.practicum.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.model.AppDict;

import java.util.Optional;

@Repository
public interface AppDictStorage extends JpaRepository<AppDict, Long> {
    boolean existsByAppName(String appName);

    AppDict save(AppDict appDict);

    Optional<AppDict> findByAppName(String appName);
}
