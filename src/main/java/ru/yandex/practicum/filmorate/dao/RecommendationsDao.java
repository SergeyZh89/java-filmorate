package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface RecommendationsDao {
    List<Film> getRecommendationsByUser(long userId, int recCount);
}
