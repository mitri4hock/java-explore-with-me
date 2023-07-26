package ru.practicum.storage;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.model.Category;

import java.util.Optional;

@Repository
public interface CategoriesStorage extends JpaRepository<Category, Long> {
    @Override
    Category save(Category category);

    @Override
    Optional<Category> findById(Long aLong);

    Optional<Category> findByName(String name);

    @Override
    Page<Category> findAll(Pageable pageable);

}
