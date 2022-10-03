package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.impl.FilmDaoImpl;
import ru.yandex.practicum.filmorate.dao.impl.ReviewDaoImpl;
import ru.yandex.practicum.filmorate.dao.impl.UserDaoImpl;
import ru.yandex.practicum.filmorate.exceptions.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ReviewNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.Review;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReviewService {

    private final ReviewDaoImpl reviewDao;
    private final UserDaoImpl userDao;
    private final FilmDaoImpl filmDao;

    @Autowired
    public ReviewService(ReviewDaoImpl reviewDao, UserDaoImpl userDao, FilmDaoImpl filmDao) {
        this.reviewDao = reviewDao;
        this.userDao = userDao;
        this.filmDao = filmDao;
    }

    public Review createReview(Review review) {
        checkIfFilmExists(review.getFilmId());
        checkIfUserExists(review.getUserId());
        return reviewDao.createReview(review);
    }

    public Review updateReview(Review review) {
        checkIfFilmExists(review.getFilmId());
        checkIfUserExists(review.getUserId());
        checkIfReviewExists(review.getReviewId());
        return reviewDao.updateReview(review);
    }

    public void deleteReview(long id) {
        reviewDao.deleteReview(id);
    }

    public Review getReview(long id) {
        checkIfReviewExists(id);
        return reviewDao.getReview(id);
    }

    public List<Review> getFilmReviews(long filmId, int count) {
        return filmId == 0 ?
                getSortedLimitedList(reviewDao.getAll(), count) :
                getSortedLimitedList(reviewDao.getFilmReviews(filmId), count);
    }

    public void like(long id, long userId) {
        checkIfUserExists(userId);
        checkIfReviewExists(id);
        reviewDao.like(id, userId);
    }

    public void dislike(long id, long userId) {
        checkIfUserExists(userId);
        checkIfReviewExists(id);
        reviewDao.dislike(id, userId);
    }

    public void deleteLike(long id, long userId) {
        checkIfUserExists(userId);
        checkIfReviewExists(id);
        reviewDao.deleteLike(id, userId);
    }

    public void deleteDislike(long id, long userId) {
        checkIfUserExists(userId);
        checkIfReviewExists(id);
        reviewDao.deleteDislike(id, userId);
    }

    private void checkIfFilmExists(long filmId) {
        if (filmDao.getFilm(filmId).isEmpty()) {
            throw new FilmNotFoundException(String.format("Фильм с идентификатором %d не найден.", filmId));
        }
    }

    private void checkIfReviewExists(long reviewId) {
        if (!reviewDao.contains(reviewId)) {
            throw new ReviewNotFoundException(String.format("Отзыв с идентификатором %d не найден.", reviewId));
        }
    }

    private void checkIfUserExists(long userId) {
        if (userDao.getUser(userId).isEmpty()) {
            throw new UserNotFoundException(String.format("Пользователя с идентификатором %d не найден.", userId));
        }
    }

    private List<Review> getSortedLimitedList(List<Review> reviews, int count) {
        return reviews.stream()
                .sorted(Comparator.comparingInt(Review::getUseful).reversed())
                .limit(count)
                .collect(Collectors.toList());
    }
}