package ru.practicum.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "app_dict", schema = "public")
@NoArgsConstructor
@Data
public class AppDict {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "app_name")
    private String appName;
}
