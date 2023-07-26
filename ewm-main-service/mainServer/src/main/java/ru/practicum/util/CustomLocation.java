package ru.practicum.util;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.PersistenceConstructor;

import java.io.Serializable;

@Getter
@Setter
@ToString
public class CustomLocation implements Serializable {
    private final double lat;
    private final double lon;

    @PersistenceConstructor
    public CustomLocation(double x, double y) {
        this.lat = x;
        this.lon = y;
    }
}
