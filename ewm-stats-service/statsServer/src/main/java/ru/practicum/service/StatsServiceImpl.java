package ru.practicum.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.ViewStatsDto;
import ru.practicum.storage.HitsStorage;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@Transactional(readOnly = true)
@AllArgsConstructor
public class StatsServiceImpl implements StatsService {

    private final HitsStorage hitsStorage;

    @Override
    public List<ViewStatsDto> findStatsOfHits(LocalDateTime start, LocalDateTime end, ArrayList<String> uris, Boolean unique) {

        log.info("Запрошена статистика с параметрами: start - {}, end - {}, uris - {}, unique - {}, ",
                start, end, uris, unique);

        if (unique == true && uris == null) {
            return hitsStorage.getStatsIpIsDistinctAndUriIsNot(start, end);
        } else if (unique == false && uris == null) {
            return hitsStorage.getStatsIpIsAnDistinctAndUriIsNot(start, end);
        } else if (unique == true && uris != null) {
            return hitsStorage.getStatsIpIsDistinctAndUriIsBe(start, end, uris);
        } else if (unique == false && uris != null) {
            return hitsStorage.getStatsIpIsAnDistinctAndUriIsBe(start, end, uris);
        }
        return null;
    }
}
