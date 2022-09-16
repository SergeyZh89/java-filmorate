package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import org.springframework.stereotype.Component;

@Data
@Component
public class RatingMpa{
    private int id;
    private String name;
}