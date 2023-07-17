package ru.practicum.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.dto.HitsDto;
import ru.practicum.model.AppDict;
import ru.practicum.model.Hits;
import ru.practicum.model.UriDict;

@UtilityClass
public class HitsMapper {
    public AppDict toAppDict(HitsDto hitsDto) {
        AppDict rezult = new AppDict();
        rezult.setAppName(hitsDto.getApp());
        return rezult;
    }

    public UriDict toUriDict(HitsDto hitsDto) {
        UriDict rezult = new UriDict();
        rezult.setUriName(hitsDto.getUri());
        return rezult;
    }

    public Hits toHits(HitsDto hitsDto, AppDict appDict, UriDict uriDict) {
        Hits rezult = new Hits();
        rezult.setIp(hitsDto.getIp());
        rezult.setTimestamp(hitsDto.getTimestamp());
        rezult.setApp(appDict);
        rezult.setUri(uriDict);
        return rezult;
    }

    public HitsDto toHitsDto(Hits hits) {
        HitsDto rezult = new HitsDto();
        rezult.setApp(hits.getApp().getAppName());
        rezult.setUri(hits.getUri().getUriName());
        rezult.setIp(hits.getIp());
        rezult.setTimestamp(hits.getTimestamp());
        return rezult;
    }
}
