package ru.practicum.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.practicum.dto.ViewStatsDto;

import java.util.List;

@Service
@Slf4j
public class StatisticModuleClient {

    private final RestTemplate restTemplate;
    @Value("${stats-server.url}")
    private String statsServerUrl;

    public StatisticModuleClient() {
        this.restTemplate = new RestTemplate();
    }

    public Long getCountViewsOfHit(String hitName) {
        String url = String.join("", statsServerUrl, "/stats",
                "?start=0001-01-01 01:01:01&end=9999-12-31 23:59:59&unique=false&uris=",
                hitName);
        log.info("Отправляю запрос на statsServer на получение статистики. Параметры запроса: {}", url);
        List<ViewStatsDto> rezTemplate = restTemplate.getForObject(url, List.class);
        return rezTemplate == null ? 0 : rezTemplate.get(0).getHits();
    }
}
