package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Director;

import java.util.List;
import java.util.Optional;

public interface DirectorDao {
    List<Director> getDirectors();

    Director getDirectorById(long id);

    Director createDirector(Director director);

    Director updateDirector(Director director);

    void deleteDirector(long id);
}
