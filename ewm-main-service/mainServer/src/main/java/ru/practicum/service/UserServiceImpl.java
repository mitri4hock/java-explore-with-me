package ru.practicum.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.UserDto;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.ErrorDtoUtil;
import ru.practicum.exception.NotFoundException;
import ru.practicum.mapper.CustomMapper;
import ru.practicum.model.User;
import ru.practicum.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional(readOnly = true)
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public List<UserDto> findUsers(Integer from, Integer size, ArrayList<Long> ids) {
        Sort sortBy = Sort.by(Sort.Order.asc("id"));
        Pageable page = PageRequest.of(from / size, size, sortBy);
        Page<User> result;
        if (ids == null) {
            result = userRepository.findAll(page);
        } else {
            result = userRepository.findByIdIn(ids, page);
        }
        log.info("запрошен список юзеров. Параметры запроса: from={}, size={}, ids={}", from, size, ids);
        return result.getContent().stream()
                .map(CustomMapper.INSTANCE::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public UserDto createUser(UserDto userDto) {
        if (userRepository.findByName(userDto.getName()).isPresent()) {
            String msg = String.join("",
                    "Запрошено создание нового Пользователя с уже сузествующим именем: ", userDto.getName());
            log.info(msg);
            throw new ConflictException(msg, new ErrorDtoUtil("Name already exist",
                    LocalDateTime.now()));
        }
        User newUser = CustomMapper.INSTANCE.toUser(userDto);
        userRepository.save(newUser);
        log.info("создан новый пользователь: {}", newUser.toString());
        return CustomMapper.INSTANCE.toUserDto(newUser);
    }

    @Override
    @Transactional
    public void deleteUser(Long userId) {
        if (userRepository.findById(userId).isEmpty()) {
            log.info("запрошено удаление несуществующего пользователя с id={}", userId);
            throw new NotFoundException(String.join("", "User with id=", userId.toString(),
                    " was not found"), new ErrorDtoUtil("The required object was not found.",
                    LocalDateTime.now()));
        }
        userRepository.deleteById(userId);
    }
}
