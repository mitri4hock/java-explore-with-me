package ru.practicum.controller;

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

    @PostMapping("/admin/compilations")
    @ResponseStatus(HttpStatus.CREATED)
    public CompilationDto createCompilationByAdmin(@RequestBody @Valid NewCompilationDto newCompilationDto) {
        return compilationService.createCompilationByAdmin(newCompilationDto);
    }

    @PatchMapping("/admin/compilations/{compId}")
    @ResponseStatus(HttpStatus.OK)
    public CompilationDto patchCompilationByAdmin(@PathVariable @PositiveOrZero Long compId,
                                                  @RequestBody @Valid UpdateCompilationRequestDto updateCompilationRequestDto) {
        return compilationService.patchCompilationByAdmin(compId, updateCompilationRequestDto);
    }

    @DeleteMapping("/admin/compilations/{compId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCompilation(@PathVariable @PositiveOrZero Long compId) {
        compilationService.deleteCompilation(compId);
    }

    @GetMapping("/compilations/{compId}")
    @ResponseStatus(HttpStatus.OK)
    public CompilationDto findCompilationById(@PathVariable @PositiveOrZero Long compId) {
        return compilationService.findCompilationById(compId);
    }

    @GetMapping("/compilations")
    @ResponseStatus(HttpStatus.OK)
    public List<CompilationDto> findCompilation(@RequestParam(value = "pinned", required = false) Boolean pinned,
                                                @RequestParam(value = "from", defaultValue = "0") @PositiveOrZero Integer from,
                                                @RequestParam(value = "size", defaultValue = "10") @Positive Integer size) {
        return compilationService.findCompilation(pinned, from, size);
    }
}
