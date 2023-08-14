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
import ru.practicum.model.Compilation;
import ru.practicum.model.CompilationsEvents;
import ru.practicum.model.Event;
import ru.practicum.storage.CompilationRepository;
import ru.practicum.storage.CompilationsEventsRepository;
import ru.practicum.storage.EventRequestRepository;
import ru.practicum.storage.EventRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@Transactional(readOnly = true)
@AllArgsConstructor
public class CompilationServiceImpl implements CompilationService {

    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;
    private final EventRequestRepository eventRequestRepository;
    private final StatisticModuleClient statisticModuleClient;
    private final CompilationsEventsRepository compilationsEventsRepository;


    @Override
    @Transactional
    public CompilationDto createCompilationByAdmin(NewCompilationDto newCompilationDto) {
        Compilation compilationRezult = compilationRepository.save(CustomMapper.INSTANCE.toCompilation(newCompilationDto));

        List<EventShortDto> listEventShortDto = new ArrayList<>();
        for (Long eventId : newCompilationDto.getEvents()) {
            Event event = eventRepository.findById(eventId).orElseThrow(() -> {
                throw new NotFoundException(String.join("", "Event with id=", eventId.toString(),
                        " was not found"), new ErrorDtoUtil("The required object was not found.",
                        LocalDateTime.now()));
            });

            CompilationsEvents compilationsEvents = new CompilationsEvents();
            compilationsEvents.setCompilation(compilationRezult);
            compilationsEvents.setEvent(event);
            compilationsEventsRepository.save(compilationsEvents);

            EventShortDto eventShortDto = CustomMapper.INSTANCE.toEventShortDto(event,
                    eventRequestRepository.countByStatusAndEvent_Id(EventRequestStatusEnum.CONFIRMED,
                            event.getId()),
                    statisticModuleClient.getCountViewsOfHit(String.join("", "/events/",
                            event.getId().toString())));
            listEventShortDto.add(eventShortDto);
        }
        return CustomMapper.INSTANCE.toCompilationDto(compilationRezult, listEventShortDto);
    }

    @Override
    @Transactional
    public void deleteCompilation(Long compId) {
        if (compilationRepository.findById(compId).isEmpty()) {
            throw new NotFoundException(String.join("", "Compilation with id=", compId.toString(),
                    " was not found"), new ErrorDtoUtil("The required object was not found.",
                    LocalDateTime.now()));
        }
        var delList = compilationsEventsRepository.findByCompilation_Id(compId);
        for (Long i : delList) {
            compilationsEventsRepository.deleteById(i);
        }
        compilationRepository.deleteById(compId);
    }

    @Override
    @Transactional
    public CompilationDto patchCompilationByAdmin(Long compId, UpdateCompilationRequestDto updateCompilationRequestDto) {
        var compilation = compilationRepository.findById(compId).orElseThrow(() -> {
            throw new NotFoundException(String.join("", "Compilation with id=", compId.toString(),
                    " was not found"), new ErrorDtoUtil("The required object was not found.",
                    LocalDateTime.now()));
        });
        if (updateCompilationRequestDto.getPinned() != null) {
            compilation.setPinned(updateCompilationRequestDto.getPinned());
        }
        if (updateCompilationRequestDto.getTitle() != null && !updateCompilationRequestDto.getTitle().isBlank()) {
            compilation.setTitle(updateCompilationRequestDto.getTitle());
        }
        //compilationStorage.save(compilation);

        var delList = compilationsEventsRepository.findByCompilation_Id(compId);
        for (Long i : delList) {
            compilationsEventsRepository.deleteById(i);
        }

        List<EventShortDto> listEventShortDto = new ArrayList<>();
        if (updateCompilationRequestDto.getEvents() != null) {
            for (Long eventId : updateCompilationRequestDto.getEvents()) {
                Event event = eventRepository.findById(eventId).orElseThrow(() -> {
                    throw new NotFoundException(String.join("", "Event with id=", eventId.toString(),
                            " was not found"), new ErrorDtoUtil("The required object was not found.",
                            LocalDateTime.now()));
                });

                CompilationsEvents compilationsEvents = new CompilationsEvents();
                compilationsEvents.setCompilation(compilation);
                compilationsEvents.setEvent(event);
                compilationsEventsRepository.save(compilationsEvents);

                EventShortDto eventShortDto = CustomMapper.INSTANCE.toEventShortDto(event,
                        eventRequestRepository.countByStatusAndEvent_Id(EventRequestStatusEnum.CONFIRMED,
                                event.getId()),
                        statisticModuleClient.getCountViewsOfHit(String.join("", "/events/",
                                event.getId().toString())));
                listEventShortDto.add(eventShortDto);
            }
        }
        return CustomMapper.INSTANCE.toCompilationDto(compilation, listEventShortDto);
    }

    @Override
    public CompilationDto findCompilationById(Long compId) {
        var compilation = compilationRepository.findById(compId).orElseThrow(() -> {
            throw new NotFoundException(String.join("", "Compilation with id=", compId.toString(),
                    " was not found"), new ErrorDtoUtil("The required object was not found.",
                    LocalDateTime.now()));
        });

        var findList = compilationsEventsRepository.findEventIdByCompilation_Id(compId);
        List<EventShortDto> listEventShortDto = new ArrayList<>();
        for (Long eventId : findList) {
            Event event = eventRepository.findById(eventId).orElseThrow(() -> {
                throw new NotFoundException(String.join("", "Event with id=", eventId.toString(),
                        " was not found"), new ErrorDtoUtil("The required object was not found.",
                        LocalDateTime.now()));
            });

            EventShortDto eventShortDto = CustomMapper.INSTANCE.toEventShortDto(event,
                    eventRequestRepository.countByStatusAndEvent_Id(EventRequestStatusEnum.CONFIRMED,
                            event.getId()),
                    statisticModuleClient.getCountViewsOfHit(String.join("", "/events/",
                            event.getId().toString())));
            listEventShortDto.add(eventShortDto);
        }
        return CustomMapper.INSTANCE.toCompilationDto(compilation, listEventShortDto);
    }

    @Override
    public List<CompilationDto> findCompilation(Boolean pinned, Integer from, Integer size) {

        Sort sortBy = Sort.by(Sort.Order.asc("id"));
        Pageable page = PageRequest.of(from / size, size, sortBy);

        var comList = compilationRepository.findByPinned(pinned, page);
        if (comList.isEmpty()) {
            return new ArrayList<>();
        }

        List<CompilationDto> rez = new ArrayList<>();
        for (Compilation compilation : comList) {
            var findList = compilationsEventsRepository.findEventIdByCompilation_Id(compilation.getId());
            List<EventShortDto> listEventShortDto = new ArrayList<>();
            for (Long eventId : findList) {
                Event event = eventRepository.findById(eventId).orElseThrow(() -> {
                    throw new NotFoundException(String.join("", "Event with id=", eventId.toString(),
                            " was not found"), new ErrorDtoUtil("The required object was not found.",
                            LocalDateTime.now()));
                });

                EventShortDto eventShortDto = CustomMapper.INSTANCE.toEventShortDto(event,
                        eventRequestRepository.countByStatusAndEvent_Id(EventRequestStatusEnum.CONFIRMED,
                                event.getId()),
                        statisticModuleClient.getCountViewsOfHit(String.join("", "/events/",
                                event.getId().toString())));
                listEventShortDto.add(eventShortDto);
            }
            rez.add(CustomMapper.INSTANCE.toCompilationDto(compilation, listEventShortDto));
        }
        return rez;
    }
}
