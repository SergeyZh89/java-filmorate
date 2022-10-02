package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.FilmDao;
import ru.yandex.practicum.filmorate.dao.impl.FilmDaoImpl;
import ru.yandex.practicum.filmorate.exceptions.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Optional;

@Service
public class FilmService implements FilmDao {
    private final FilmDaoImpl filmDao;

    @Autowired
    public FilmService(FilmDaoImpl filmDao) {
        this.filmDao = filmDao;
    }

    @Override
    public void userLikeFilm(Film film, long id) {
        filmDao.userLikeFilm(film, id);
    }

    @Override
    public Film userDisLikeFilm(Film film, long id) {
        if (id <= 0) {
            throw new UserNotFoundException("Такого пользоватаеля не существует");
        }
        return filmDao.userDisLikeFilm(film, id);
    }

    @Override
    public List<Film> getFilms() {
        return filmDao.getFilms();
    }

    @Override
    public Film createFilm(Film film) {
        if (film.getMpa() == null) {
            throw new ValidationException("Требуется указать рейтинг фильма");
        }
        return filmDao.createFilm(film);
    }

    @Override
    public Film updateFilm(Film film) {
        if (film.getId() <= 0) {
            throw new FilmNotFoundException("Такого фильма не существует");
        }
        return filmDao.updateFilm(film);
    }

    @Override
    public Optional<Film> getFilm(long id) {
        if (id <= 0) {
            throw new FilmNotFoundException("Такого фильма не существует");
        }
        return filmDao.getFilm(id);
    }

    @Override
    public List<Film> getPopularFilms(int count, Long genreId, Integer year) {
        return filmDao.getPopularFilms(count, genreId, year);
    }

    public List<Film> getCommonFilms(long userId, long friendId){
        return filmDao.getCommonFilms(userId, friendId);
    }

    public List<Film> getFilmsDirector(int director_id, String sortBy){
        return filmDao.sortFilmsDirector(director_id, sortBy);
    }

    @Override
    public void deleteFilm(long id) {
        filmDao.deleteFilm(id);
    }
}
