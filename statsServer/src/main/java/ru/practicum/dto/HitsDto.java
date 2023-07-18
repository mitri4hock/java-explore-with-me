package ru.practicum.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HitsDto {
    private String app;
    private String uri;
    private String ip;
    private String timestamp;
}
