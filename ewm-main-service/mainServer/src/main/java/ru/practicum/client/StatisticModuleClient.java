package ru.practicum.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.practicum.dto.HitsDto;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
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
                "?start=0001-01-01 01:01:01&end=9999-12-31 23:59:59&unique=true&uris=",
                hitName);
        log.info("Отправляю запрос на statsServer на получение статистики. Параметры запроса: {}", url);
        List<LinkedHashMap> rezTemplate = null;
        rezTemplate = restTemplate.getForObject(url, List.class);
        var rez = (rezTemplate == null) || (rezTemplate.size() == 0) ? 0 :  rezTemplate.get(0).get("hits");
        return Long.valueOf(rez.toString());
    }

    public HitsDto postRequestToHit(HttpServletRequest request) {
        String url = String.join("", statsServerUrl, "/hit");
        HitsDto postBody = new HitsDto("ewm-main-service", request.getRequestURI(), request.getRemoteAddr(),
                LocalDateTime.now());
        log.info("Отправляю запрос на statsServer на запись статистики. Параметры запроса: {}", postBody);
        return restTemplate.postForObject(url, postBody, HitsDto.class);
    }
}
