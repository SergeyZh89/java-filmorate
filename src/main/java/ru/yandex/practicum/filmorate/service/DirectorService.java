package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.DirectorDao;
import ru.yandex.practicum.filmorate.exceptions.DirectorNotFoundException;
import ru.yandex.practicum.filmorate.model.Director;

import java.util.List;
import java.util.Optional;

@Service
public class DirectorService implements DirectorDao {
    private final DirectorDao directorDao;

    public DirectorService(DirectorDao directorDao) {
        this.directorDao = directorDao;
    }

    @Override
    public List<Director> getDirectors() {
        return directorDao.getDirectors();
    }

    @Override
    public Optional<Director> getDirectorById(long id) {
        if (id <= 0) {
            throw new DirectorNotFoundException("Такого режиссера не существует");
        }
        return directorDao.getDirectorById(id);
    }

    @Override
    public Optional<Director> createDirector(Director director) {
        return directorDao.createDirector(director);
    }

    @Override
    public Director updateDirector(Director director) {
        if (director.getId() <= 0) {
            throw new DirectorNotFoundException("Такого режиссера не существует");
        }
        return directorDao.updateDirector(director);
    }

    @Override
    public void deleteDirector(long id) {
        directorDao.deleteDirector(id);
    }
}
