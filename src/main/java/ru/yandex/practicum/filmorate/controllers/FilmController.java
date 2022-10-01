package ru.yandex.practicum.filmorate.controllers;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.time.LocalDate;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/films")
@NoArgsConstructor
public class FilmController {

    private FilmService filmService;

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping
    public List<Film> getFilms() {
        log.debug("Получен запрос на список фильмов");
        return filmService.getFilms();
    }

    @GetMapping("/{id}")
    public Film getFilm(@PathVariable long id) {
        log.debug("Получен запрос фильмов под номером: " + id);
        return filmService.getFilm(id);
    }

    @GetMapping("/popular")
    public List<Film> getPopularFilms(@RequestParam(required = false, defaultValue = "10") int count) {
        log.debug("Получен запрос на список популярных фильмов");
        return filmService.getPopularFilms(count);
    }

    @GetMapping("/common")
    public List<Film> getCommonFilms(@RequestParam long userId, @RequestParam long friendId){
        return filmService.getCommonFilms(userId, friendId);
    }

    @PostMapping
    public Film createFilm(@RequestBody Film newFilm) {
        log.debug("Получен запрос на добавление фильма: id " + newFilm.getId());
        validatorFilm(newFilm);
        return filmService.createFilm(newFilm);
    }

    @PutMapping
    public Film updateFilm(@RequestBody Film newFilm) {
        log.debug("Получен запрос на обновление фильма: id " + newFilm.getId());
        return filmService.updateFilm(newFilm);
    }

    @PutMapping("/{id}/like/{userId}")
    public void userLikeFilm(@PathVariable long id, @PathVariable long userId) {
        log.debug("Получен запрос на добавление лайка фильму: id" + id + " от пользователя: id " + userId);
        filmService.userLikeFilm(filmService.getFilm(id), userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public Film userDisLikeFilm(@PathVariable long id, @PathVariable long userId) {
        log.debug("Получен запрос на удаление лайка");
        return filmService.userDisLikeFilm(filmService.getFilm(id), userId);
    }

    @DeleteMapping("{id}")
    public void deleteFilm(@PathVariable long id) {
        log.debug("Получен запрос на удаление фильма: id " + id);
        filmService.deleteFilm(id);
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
