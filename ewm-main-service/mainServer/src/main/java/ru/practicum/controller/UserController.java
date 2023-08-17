package ru.practicum.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.UserDto;
import ru.practicum.service.UserService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.ArrayList;
import java.util.List;

@RequestMapping("/admin/users")
@RestController
@AllArgsConstructor
@Validated
public class UserController {

    private final UserService userService;

    @Operation(summary = "Получение информации о пользователях." +
            " Возвращает информацию обо всех пользователях (учитываются параметры ограничения выборки), либо о конкретных" +
            " (учитываются указанные идентификаторы) В случае, если по заданным фильтрам не найдено ни одного" +
            " пользователя, возвращает пустой список")
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<UserDto> findUsers(@RequestParam(value = "from", defaultValue = "0") Integer from,
                                   @RequestParam(value = "size", defaultValue = "10") @Positive Integer size,
                                   @RequestParam(value = "ids", required = false) ArrayList<Long> ids) {
        return userService.findUsers(from, size, ids);
    }

    @Operation(summary = "Добавление нового пользователя")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto createUser(@RequestBody @Valid UserDto userDto) {
        return userService.createUser(userDto);
    }

    @Operation(summary = "Удаление пользователя")
    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable @PositiveOrZero Long userId) {
        userService.deleteUser(userId);
    }
}
