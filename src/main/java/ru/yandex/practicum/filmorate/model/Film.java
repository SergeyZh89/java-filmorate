package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Film {
    private long id;
    private String name;
    private LocalDate releaseDate;
    private String description;
    private int duration;
    private RatingMpa mpa;
    private List<Genre> genres = new ArrayList<>();
    private List<Long> userLikes = new ArrayList<>();
    private List <Director> directors = new ArrayList<>();
}
