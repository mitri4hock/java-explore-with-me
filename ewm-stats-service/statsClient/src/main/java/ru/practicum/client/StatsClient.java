package ru.practicum.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.practicum.ConstantsUtil;
import ru.practicum.dto.ViewStatsDto;
import ru.practicum.exception.BadParamException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class StatsClient {
    private final RestTemplate restTemplate;
    @Value("${stats-server.url}")
    private String statsServerUrl;
    private static final String API_PREFIX = "/stats";

    public StatsClient() {
        this.restTemplate = new RestTemplate();
    }

    public List<ViewStatsDto> findStatsOfHits(LocalDateTime start, LocalDateTime end, ArrayList<String> uris, Boolean unique) {

        if (start.isAfter(end)) {
            String str = String.format("При запросе статистики были неверно указаны даты интервала." +
                    " Start = %s, End = %s", start, end);
            log.info(str);
            throw new BadParamException(str);
        }
        String url = String.join("", statsServerUrl, API_PREFIX, "?start=",
                String.format(ConstantsUtil.FORMAT_DATE, start), "&end=", String.format(ConstantsUtil.FORMAT_DATE, end),
                "&unique=", unique.toString());
        if (uris != null) {
            for (String x : uris) {
                url = url + "&uris=" + x;
            }
        }
        log.info("Отправляю запрос на statsServer на получение статистики. Параметры запроса: start = {}, end = {}," +
                " uris = {}, unique = {}", start, end, uris, unique);
        return restTemplate.getForObject(
                url,
                List.class);
    }
}
