package ru.practicum.service;

import ru.practicum.dto.UserDto;

import java.util.ArrayList;
import java.util.List;

public interface UserService {
    List<UserDto> findUsers(Integer from, Integer size, ArrayList<Long> ids);

    UserDto createUser(UserDto userDto);

    void deleteUser(Long userId);
}
