package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.List;

@Service
public class FilmService implements FilmStorage {

    private FilmStorage filmStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

    public void userLikeFilm(Film film, long id) {
        film.getUserLikes().add(id);
    }

    public Film userDisLikeFilm(Film film, long id) {
        if (film.getUserLikes().contains(id)) {
            film.getUserLikes().remove(id);
            return film;
        } else {
            throw new UserNotFoundException("Такого пользоватаеля не существует");
        }
    }

    @Override
    public List<Film> getFilms() {
        return filmStorage.getFilms();
    }

    @Override
    public Film createFilm(Film film) {
        return filmStorage.createFilm(film);
    }

    @Override
    public Film updateFilm(Film film) {
        return filmStorage.updateFilm(film);
    }

    @Override
    public Film getFilm(long id) {
        return filmStorage.getFilm(id);
    }

    @Override
    public List<Film> getPopularFilms(int count) {
        return filmStorage.getPopularFilms(count);
    }

    @Override
    public void deleteFilm(long id) {
        filmStorage.deleteFilm(id);
    }
}
