package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.RatingMpaDao;
import ru.yandex.practicum.filmorate.dao.impl.RatingMpaDaoImpl;
import ru.yandex.practicum.filmorate.exceptions.RatindMpaNotFoundException;
import ru.yandex.practicum.filmorate.model.RatingMpa;

import java.util.List;

@Service
public class RatingMpaService implements RatingMpaDao {
    private final RatingMpaDaoImpl ratingMpaDao;

    @Autowired
    public RatingMpaService(RatingMpaDaoImpl ratingMpaDao) {
        this.ratingMpaDao = ratingMpaDao;
    }

    @Override
    public List<RatingMpa> getRatings() {
        return ratingMpaDao.getRatings();
    }

    @Override
    public RatingMpa getRatingsById(int id) {
        if (id <= 0) {
            throw new RatindMpaNotFoundException("Такого рейтинга не существует");
        }
        return ratingMpaDao.getRatingsById(id);
    }
}
