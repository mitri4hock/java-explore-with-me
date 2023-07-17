package ru.practicum.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.HitsDto;
import ru.practicum.service.HitsService;

@RestController
@RequestMapping("/hit")
@AllArgsConstructor
public class HitsController {
    private final HitsService hitsService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public HitsDto createHit(@RequestBody HitsDto hitsDto) {
        return hitsService.createHit(hitsDto);
    }
}
