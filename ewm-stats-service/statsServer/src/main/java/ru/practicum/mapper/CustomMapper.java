package ru.practicum.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import ru.practicum.dto.HitsDto;
import ru.practicum.model.AppDict;
import ru.practicum.model.Hits;
import ru.practicum.model.UriDict;

@Mapper
public interface CustomMapper {
    CustomMapper INSTANCE = Mappers.getMapper(CustomMapper.class);

    @Mapping(source = "app", target = "appName")
    AppDict toAppDict(HitsDto hitsDto);

    @Mapping(source = "uri", target = "uriName")
    UriDict toUriDict(HitsDto hitsDto);

    @Mapping(source = "app.appName", target = "app")
    @Mapping(source = "uri.uriName", target = "uri")
    HitsDto toHitsDto(Hits hits);
}
