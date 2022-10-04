package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.impl.RatingMpaDaoImpl;
import ru.yandex.practicum.filmorate.exceptions.RatindMpaNotFoundException;
import ru.yandex.practicum.filmorate.model.RatingMpa;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RatingMpaService {
    private final RatingMpaDaoImpl ratingMpaDao;

    public List<RatingMpa> getRatings() {
        return ratingMpaDao.getRatings();
    }

    public RatingMpa getRatingsById(int id) {
        if (id <= 0) {
            throw new RatindMpaNotFoundException("Такого рейтинга не существует");
        }
        return ratingMpaDao.getRatingsById(id);
    }
}
