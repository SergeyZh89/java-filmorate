package ru.yandex.practicum.filmorate.dao;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;

@Component
public interface ReviewDao {

    Review createReview(Review review);

    Review updateReview(Review review);

    void deleteReview(long id);

    Review getReview(long id);

    List<Review> getAll();

    List<Review> getFilmReviews(long filmId);

    void like(long id, long userId);

    void dislike(long id, long userId);

    void deleteLike(long id, long userId);

    void deleteDislike(long id, long userId);

    boolean contains(long id);
}
