package ru.practicum.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ConstantsUtil;
import ru.practicum.dto.ViewStatsDto;
import ru.practicum.storage.HitsStorage;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@Transactional(readOnly = true)
@AllArgsConstructor
public class StatsServiceImpl implements StatsService {

    private HitsStorage hitsStorage;

    @Override
    public List<ViewStatsDto> findStatsOfHits(String start, String end, ArrayList<String> uris, Boolean unique) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(ConstantsUtil.formatDate);
        LocalDateTime startLDT = LocalDateTime.parse(start, dateTimeFormatter);
        LocalDateTime endLDT = LocalDateTime.parse(end, dateTimeFormatter);

        log.info("Запрошена статистика с параметрами: start - {}, end - {}, uris - {}, unique - {}, ",
                start, end, uris, unique);

        if (unique == true && uris == null) {
            return hitsStorage.getStatsIpIsDistinctAndUriIsNot(startLDT, endLDT);
        } else if (unique == false && uris == null) {
            return hitsStorage.getStatsIpIsAnDistinctAndUriIsNot(startLDT, endLDT);
        } else if (unique == true && uris != null) {
            return hitsStorage.getStatsIpIsDistinctAndUriIsBe(startLDT, endLDT, uris);
        } else if (unique == false && uris != null) {
            return hitsStorage.getStatsIpIsAnDistinctAndUriIsBe(startLDT, endLDT, uris);
        }
        return null;
    }
}
