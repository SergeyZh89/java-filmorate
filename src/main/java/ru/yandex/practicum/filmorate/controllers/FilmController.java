package ru.yandex.practicum.filmorate.controllers;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.time.LocalDate;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/films")
@NoArgsConstructor
public class FilmController {

    private FilmStorage filmStorage;
    private FilmService filmService;

    @Autowired
    public FilmController(@Qualifier("inMemoryFilmStorage") FilmStorage filmStorage, @Qualifier("filmService") FilmService filmService) {
        this.filmStorage = filmStorage;
        this.filmService = filmService;
    }

    @GetMapping
    public List<Film> getFilms() {
        log.debug("Получен запрос на список фильмов");
        return filmStorage.getFilms();
    }

    @GetMapping("/popular?count={count}")
    public List<Film> getPopularFilms(@RequestParam(required = false) int count) {
        log.debug("Получен запрос на список популярных фильмов");
        return filmStorage.getPopularFilms(count);
    }

    @PostMapping
    public Film createFilm(@RequestBody Film newFilm) {
        log.debug("Получен запрос на добавление фильма: id " + newFilm.getId());
        validatorFilm(newFilm);
        return filmStorage.createFilm(newFilm);
    }

    @PutMapping
    public Film updateFilm(@RequestBody Film newFilm) {
        log.debug("Получен запрос на обновление фильма");
        return filmStorage.updateFilm(newFilm);
    }

    @PutMapping("/{id}/like/{userId}")
    public Film userLikeFilm(@PathVariable long id, @PathVariable long userId) {
        return filmService.userLikeFilm(filmStorage.getFilm(id), userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public Film userDisLikeFilm(@PathVariable long id, @PathVariable long userId) {
        return filmService.userDisLikeFilm(filmStorage.getFilm(id), userId);
    }

    protected void validatorFilm(Film film) {
        if (film.getName().isBlank()) {
            throw new ValidationException("Имя не должно быть пустым");
        } else if (film.getDuration() <= 0) {
            throw new ValidationException("Продолжительность должна быть положительной");
        } else if (LocalDate.of(1895, 12, 28).isAfter(film.getReleaseDate())) {
            throw new ValidationException("Дата не должна быть раньше 1895 года 28 декабря");
        } else if (film.getDescription() != null) {
            if (film.getDescription().length() <= 200) {
                return;
            }
            throw new ValidationException("Длина описания не должна быть больше 200 символов");
        }
    }
}
