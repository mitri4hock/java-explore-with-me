package ru.practicum.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Table(name = "comments", schema = "public")
@Setter
@Getter
@ToString
@NoArgsConstructor
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id")
    @NotNull
    private Event event; // комментируемое событие
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "commentator_id")
    @NotNull
    private User commentator; //комментатор
    @Column(name = "comment_text")
    @NotBlank
    @Length(max = 5000)
    private String commentText; // текст комментария
    @Column(name = "created_date")
    @NotNull
    private LocalDateTime createdDate; // Дата и время создания комментария
}
