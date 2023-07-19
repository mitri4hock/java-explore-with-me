package ru.practicum.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.HitsDto;
import ru.practicum.service.HitService;

@RestController
@RequestMapping("/hit")
@AllArgsConstructor
public class HitController {

    private final HitService hitService;

    /**
     * Сохранение информации о том, что на uri конкретного сервиса был отправлен запрос пользователем. Название сервиса,
     * uri и ip пользователя указаны в теле запроса.
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public HitsDto createHit(@RequestBody HitsDto hitsDto) {
        return hitService.createHit(hitsDto);
    }
}
