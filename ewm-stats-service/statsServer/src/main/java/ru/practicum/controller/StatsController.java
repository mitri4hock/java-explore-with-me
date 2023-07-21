package ru.practicum.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.ViewStatsDto;
import ru.practicum.service.StatsService;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/stats")
@AllArgsConstructor
public class StatsController {
    private final StatsService statsService;

    /**
     * Получение статистики по посещениям
     */
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ViewStatsDto> findStatsOfHits(
            @RequestParam(value = "start") String start,
            @RequestParam(value = "end") String end,
            @RequestParam(value = "uris", required = false) ArrayList<String> uris,
            @RequestParam(value = "unique", defaultValue = "false") Boolean unique) {
        return statsService.findStatsOfHits(start, end, uris, unique);
    }
}