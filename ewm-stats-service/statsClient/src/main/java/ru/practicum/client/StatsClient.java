package ru.practicum.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.practicum.dto.ViewStatsDto;
import ru.practicum.exception.BadParamException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class StatsClient {
    private final RestTemplate restTemplate;
    @Value("${stats-server.url}")
    private String statsServerUrl;
    private static final String API_PREFIX = "/stats";
    private DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public StatsClient() {
        this.restTemplate = new RestTemplate();
    }

    public List<ViewStatsDto> findStatsOfHits(String start, String end, ArrayList<String> uris, Boolean unique) {

        if (LocalDateTime.parse(start, dateTimeFormatter).isAfter(LocalDateTime.parse(end, dateTimeFormatter))) {
            String str = String.format("При запросе статистики были неверно указаны даты интервала." +
                    " Start = %s, End = %s", start, end);
            log.info(str);
            throw new BadParamException(str);
        }
        String url = String.join("", statsServerUrl, API_PREFIX, "?start=", start, "&end=",
                end, "&unique=", unique.toString());
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
