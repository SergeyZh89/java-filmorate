package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

@Service
public class FilmService {
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
}
