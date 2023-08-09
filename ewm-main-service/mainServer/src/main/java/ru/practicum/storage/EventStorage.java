package ru.practicum.storage;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;
import ru.practicum.enums.StateEnum;
import ru.practicum.model.Event;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface EventStorage extends JpaRepository<Event, Long> {

    @Override
    Event save(Event event);

    @Override
    Optional<Event> findById(Long aLong);

    List<Event> findByInitiator_Id(Long id, Pageable pageable);

    Optional<Event> findByIdAndState(Long id, StateEnum state);

    List<Event> findByCategory_Id(Long id);



    @Query("select e from Event e " +
            " where 1=1 " +
            " and e.initiator.id in ?1  " +
            " and  e.state in ?2  " +
            " and  e.category.id in ?3  " +
            " and  e.eventDate >= ?4  " +
            " and  e.eventDate <= ?5  "
//            " and (?1 is null or e.initiator.id in ?1 ) " +
//            " and  (?2 is null or e.state in ?2 ) " +
//            " and (?3 is null or  e.category.id in ?3 ) " +
//            " and (?4 is null or  e.eventDate >= ?4 ) " +
//            " and (?5 is null or  e.eventDate <= ?5  )"
    )
    List<Event> customXZ(@Nullable Collection<Long> ids,
                         @Nullable Collection<StateEnum> states,
                         @Nullable Collection<Long> ids1,
                         @Nullable LocalDateTime eventDateStart,
                         @Nullable LocalDateTime eventDateEnd,
                         Pageable pageable);



//    List<Event> findByInitiator_IdInAndStateInAndCategory_IdInAndEventDateBetween(Collection<Long> ids,
//                                                                                  Collection<StateEnum> states,
//                                                                                  Collection<Long> ids1,
//                                                                                  LocalDateTime eventDateStart,
//                                                                                  LocalDateTime eventDateEnd,
//                                                                                  Pageable pageable);


    @Query("select e from Event e " +
            "where (upper(e.annotation) like upper(?1) or upper(e.description) like upper(?2) )" +
            " and e.category.id in ?3" +
            " and e.paid = ?4" +
            " and e.eventDate between ?5 and ?6" +
            " and e.state = ?7")
    List<Event> findEventsByUsers(String annotation, String description, Collection<Long> ids,
                                  Boolean paid, LocalDateTime eventDateStart,
                                  LocalDateTime eventDateEnd, StateEnum state, Pageable pageable);


}
