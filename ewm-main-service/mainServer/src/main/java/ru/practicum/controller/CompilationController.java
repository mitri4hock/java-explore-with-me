package ru.practicum.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.CompilationDto;
import ru.practicum.dto.NewCompilationDto;
import ru.practicum.dto.UpdateCompilationRequestDto;
import ru.practicum.service.CompilationService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping
@AllArgsConstructor
@Validated
public class CompilationController {

    private final CompilationService compilationService;

    @Operation(summary = "Добавление новой подборки (подборка может не содержать событий)")
    @PostMapping("/admin/compilations")
    @ResponseStatus(HttpStatus.CREATED)
    public CompilationDto createCompilationByAdmin(@RequestBody @Valid NewCompilationDto newCompilationDto) {
        return compilationService.createCompilationByAdmin(newCompilationDto);
    }

    @Operation(summary = "Обновить информацию о подборке")
    @PatchMapping("/admin/compilations/{compId}")
    @ResponseStatus(HttpStatus.OK)
    public CompilationDto patchCompilationByAdmin(@PathVariable @PositiveOrZero Long compId,
                                                  @RequestBody @Valid UpdateCompilationRequestDto updateCompilationRequestDto) {
        return compilationService.patchCompilationByAdmin(compId, updateCompilationRequestDto);
    }

    @Operation(summary = "Удаление подборки")
    @DeleteMapping("/admin/compilations/{compId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCompilation(@PathVariable @PositiveOrZero Long compId) {
        compilationService.deleteCompilation(compId);
    }

    @Operation(summary = "Получение подборки событий по его id В случае, если подборки с заданным id " +
            "не найдено, возвращает статус код 404")
    @GetMapping("/compilations/{compId}")
    @ResponseStatus(HttpStatus.OK)
    public CompilationDto findCompilationById(@PathVariable @PositiveOrZero Long compId) {
        return compilationService.findCompilationById(compId);
    }

    @Operation(summary = "Получение подборок событий. В случае, если по заданным фильтрам не найдено ни одной подборки," +
            " возвращает пустой список")
    @GetMapping("/compilations")
    @ResponseStatus(HttpStatus.OK)
    public List<CompilationDto> findCompilation(@RequestParam(value = "pinned", required = false) Boolean pinned,
                                                @RequestParam(value = "from", defaultValue = "0") @PositiveOrZero Integer from,
                                                @RequestParam(value = "size", defaultValue = "10") @Positive Integer size) {
        return compilationService.findCompilation(pinned, from, size);
    }
}
