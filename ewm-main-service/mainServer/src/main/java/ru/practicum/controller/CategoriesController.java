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

    @GetMapping("/categories")
    @ResponseStatus(HttpStatus.OK)
    public List<CategoryDto> findCategories(@RequestParam(value = "from", defaultValue = "0") @PositiveOrZero Integer from,
                                            @RequestParam(value = "size", defaultValue = "10") @Positive Integer size) {
        return categoriesService.findCategories(from, size);
    }

    @GetMapping("/categories/{catId}")
    @ResponseStatus(HttpStatus.OK)
    public CategoryDto findCategoriById(@PathVariable @PositiveOrZero Long catId) {
        return categoriesService.findCategoriById(catId);
    }

    @PostMapping("/admin/categories")
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryDto createCategory(@RequestBody @Valid CategoryDto categoryDto) {
        return categoriesService.createCategory(categoryDto);
    }

    @PatchMapping("/admin/categories/{catId}")
    @ResponseStatus(HttpStatus.OK)
    public CategoryDto patchCategory(@RequestBody @Valid CategoryDto categoryDto,
                                     @PathVariable @PositiveOrZero Long catId) {
        return categoriesService.patchCategory(categoryDto, catId);
    }

    @DeleteMapping("/admin/categories/{catId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCategory(@PathVariable @PositiveOrZero Long catId) {
        categoriesService.deleteCategory(catId);
    }
}
