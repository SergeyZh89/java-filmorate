package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.DirectorDao;
import ru.yandex.practicum.filmorate.exceptions.DirectorNotFoundException;
import ru.yandex.practicum.filmorate.model.Director;

import java.util.List;
import java.util.Optional;

@Service
public class DirectorService {
    private final DirectorDao directorDao;

    public DirectorService(DirectorDao directorDao) {
        this.directorDao = directorDao;
    }

    public List<Director> getDirectors() {
        return directorDao.getDirectors();
    }

    public Director getDirectorById(long id) {
        if (id <= 0) {
            throw new DirectorNotFoundException("Такого режиссера не существует");
        }
        return directorDao.getDirectorById(id);
    }

    public Director createDirector(Director director) {
        return directorDao.createDirector(director);
    }

    public Director updateDirector(Director director) {
        if (director.getId() <= 0 || directorDao.getDirectorById(director.getId()) == null) {
            throw new DirectorNotFoundException(String.format("Режиссёра с идентификатором %d не существует.", director.getId()));
        }
        return directorDao.updateDirector(director);
    }

    public void deleteDirector(long id) {
        directorDao.deleteDirector(id);
    }
}
