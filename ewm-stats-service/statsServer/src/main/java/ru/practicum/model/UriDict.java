package ru.practicum.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "uri_dict", schema = "public")
@NoArgsConstructor
@Data
public class UriDict {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "uri_name")
    private String uriName;
}
