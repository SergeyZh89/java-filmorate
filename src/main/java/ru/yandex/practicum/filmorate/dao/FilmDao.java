package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.RatingMpa;

import java.util.List;
import java.util.Optional;

public interface FilmDao {
    List<Film> getFilms();

    Film createFilm(Film film);

    Film updateFilm(Film film);

    Optional<Film> getFilm(long id);

    List<Film> getPopularFilms(int count, Long genreId, Integer year);

    void userLikeFilm(long filmId, long id);

    Film userDisLikeFilm(long filmId, long id);

    void deleteFilm(long id);

    RatingMpa ratingMpaMapper(int id);

    List<Genre> genreMapper(int id);

    List<Long> userLikesMapper(int id);

    List<Film> getPopularFilmsBySearch(String query, List<String> by);

    List<Film> getCommonFilms(long userId, long friendId);

    List<Film> sortFilmsDirector(int directorId, String sortBy);
}
