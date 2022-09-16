package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.RatingMpa;

import java.util.List;

public interface RatingMpaDao {
    List<RatingMpa> getRatings();

    RatingMpa getRatingsById(int id);
}
