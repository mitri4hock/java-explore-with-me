package ru.practicum.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.beans.factory.annotation.Value;
import ru.practicum.enums.StateEnum;
import ru.practicum.util.CustomLocation;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Entity
@Table(name = "events", schema = "public")
@Setter
@Getter
@ToString
@NoArgsConstructor
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "annotation")
    @NotBlank
    @Size(max = 2000, min = 20)
    private String annotation; //Краткое описание
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    @NotNull
    private Category category;
    @Column(name = "created_on")
    @NotNull
    private LocalDateTime createdOn; //Дата и время создания события (в формате "yyyy-MM-dd HH:mm:ss")
    @Column(name = "description")
    @NotBlank
    @Size(max = 7000, min = 20)
    private String description; //Полное описание события
    @Column(name = "event_date")
    @NotNull
    private LocalDateTime eventDate; //Дата и время на которые намечено событие (в формате "yyyy-MM-dd HH:mm:ss")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "initiator_id")
    @NotNull
    private User initiator; //Пользователь (краткая информация)
    @Column(name = "location")
    @NotNull
    private CustomLocation location; // Широта и долгота места проведения события
    @Column(name = "paid")
    @NotNull
    @Value("false")
    private Boolean paid; //Нужно ли оплачивать участие
    @Column(name = "participant_limit")
    @NotNull
    @Value("0")
    private Integer participantLimit;//Ограничение на количество участников. Значение 0 - означает отсутствие ограничения
    @Column(name = "published_on")
    private LocalDateTime publishedOn; // Дата и время публикации события (в формате "yyyy-MM-dd HH:mm:ss")
    @Column(name = "request_moderation")
    @NotNull
    @Value("true")
    private Boolean requestModeration; //Нужна ли пре-модерация заявок на участие
    @Column(name = "state")
    @Enumerated(EnumType.STRING)
    @NotNull
    private StateEnum state; //Список состояний жизненного цикла события
    @Column(name = "title")
    @NotBlank
    @Size(max = 120, min = 3)
    private String title; //Заголовок
}












