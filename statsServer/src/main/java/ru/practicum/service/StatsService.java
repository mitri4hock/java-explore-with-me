package ru.practicum.service;

import ru.practicum.dto.ViewStatsDto;

import java.util.ArrayList;
import java.util.List;

public interface StatsService {
    List<ViewStatsDto> findStatsOfHits(String start, String end, ArrayList<String> uris, Boolean unique);
}
