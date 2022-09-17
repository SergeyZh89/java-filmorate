package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Film {
    private long id;
    private String name;
    private LocalDate releaseDate;
    private String description;
    private int duration;
    private RatingMpa mpa;
    private List<Genre> genres = new ArrayList<>();
    private List<Long> userLikes = new ArrayList<>();
}

