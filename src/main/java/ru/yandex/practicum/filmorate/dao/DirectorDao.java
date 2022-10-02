package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Director;

import java.util.List;
import java.util.Optional;

public interface DirectorDao {
    List<Director> getDirectors();

    Optional<Director> getDirectorById(long id);

    Optional<Director> createDirector(Director director);

    Director updateDirector(Director director);

    void deleteDirector(long id);
}
