package ru.practicum.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.dto.HitsDto;
import ru.practicum.model.AppDict;
import ru.practicum.model.Hits;
import ru.practicum.model.UriDict;


@UtilityClass
public class HitsMapper {
    public Hits toHits(HitsDto hitsDto, AppDict appDict, UriDict uriDict) {
        Hits rezult = new Hits();
        rezult.setIp(hitsDto.getIp());
        rezult.setTimestamp(hitsDto.getTimestamp());
        rezult.setApp(appDict);
        rezult.setUri(uriDict);
        return rezult;
    }
}
