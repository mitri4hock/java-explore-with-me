package ru.practicum.controller;

import lombok.AllArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ConstantsUtil;
import ru.practicum.client.StatsClient;
import ru.practicum.dto.ViewStatsDto;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/stats")
@AllArgsConstructor
@Validated
public class StatsController {
    private final StatsClient statsClient;

    /**
     * Получение статистики по посещениям
     */
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ViewStatsDto> findStatsOfHits(
            @RequestParam(value = "start") @NotNull @DateTimeFormat(pattern = ConstantsUtil.formatDate) LocalDateTime start,
            @RequestParam(value = "end") @NotNull @DateTimeFormat(pattern = ConstantsUtil.formatDate) LocalDateTime end,
            @RequestParam(value = "uris", required = false) ArrayList<String> uris,
            @RequestParam(value = "unique", defaultValue = "false") Boolean unique) {
        return statsClient.findStatsOfHits(start, end, uris, unique);
    }
}
