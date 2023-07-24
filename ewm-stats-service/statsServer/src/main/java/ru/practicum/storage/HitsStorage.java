package ru.practicum.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.dto.ViewStatsDto;
import ru.practicum.model.Hits;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Repository
public interface HitsStorage extends JpaRepository<Hits, Long> {
    Hits save(Hits hits);

    @Query("select new ru.practicum.dto.ViewStatsDto(t.app.appName , t.uri.uriName , count(distinct t.ip) ) " +
            " from Hits as t " +
            " where t.timestamp between ?1 and ?2 " +
            " group by t.app.appName, t.uri.uriName " +
            " order by count(distinct t.ip) desc ")
    List<ViewStatsDto> getStatsIpIsDistinctAndUriIsNot(LocalDateTime timestampStart, LocalDateTime timestampEnd);

    @Query("select new ru.practicum.dto.ViewStatsDto(t.app.appName , t.uri.uriName , count(t.ip) ) " +
            " from Hits as t " +
            " where t.timestamp between ?1 and ?2 " +
            " group by t.app.appName, t.uri.uriName " +
            " order by count(t.ip) desc ")
    List<ViewStatsDto> getStatsIpIsAnDistinctAndUriIsNot(LocalDateTime timestampStart, LocalDateTime timestampEnd);

    @Query("select new ru.practicum.dto.ViewStatsDto(t.app.appName , t.uri.uriName , count(distinct t.ip) ) " +
            " from Hits as t " +
            " where t.timestamp between ?1 and ?2 " +
            " and t.uri.uriName in ?3" +
            " group by t.app.appName, t.uri.uriName " +
            " order by count(distinct t.ip) desc ")
    List<ViewStatsDto> getStatsIpIsDistinctAndUriIsBe(LocalDateTime timestampStart, LocalDateTime timestampEnd,
                                                      Collection<String> uriNames);

    @Query("select new ru.practicum.dto.ViewStatsDto(t.app.appName , t.uri.uriName , count(t.ip) ) " +
            " from Hits as t " +
            " where t.timestamp between ?1 and ?2 " +
            " and t.uri.uriName in ?3" +
            " group by t.app.appName, t.uri.uriName " +
            " order by count(t.ip) desc ")
    List<ViewStatsDto> getStatsIpIsAnDistinctAndUriIsBe(LocalDateTime timestampStart, LocalDateTime timestampEnd,
                                                        Collection<String> uriNames);
}
