package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.impl.GenreDaoImpl;
import ru.yandex.practicum.filmorate.exceptions.GenreNotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GenreService {
    private final GenreDaoImpl genreDao;

    public List<Genre> getGenres() {
        return genreDao.getGenres();
    }

    public Genre getGenresById(int id) {
        if (id <= 0) {
            throw new GenreNotFoundException("Такого жанра не существует");
        }
        return genreDao.getGenresById(id);
    }
}
