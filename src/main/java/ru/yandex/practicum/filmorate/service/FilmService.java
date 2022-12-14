package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.FilmDao;
import ru.yandex.practicum.filmorate.exceptions.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FilmService {
    private final FilmDao filmDao;

    public void userLikeFilm(long filmId, long id) {
        filmDao.userLikeFilm(filmId, id);
    }

    public Film userDisLikeFilm(long userId, long id) {
        if (id <= 0) {
            throw new UserNotFoundException("Такого пользоватаеля не существует");
        }
        return filmDao.userDisLikeFilm(userId, id);
    }

    public List<Film> getFilms() {
        return filmDao.getFilms();
    }

    public Film createFilm(Film film) {
        if (film.getMpa() == null) {
            throw new ValidationException("Требуется указать рейтинг фильма");
        }
        return filmDao.createFilm(film);
    }

    public Film updateFilm(Film film) {
        if (film.getId() <= 0) {
            throw new FilmNotFoundException("Такого фильма не существует");
        }
        return filmDao.updateFilm(film);
    }

    public Optional<Film> getFilm(long id) {
        if (id <= 0) {
            throw new FilmNotFoundException("Такого фильма не существует");
        }
        return filmDao.getFilm(id);
    }

    public List<Film> getPopularFilms(int count, Long genreId, Integer year) {
        return filmDao.getPopularFilms(count, genreId, year);
    }

    public List<Film> getCommonFilms(long userId, long friendId){
        return filmDao.getCommonFilms(userId, friendId);
    }

    public List<Film> getFilmsDirector(int directorId, String sortBy){
        return filmDao.sortFilmsDirector(directorId, sortBy);
    }

    public void deleteFilm(long id) {
        filmDao.deleteFilm(id);
    }

    public List<Film> getPopularFilmsBySearch(String query, List<String> by) {
       return filmDao.getPopularFilmsBySearch(query, by);
    }
}
