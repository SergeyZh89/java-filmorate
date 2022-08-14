package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

@Service
public class FilmService {

    public Film userLikeFilm(Film film, long id) {
        film.getUserLikes().add(id);
        return film;
    }

    public Film userDisLikeFilm(Film film, long id) {
        if (film.getUserLikes().contains(id)) {
            film.getUserLikes().remove(id);
        }
        return film;
    }
}
