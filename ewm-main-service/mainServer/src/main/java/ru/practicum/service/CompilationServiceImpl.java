package ru.practicum.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.client.StatisticModuleClient;
import ru.practicum.dto.CompilationDto;
import ru.practicum.dto.EventShortDto;
import ru.practicum.dto.NewCompilationDto;
import ru.practicum.dto.UpdateCompilationRequestDto;
import ru.practicum.enums.EventRequestStatusEnum;
import ru.practicum.exception.ErrorDtoUtil;
import ru.practicum.exception.NotFoundException;
import ru.practicum.mapper.CustomMapper;
import ru.practicum.mapper.UtilitMapper;
import ru.practicum.model.Compilation;
import ru.practicum.model.CompilationsEvents;
import ru.practicum.model.Event;
import ru.practicum.storage.CompilationStorage;
import ru.practicum.storage.CompilationsEventsStorage;
import ru.practicum.storage.EventRequestStorage;
import ru.practicum.storage.EventStorage;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@Transactional(readOnly = true)
@AllArgsConstructor
public class CompilationServiceImpl implements CompilationService {

    private final CompilationStorage compilationStorage;
    private final EventStorage eventStorage;
    private final EventRequestStorage eventRequestStorage;
    private final StatisticModuleClient statisticModuleClient;
    private final CompilationsEventsStorage compilationsEventsStorage;


    @Override
    @Transactional
    public CompilationDto createCompilationByAdmin(NewCompilationDto newCompilationDto) {
        if (newCompilationDto.getEvents().isEmpty()) {
            return new CompilationDto();
        }

        Compilation compilationRezult = compilationStorage.save(CustomMapper.INSTANCE.toCompilation(newCompilationDto));

        List<EventShortDto> listEventShortDto = new ArrayList<>();
        for (Long eventId : newCompilationDto.getEvents()) {
            Event event = eventStorage.findById(eventId).orElseThrow(() -> {
                throw new NotFoundException(String.join("", "Event with id=", eventId.toString(),
                        " was not found"), new ErrorDtoUtil("The required object was not found.",
                        LocalDateTime.now()));
            });

            CompilationsEvents compilationsEvents = new CompilationsEvents();
            compilationsEvents.setCompilation(compilationRezult);
            compilationsEvents.setEvent(event);
            compilationsEventsStorage.save(compilationsEvents);

            EventShortDto eventShortDto = UtilitMapper.toEventShortDto(event,
                    eventRequestStorage.countByStatusAndEvent_Id(EventRequestStatusEnum.CONFIRMED,
                            event.getId()),
                    statisticModuleClient.getCountViewsOfHit(String.join("", "/events/",
                            event.getId().toString())));
            listEventShortDto.add(eventShortDto);
        }
        return UtilitMapper.toCompilationDto(compilationRezult, listEventShortDto);
    }

    @Override
    @Transactional
    public void deleteCompilation(Long compId) {
        if (compilationStorage.findById(compId).isEmpty()) {
            throw new NotFoundException(String.join("", "Compilation with id=", compId.toString(),
                    " was not found"), new ErrorDtoUtil("The required object was not found.",
                    LocalDateTime.now()));
        }
        var delList = compilationsEventsStorage.findByCompilation_Id(compId);
        for (Long i : delList) {
            compilationsEventsStorage.deleteById(i);
        }
        compilationStorage.deleteById(compId);
    }

