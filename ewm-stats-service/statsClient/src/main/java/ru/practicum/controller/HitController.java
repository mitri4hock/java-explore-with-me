package ru.practicum.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.client.HitClient;
import ru.practicum.dto.HitsDto;

import javax.validation.Valid;

@RestController
@RequestMapping("/hit")
@AllArgsConstructor
@Validated
public class HitController {

    private final HitClient hitClient;

    /**
     * Сохранение информации о том, что на uri конкретного сервиса был отправлен запрос пользователем. Название сервиса,
     * uri и ip пользователя указаны в теле запроса.
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public HitsDto createHit(@RequestBody @Valid HitsDto hitsDto) {
        return hitClient.createHit("", hitsDto);
    }
}
