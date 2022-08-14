package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class InMemoryFilmStorage implements FilmStorage {
    private Map<Long, Film> films = new HashMap<>();
    private long idFilm;

    private long idFilms() {
        return ++idFilm;
    }

    @Override
    public List<Film> getFilms() {
        return new ArrayList<>(films.values());
    }

    @Override
    public Film getFilm(long id) {
        if (films.containsKey(id)) {
            return films.get(id);
        } else {
            throw new FilmNotFoundException("Такого фильма нет");
        }
    }

    @Override
    public List<Film> getPopularFilms(int count) {
        List<Film> topFilms = new ArrayList<>();
        if (count == 0) {
            return films.values().stream()
                    .sorted(new FilmCompatator())
                    .limit(10)
                    .collect(Collectors.toList());

        } else {
            return films.values().stream()
                    .sorted(new FilmCompatator())
                    .limit(count)
                    .collect(Collectors.toList());
        }
    }

    class FilmCompatator implements Comparator<Film> {
        @Override
        public int compare(Film o1, Film o2) {
            return o2.getUserLikes().size() - o1.getUserLikes().size();
        }
    }

    @Override
    public Film createFilm(Film newFilm) {
        newFilm.setId(idFilms());
        films.put(newFilm.getId(), newFilm);
        return newFilm;
    }

    @Override
    public Film updateFilm(Film newFilm) {
        if (films.containsKey(newFilm.getId())) {
            films.remove(newFilm.getId());
            films.put(newFilm.getId(), newFilm);
            return newFilm;
        } else {
            throw new UserNotFoundException("Такого пользователя не существует");
        }
    }

    @Override
    public void deleteFilm(long id) {
        if (films.containsKey(id)) {
            films.remove(id);
        } else {
            throw new FilmNotFoundException("Такого фильма не существует");
        }
    }
}
