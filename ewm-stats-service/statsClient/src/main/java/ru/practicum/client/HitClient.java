package ru.practicum.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.practicum.dto.HitsDto;

@Service
@Slf4j
public class HitClient {
    private final RestTemplate restTemplate;
    @Value("${stats-server.url}")
    private String statsServerUrl;
    private static final String API_PREFIX = "/hit";

    public HitClient() {
        this.restTemplate = new RestTemplate();
    }

    public HitsDto createHit(String url, HitsDto hitsDto) {
        HttpEntity<HitsDto> request = new HttpEntity<>(hitsDto);
        log.info("Отправляю запрос на statsServer на создание hit: {}", hitsDto);
        return restTemplate.postForObject(
                statsServerUrl + API_PREFIX + url,
                request,
                HitsDto.class);
    }
}
