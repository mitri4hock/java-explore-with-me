package ru.practicum.model;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@Table(name = "compilations", schema = "public")
@Setter
@Getter
@ToString
@NoArgsConstructor
public class Compilation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "pinned")
    @NotNull
    private Boolean pinned; //Закреплена ли подборка на главной странице сайта
    @Column(name = "title")
    @NotBlank
    @Size(max = 50, min = 1)
    private String title; //Заголовок подборки
}
