package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Component
public class Event {
    private long timestamp;
    private long userId;
    private String eventType;
    private String operation;
    private long eventId;
    private long entityId;
}
