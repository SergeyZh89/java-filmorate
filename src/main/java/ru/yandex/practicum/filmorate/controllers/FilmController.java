package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/films")
public class FilmController {
    private List<Film> films = new ArrayList<>();
    private int idFilm;

    private int idFilms() {
        return ++idFilm;
    }

    private boolean validatorFilm(Film film) {
        if (film.getName().isBlank() || (film.getDuration() <= 0) || LocalDate.of(1895, 12, 28).isAfter(film.getReleaseDate())) {
            return false;
        } else if (film.getDescription() != null) {
            if (film.getDescription().length() > 200) {
                return false;
            }
        } return true;
    }


    @GetMapping
    public List<Film> getFilms() {
        return films;
    }

    @PostMapping
    public Film create(@RequestBody Film film) {
        if (validatorFilm(film)) {
            film.setId(idFilms());
            films.add(film);
        } else {
            throw new ValidationException(HttpStatus.BAD_REQUEST);
        }
        return film;
    }

    @PutMapping
    public Film update(@RequestBody Film film) {
        for (Film film1: films) {
            if (film1.getId() == film.getId()) {
                films.remove(film1);
                films.add(film);
            } else {
                throw new ValidationException(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
        return film;
    }
}
