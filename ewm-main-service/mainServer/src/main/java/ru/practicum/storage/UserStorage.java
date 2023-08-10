package ru.practicum.storage;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.model.User;

import java.util.Collection;
import java.util.Optional;

@Repository
public interface UserStorage extends JpaRepository<User, Long> {

    @Override
    Page<User> findAll(Pageable page);

    Page<User> findByIdIn(Collection<Long> ids, Pageable pageable);

    @Override
    User save(User user);

    @Override
    Optional<User> findById(Long aLong);

    Optional<User> findByName(String name);


    @Override
    void deleteById(Long aLong);
}
