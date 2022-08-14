package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.FilmNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class InMemoryFilmStorage implements FilmStorage {
    private List<Film> films = new ArrayList<>();
    private long idFilm;

    private long idFilms() {
        return ++idFilm;
    }

    @Override
    public List<Film> getFilms() {
        return films;
    }

    @Override
    public Film getFilm(long id) {
        return films.stream().filter(film -> film.getId() == id).findFirst().orElseThrow(() -> new FilmNotFoundException("Такого фильма нет"));
    }

    @Override
    public List<Film> getPopularFilms(int count) {
        if (count != 0){
           return films.stream().sorted(Comparator.comparing(film -> film.getUserLikes().size())).collect(Collectors.toList());
        } else {
           return films.stream().sorted(Comparator.comparing(film -> film.getUserLikes().size())).limit(count).collect(Collectors.toList());
        }
    }

    @Override
    public Film createFilm(Film newFilm) {
        newFilm.setId(idFilms());
        films.add(newFilm);
        return newFilm;
    }

    @Override
    public Film updateFilm(Film newFilm) {
        for (Film film : films) {
            if (film.getId() == newFilm.getId()) {
                films.remove(film);
                films.add(newFilm);
            } else {
                throw new FilmNotFoundException("Такого фильма нет");
            }
        }
        return newFilm;
    }
}
