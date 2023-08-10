package ru.practicum.storage;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import ru.practicum.enums.StateEnum;
import ru.practicum.model.Event;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Repository
public class CustomStorage {
    EntityManager em;

    public CustomStorage(EntityManager em) {
        this.em = em;
    }

    public List<Event> findEventByFilters(Collection<Long> ids,
                                          Collection<StateEnum> states,
                                          Collection<Long> ids1,
                                          LocalDateTime eventDateStart,
                                          LocalDateTime eventDateEnd,
                                          Pageable pageable) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Event> cq = cb.createQuery(Event.class);

        Root<Event> event = cq.from(Event.class);
        Predicate idsPredicate = null;
        Predicate statesPredicate = null;
        Predicate ids1Predicate = null;
        Predicate eventDateStartPredicate = null;
        if (ids != null) {
            idsPredicate = event.get("initiator").get("id").in(ids);
        } else {
            idsPredicate = event.get("id").isNotNull();
        }
        if (states != null) {
            statesPredicate = event.get("state").in(states);
        } else {
            statesPredicate = event.get("id").isNotNull();
        }
        if (ids1 != null) {
            ids1Predicate = event.get("category").get("id").in(ids1);
        } else {
            ids1Predicate = event.get("id").isNotNull();
        }
        if (eventDateStart != null && eventDateEnd != null) {
            eventDateStartPredicate = cb.between(event.get("eventDate"),
                    eventDateStart, eventDateEnd);
        } else {
            eventDateStartPredicate = event.get("id").isNotNull();
        }
        cq.where(idsPredicate, statesPredicate, ids1Predicate, eventDateStartPredicate);
        TypedQuery<Event> query = em.createQuery(cq)
                .setMaxResults(pageable.getPageSize())
                .setFirstResult(Long.valueOf(pageable.getOffset()).intValue());
        return query.getResultList();
    }

    public List<Event> findEventsByUsers(String text, Collection<Long> categories, Boolean paid,
                                         LocalDateTime eventDateStart, LocalDateTime eventDateEnd,
                                         StateEnum state, Pageable pageable) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Event> cq = cb.createQuery(Event.class);

        Root<Event> event = cq.from(Event.class);
        Predicate annotationPredicate = null;
        Predicate descriptionPredicate = null;
        Predicate categoryPredicate = null;
        Predicate paidPredicate = null;
        Predicate eventDatePredicate = null;
        Predicate statePredicate = null;

        if (text != null) {
            annotationPredicate = cb.like(event.get("annotation"), "%" + text + "%");
            descriptionPredicate = cb.like(event.get("description"), "%" + text + "%");
        } else {
            annotationPredicate = event.get("id").isNotNull();
            descriptionPredicate = event.get("id").isNotNull();
        }
        Predicate likePredicate = cb.or(annotationPredicate, descriptionPredicate);
        if (categories != null) {
            categoryPredicate = event.get("category").get("id").in(categories);
        } else {
            categoryPredicate = event.get("id").isNotNull();
        }
        if (paid != null) {
            paidPredicate = cb.equal(event.get("paid"), paid);
        } else {
            paidPredicate = event.get("id").isNotNull();
        }
        eventDatePredicate = cb.between(event.get("eventDate"), eventDateStart, eventDateEnd);
        statePredicate = cb.equal(event.get("state"), state);

        cq.where(likePredicate, categoryPredicate, paidPredicate,
                eventDatePredicate, statePredicate);
        TypedQuery<Event> query = em.createQuery(cq)
                .setMaxResults(pageable.getPageSize())
                .setFirstResult(Long.valueOf(pageable.getOffset()).intValue());
        return query.getResultList();
    }
}
