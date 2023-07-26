package ru.practicum.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.EventFullDto;
import ru.practicum.dto.NewEventDto;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.ErrorDtoUtil;
import ru.practicum.exception.NotFoundException;
import ru.practicum.mapper.UtilitMapper;
import ru.practicum.model.Category;
import ru.practicum.model.Event;
import ru.practicum.model.User;
import ru.practicum.storage.CategoriesStorage;
import ru.practicum.storage.EventStorage;
import ru.practicum.storage.UserStorage;
import ru.practicum.util.UtilClass;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@Slf4j
@Transactional(readOnly = true)
@AllArgsConstructor
public class EventServiceImpl implements EventService {

    private final EventStorage eventStorage;
    private final UserStorage userStorage;
    private final CategoriesStorage categoriesStorage;
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(UtilClass.FORMAT_DATE);

    @Override
    @Transactional
    public EventFullDto createEvent(Long userId, NewEventDto newEventDto) {
        User user = userStorage.findById(userId).orElseThrow(() -> {
            log.info("запрошено добавление события несуществующим пользователем с id={}", userId);
            throw new NotFoundException(String.join("", "User with id=", userId.toString(),
                    " was not found"), new ErrorDtoUtil("The required object was not found.",
                    LocalDateTime.now()));
        });
        Category category = categoriesStorage.findById(newEventDto.getCategory()).orElseThrow(() -> {
            log.info("запрошено добавление события с несуществующей категорией с id={}", newEventDto.getCategory());
            throw new NotFoundException(String.join("", "Category with id=",
                    newEventDto.getCategory().toString(), " was not found"),
                    new ErrorDtoUtil("The required object was not found.", LocalDateTime.now()));
        });
        if (LocalDateTime.parse(newEventDto.getEventDate(), dateTimeFormatter)
                .isBefore(LocalDateTime.now().plusHours(2))) {
            log.info("Попытка создать событие со временем старта неудовлетворяющем логике прилоения (сейчас + 2 часа)." +
                    " EventDate={}", newEventDto.getEventDate());
            throw new ConflictException(String.join("", "Field: eventDate. Error: должно содержать" +
                    " дату, которая еще не наступила. Value: ", newEventDto.getEventDate()),
                    new ErrorDtoUtil("For the requested operation the conditions are not met.",
                            LocalDateTime.now()));
        }
        Event event = UtilitMapper.toEvent(newEventDto, category, user);
        eventStorage.save(event);
        log.info("создано новое событие: {}", event.toString());
        return UtilitMapper.toEventFullDto(event, 0, 0);
    }
}
