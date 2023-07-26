package ru.practicum.service;

import ru.practicum.dto.CategoryDto;

import java.util.List;

public interface CategoriesService {
    CategoryDto createCategory(CategoryDto categoryDto);

    CategoryDto patchCategory(CategoryDto categoryDto, Long catId);

    List<CategoryDto> findCategories(Integer from, Integer size);

    CategoryDto findCategoriById(Long catId);
}
