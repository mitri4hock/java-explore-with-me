package ru.practicum.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.CategoryDto;
import ru.practicum.exception.ErrorDtoUtil;
import ru.practicum.exception.NotFoundException;
import ru.practicum.mapper.CustomMapper;
import ru.practicum.model.Category;
import ru.practicum.storage.CategoriesStorage;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional(readOnly = true)
@AllArgsConstructor
public class CategoriesServiceImpl implements CategoriesService {
    private final CategoriesStorage categoriesStorage;

    @Override
    @Transactional
    public CategoryDto createCategory(CategoryDto categoryDto) {
        Category newCategory = CustomMapper.INSTANCE.toCategory(categoryDto);
        categoriesStorage.save(newCategory);
        log.info("создана новая категория: {}", newCategory.toString());
        return CustomMapper.INSTANCE.toCategoryDto(newCategory);
    }

    @Override
    @Transactional
    public CategoryDto patchCategory(CategoryDto categoryDto, Long catId) {
        var result = categoriesStorage.findById(catId).orElseThrow(() -> {
            log.info("запрошено изменение неуществующего статуса. Id={}", catId);
            throw new NotFoundException(String.join("", "Category with id=", catId.toString(),
                    " was not found"),
                    new ErrorDtoUtil("The required object was not found.", LocalDateTime.now()));
        });
        if (categoriesStorage.findByName(categoryDto.getName()).isPresent()) {
            log.info("попытка присвоения длястатуса уже существующего имени. Name={}", categoryDto.getName());
            throw new NotFoundException(String.join("", "That name=", categoryDto.getName(),
                    " already exists"),
                    new ErrorDtoUtil("The required object already exists.", LocalDateTime.now()));
        }
        result.setName(categoryDto.getName());
        categoriesStorage.save(result);
        return CustomMapper.INSTANCE.toCategoryDto(result);
    }

    @Override
    public List<CategoryDto> findCategories(Integer from, Integer size) {
        Sort sortBy = Sort.by(Sort.Order.desc("id"));
        Pageable page = PageRequest.of(from / size, size, sortBy);
        Page<Category> preRez = categoriesStorage.findAll(page);
        return preRez.getContent().stream()
                .map(CustomMapper.INSTANCE::toCategoryDto)
                .collect(Collectors.toList());
    }

    @Override
    public CategoryDto findCategoriById(Long catId) {
        var rezult = categoriesStorage.findById(catId).orElseThrow(() -> {
            log.info("запрошена несуществующая категория id={}", catId);
            throw new NotFoundException(String.join("", "Category with id=", catId.toString(),
                    " was not found"), new ErrorDtoUtil("The required object was not found.", LocalDateTime.now()));
        });
        return CustomMapper.INSTANCE.toCategoryDto(rezult);
    }
}
