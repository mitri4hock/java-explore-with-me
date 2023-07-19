package ru.practicum.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.HitsDto;
import ru.practicum.mapper.HitsMapper;
import ru.practicum.model.AppDict;
import ru.practicum.model.Hits;
import ru.practicum.model.UriDict;
import ru.practicum.storage.AppDictStorage;
import ru.practicum.storage.HitsStorage;
import ru.practicum.storage.UriDictStorage;

@Service
@Slf4j
@Transactional(readOnly = true)
@AllArgsConstructor
public class HitServiceImpl implements HitService {
    private final HitsStorage hitsStorage;
    private final AppDictStorage appDictStorage;
    private final UriDictStorage uriDictStorage;

    @Override
    @Transactional
    public HitsDto createHit(HitsDto hitsDto) {
        if (!appDictStorage.existsByAppName(hitsDto.getApp())) {
            AppDict newAppDict = HitsMapper.toAppDict(hitsDto);
            log.info("Создан новый элемент в таблице app_dict: {}", newAppDict);
            appDictStorage.save(newAppDict);
        }
        if (!uriDictStorage.existsByUriName(hitsDto.getUri())) {
            UriDict newUriDict = HitsMapper.toUriDict(hitsDto);
            log.info("Создан новый элемент в таблице uri_dict: {}", newUriDict);
            uriDictStorage.save(newUriDict);
        }
        Hits newHits = HitsMapper.toHits(hitsDto,
                appDictStorage.findByAppName(hitsDto.getApp()).orElse(null),
                uriDictStorage.findByUriName(hitsDto.getUri()).orElse(null));
        log.info("Создана новая запись в таблице hits: {}", newHits);
        hitsStorage.save(newHits);
        return HitsMapper.toHitsDto(newHits);
    }
}
