package ru.practicum.service;

import ru.practicum.dto.CompilationDto;
import ru.practicum.dto.NewCompilationDto;
import ru.practicum.dto.UpdateCompilationRequestDto;

import java.util.List;

public interface CompilationService {
    CompilationDto createCompilationByAdmin(NewCompilationDto newCompilationDto);

    void deleteCompilation(Long compId);

    CompilationDto patchCompilationByAdmin(Long compId, UpdateCompilationRequestDto updateCompilationRequestDto);

    CompilationDto findCompilationById(Long compId);

    List<CompilationDto> findCompilation(Boolean pinned, Integer from, Integer size);
}
