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
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.ErrorDtoUtil;
import ru.practicum.exception.NotFoundException;
import ru.practicum.mapper.CustomMapper;
import ru.practicum.model.Category;
import ru.practicum.storage.CategoriesRepository;
import ru.practicum.storage.EventRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional(readOnly = true)
@AllArgsConstructor
public class CategoriesServiceImpl implements CategoriesService {
    private final CategoriesRepository categoriesRepository;
    private final EventRepository eventRepository;

    @Override
    @Transactional
    public CategoryDto createCategory(CategoryDto categoryDto) {
        if (categoriesRepository.findByName(categoryDto.getName()).isPresent()) {
            String msg = String.join("",
                    "Запрошено создание новой категории с уже сузествующим именем: ", categoryDto.getName());
            log.info(msg);
            throw new ConflictException(msg, new ErrorDtoUtil("Name already exist",
                    LocalDateTime.now()));
        }
        Category newCategory = CustomMapper.INSTANCE.toCategory(categoryDto);
        categoriesRepository.save(newCategory);
        log.info("создана новая категория: {}", newCategory.toString());
        return CustomMapper.INSTANCE.toCategoryDto(newCategory);
    }

    @Override
    @Transactional
    public CategoryDto patchCategory(CategoryDto categoryDto, Long catId) {
        var result = categoriesRepository.findById(catId).orElseThrow(() -> {
            log.info("запрошено изменение неуществующего статуса. Id={}", catId);
            throw new NotFoundException(String.join("", "Category with id=", catId.toString(),
                    " was not found"),
                    new ErrorDtoUtil("The required object was not found.", LocalDateTime.now()));
        });
        var oldCategory = categoriesRepository.findByName(categoryDto.getName());
        if (oldCategory.isPresent() && !oldCategory.get().getId().equals(catId)) {
            log.info("попытка присвоения для статуса уже существующего имени. Name={}", categoryDto.getName());
            throw new ConflictException(String.join("", "That name=", categoryDto.getName(),
                    " already exists"),
                    new ErrorDtoUtil("The required object already exists.", LocalDateTime.now()));
        }
        result.setName(categoryDto.getName());
        return CustomMapper.INSTANCE.toCategoryDto(result);
    }

    @Override
    public List<CategoryDto> findCategories(Integer from, Integer size) {
        Sort sortBy = Sort.by(Sort.Order.desc("id"));
        Pageable page = PageRequest.of(from / size, size, sortBy);
        Page<Category> preRez = categoriesRepository.findAll(page);
        return preRez.getContent().stream()
                .map(CustomMapper.INSTANCE::toCategoryDto)
                .collect(Collectors.toList());
    }

    @Override
    public CategoryDto findCategoriById(Long catId) {
        var rezult = categoriesRepository.findById(catId).orElseThrow(() -> {
            log.info("запрошена несуществующая категория id={}", catId);
            throw new NotFoundException(String.join("", "Category with id=", catId.toString(),
                    " was not found"), new ErrorDtoUtil("The required object was not found.",
                    LocalDateTime.now()));
        });
        return CustomMapper.INSTANCE.toCategoryDto(rezult);
    }

    @Override
    @Transactional
    public void deleteCategory(Long catId) {
        if (categoriesRepository.findById(catId).isEmpty()) {
            log.info("Запрошено удаление несуществующей категории. id={}", catId);
            throw new NotFoundException(String.join("", "Category with id=", catId.toString(),
                    " was not found"), new ErrorDtoUtil("The required object was not found.",
                    LocalDateTime.now()));
        }
        if (!eventRepository.findByCategoryId(catId).isEmpty()) {
            log.info("Запрошено удаление категории, со связанными событиями. Id={}", catId);
            throw new ConflictException("The category is not empty",
                    new ErrorDtoUtil("For the requested operation the conditions are not met.",
                            LocalDateTime.now()));
        }
        var delEntity = categoriesRepository.removeById(catId);
        log.info("категория удалена: {}", delEntity);
    }
}
