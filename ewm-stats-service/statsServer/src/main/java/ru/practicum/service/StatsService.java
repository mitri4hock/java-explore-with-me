package ru.practicum.service;

import ru.practicum.dto.ViewStatsDto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public interface StatsService {
    List<ViewStatsDto> findStatsOfHits(LocalDateTime start, LocalDateTime end, ArrayList<String> uris, Boolean unique);
}