    @Override
    @Transactional
    public CompilationDto patchCompilationByAdmin(Long compId, UpdateCompilationRequestDto updateCompilationRequestDto) {
        var compilation = compilationStorage.findById(compId).orElseThrow(() -> {
            throw new NotFoundException(String.join("", "Compilation with id=", compId.toString(),
                    " was not found"), new ErrorDtoUtil("The required object was not found.",
                    LocalDateTime.now()));
        });
        if (updateCompilationRequestDto.getPinned() != null) {
            compilation.setPinned(updateCompilationRequestDto.getPinned());
        }
        if (updateCompilationRequestDto.getTitle() != null) {
            compilation.setTitle(updateCompilationRequestDto.getTitle());
        }
        compilationStorage.save(compilation);

        var delList = compilationsEventsStorage.findByCompilation_Id(compId);
        for (Long i : delList) {
            compilationsEventsStorage.deleteById(i);
        }
        compilationStorage.deleteById(compId);

        List<EventShortDto> listEventShortDto = new ArrayList<>();
        for (Long eventId : updateCompilationRequestDto.getEvents()) {
            Event event = eventStorage.findById(eventId).orElseThrow(() -> {
                throw new NotFoundException(String.join("", "Event with id=", eventId.toString(),
                        " was not found"), new ErrorDtoUtil("The required object was not found.",
                        LocalDateTime.now()));
            });

            CompilationsEvents compilationsEvents = new CompilationsEvents();
            compilationsEvents.setCompilation(compilation);
            compilationsEvents.setEvent(event);
            compilationsEventsStorage.save(compilationsEvents);

            EventShortDto eventShortDto = UtilitMapper.toEventShortDto(event,
                    eventRequestStorage.countByStatusAndEvent_Id(EventRequestStatusEnum.CONFIRMED,
                            event.getId()),
                    statisticModuleClient.getCountViewsOfHit(String.join("", "/events/",
                            event.getId().toString())));
            listEventShortDto.add(eventShortDto);
        }
        return UtilitMapper.toCompilationDto(compilation, listEventShortDto);
    }

    @Override
    public CompilationDto findCompilationById(Long compId) {
        var compilation = compilationStorage.findById(compId).orElseThrow(() -> {
            throw new NotFoundException(String.join("", "Compilation with id=", compId.toString(),
                    " was not found"), new ErrorDtoUtil("The required object was not found.",
                    LocalDateTime.now()));
        });

        var findList = compilationsEventsStorage.findEventIdByCompilation_Id(compId);
        List<EventShortDto> listEventShortDto = new ArrayList<>();
        for (Long eventId : findList) {
            Event event = eventStorage.findById(eventId).orElseThrow(() -> {
                throw new NotFoundException(String.join("", "Event with id=", eventId.toString(),
                        " was not found"), new ErrorDtoUtil("The required object was not found.",
                        LocalDateTime.now()));
            });

            EventShortDto eventShortDto = UtilitMapper.toEventShortDto(event,
                    eventRequestStorage.countByStatusAndEvent_Id(EventRequestStatusEnum.CONFIRMED,
                            event.getId()),
                    statisticModuleClient.getCountViewsOfHit(String.join("", "/events/",
                            event.getId().toString())));
            listEventShortDto.add(eventShortDto);
        }
        return UtilitMapper.toCompilationDto(compilation, listEventShortDto);
    }

    @Override
    public List<CompilationDto> findCompilation(Boolean pinned, Integer from, Integer size) {

        Sort sortBy = Sort.by(Sort.Order.asc("id"));
        Pageable page = PageRequest.of(from / size, size, sortBy);

        var comList = compilationStorage.findByPinned(pinned, page);
        if (comList.isEmpty()) {
            return new ArrayList<>();
        }

        List<CompilationDto> rez = new ArrayList<>();
        for (Compilation compilation : comList) {
            var findList = compilationsEventsStorage.findEventIdByCompilation_Id(compilation.getId());
            List<EventShortDto> listEventShortDto = new ArrayList<>();
            for (Long eventId : findList) {
                Event event = eventStorage.findById(eventId).orElseThrow(() -> {
                    throw new NotFoundException(String.join("", "Event with id=", eventId.toString(),
                            " was not found"), new ErrorDtoUtil("The required object was not found.",
                            LocalDateTime.now()));
                });

                EventShortDto eventShortDto = UtilitMapper.toEventShortDto(event,
                        eventRequestStorage.countByStatusAndEvent_Id(EventRequestStatusEnum.CONFIRMED,
                                event.getId()),
                        statisticModuleClient.getCountViewsOfHit(String.join("", "/events/",
                                event.getId().toString())));
                listEventShortDto.add(eventShortDto);
            }
            rez.add(UtilitMapper.toCompilationDto(compilation, listEventShortDto));
        }
        return rez;
    }
}
