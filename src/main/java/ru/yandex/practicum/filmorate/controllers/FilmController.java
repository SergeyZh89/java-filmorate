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

    protected void validatorFilm(Film film) {
        if (film.getName().isBlank()) {
            throw new ValidationException(HttpStatus.BAD_REQUEST, "Имя не должно быть пустым");
        } else if (film.getDuration() <= 0) {
            throw new ValidationException(HttpStatus.BAD_REQUEST, "Продолжительность должна быть положительной");
        } else if (LocalDate.of(1895, 12, 28).isAfter(film.getReleaseDate())) {
            throw new ValidationException(HttpStatus.BAD_REQUEST, "Дата не должна быть раньше 1895 года 28 декабря");
        } else if (film.getDescription() != null) {
            if (film.getDescription().length() <= 200) {
                return;
            }
            throw new ValidationException(HttpStatus.BAD_REQUEST, "Длина описания не должна быть больше 200 символов");
        }
    }

    private int idFilms() {
        return ++idFilm;
    }

    @GetMapping
    public List<Film> getFilms() {
        log.debug("Получен запрос на список фильмов");
        return films;
    }

    @PostMapping
    public Film create(@RequestBody Film newFilm) {
        log.debug("Получен запрос на добавление фильма");
        validatorFilm(newFilm);
        newFilm.setId(idFilms());
        films.add(newFilm);
        return newFilm;
    }

    @PutMapping
    public Film update(@RequestBody Film newFilm) {
        log.debug("Получен запрос на обновление фильма");
        for (Film film : films) {
            if (film.getId() == newFilm.getId()) {
                films.remove(film);
                films.add(newFilm);
            } else {
                throw new ValidationException(HttpStatus.INTERNAL_SERVER_ERROR, "Такого фильма нет");
            }
        }
        return newFilm;
    }
}
