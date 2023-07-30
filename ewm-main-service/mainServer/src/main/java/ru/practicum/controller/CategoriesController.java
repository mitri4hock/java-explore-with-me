package ru.practicum.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.CategoryDto;
import ru.practicum.service.CategoriesService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RequestMapping("")
@RestController
@AllArgsConstructor
@Validated
public class CategoriesController {

    private final CategoriesService categoriesService;

    /**
     * Получение категорий
     * В случае, если по заданным фильтрам не найдено ни одной категории, возвращает пустой список
     */
    @GetMapping("/categories")
    @ResponseStatus(HttpStatus.OK)
    public List<CategoryDto> findCategories(@RequestParam(value = "from", defaultValue = "0") @PositiveOrZero Integer from,
                                            @RequestParam(value = "size", defaultValue = "10") @Positive Integer size) {
        return categoriesService.findCategories(from, size);
    }

    /**
     * Получение информации о категории по её идентификатору
     * В случае, если категории с заданным id не найдено, возвращает статус код 404
     */
    @GetMapping("/categories/{catId}")
    @ResponseStatus(HttpStatus.OK)
    public CategoryDto findCategoriById(@PathVariable @PositiveOrZero Long catId) {
        return categoriesService.findCategoriById(catId);
    }

    /**
     * Добавление новой категории
     * Обратите внимание: имя категории должно быть уникальным
     */
    @PostMapping("/admin/categories")
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryDto createCategory(@RequestBody @Valid CategoryDto categoryDto) {
        return categoriesService.createCategory(categoryDto);
    }

    /**
     * Изменение категории
     * Обратите внимание: имя категории должно быть уникальным
     */
    @PatchMapping("/admin/categories/{catId}")
    @ResponseStatus(HttpStatus.OK)
    public CategoryDto patchCategory(@RequestBody @Valid CategoryDto categoryDto,
                                     @PathVariable @PositiveOrZero Long catId) {
        return categoriesService.patchCategory(categoryDto, catId);
    }

}
