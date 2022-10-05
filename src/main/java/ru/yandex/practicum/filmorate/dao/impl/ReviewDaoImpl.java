package ru.yandex.practicum.filmorate.dao.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.ReviewDao;
import ru.yandex.practicum.filmorate.model.Review;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Component
public class ReviewDaoImpl implements ReviewDao {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public Review createReview(Review review) {
        jdbcTemplate.update(
                "INSERT INTO reviews (content, is_positive, user_id, film_id) VALUES (?, ?, ?, ?);",
                review.getContent(),
                review.getIsPositive(),
                review.getUserId(),
                review.getFilmId()
        );
        SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet("SELECT * FROM reviews ORDER BY review_id DESC LIMIT 1;");
        sqlRowSet.next();
        return getReview(sqlRowSet);
    }

    @Override
    public Review updateReview(Review review) {
        jdbcTemplate.update(
                "UPDATE reviews SET content = ?, is_positive = ? WHERE review_id = ?;",
                review.getContent(),
                review.getIsPositive(),
                review.getReviewId()
        );
        return getReview(review.getReviewId());
    }

    @Override
    public void deleteReview(long id) {
        jdbcTemplate.update("DELETE FROM review_likes WHERE review_id = ?;", id);
        jdbcTemplate.update("DELETE FROM reviews WHERE review_id = ?;", id);
    }

    @Override
    public Review getReview(long id) {
        SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet("SELECT * FROM reviews WHERE review_id = ?;", id);
        sqlRowSet.next();
        return getReview(sqlRowSet);
    }

    @Override
    public List<Review> getAll() {
        List<Review> all = new ArrayList<>();
        SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet("SELECT * FROM reviews;");
        while (sqlRowSet.next()) {
            Review review = getReview(sqlRowSet);
            all.add(review);
        }
        return all;
    }

    @Override
    public List<Review> getFilmReviews(long filmId) {
        List<Review> reviews = new ArrayList<>();
        SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet("SELECT * FROM reviews WHERE film_id = ?;", filmId);
        while (sqlRowSet.next()) {
            Review review = getReview(sqlRowSet);
            reviews.add(review);
        }
        return reviews;
    }

    @Override
    public void addLike(long id, long userId) {
        jdbcTemplate.update("INSERT INTO review_likes (review_id, user_id) VALUES (?, ?);", id, userId);
        updateUseful(id);
    }

    @Override
    public void addDislike(long id, long userId) {
        jdbcTemplate.update("INSERT INTO review_dislikes (review_id, user_id) VALUES (?, ?);", id, userId);
        updateUseful(id);
    }

    @Override
    public void deleteLike(long id, long userId) {
        jdbcTemplate.update("DELETE FROM review_likes WHERE review_id = ? AND user_id = ?;", id, userId);
        updateUseful(id);
    }

    @Override
    public void deleteDislike(long id, long userId) {
        jdbcTemplate.update("DELETE FROM review_dislikes WHERE review_id = ? AND user_id = ?;", id, userId);
        updateUseful(id);
    }

    @Override
    public boolean contains(long id) {
        SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet("SELECT * FROM reviews WHERE review_id = ?;", id);
        return sqlRowSet.next();
    }

    private Review getReview(SqlRowSet sqlRowSet) {
        return Review
                .builder()
                .reviewId(sqlRowSet.getLong("review_id"))
                .content(sqlRowSet.getString("content"))
                .isPositive(sqlRowSet.getBoolean("is_positive"))
                .userId(sqlRowSet.getLong("user_id"))
                .filmId(sqlRowSet.getLong("film_id"))
                .useful(sqlRowSet.getInt("useful"))
                .build();
    }

    private void updateUseful(long id) {
        jdbcTemplate.update(
                "UPDATE reviews AS r " +
                        "SET useful = (SELECT COUNT(rl.user_id) FROM review_likes AS rl " +
                        "WHERE rl.review_id = r.review_id) - " +
                        "(SELECT COUNT(rd.user_id) FROM review_dislikes AS rd WHERE rd.review_id = r.review_id) " +
                        "WHERE review_id = ?;",
                id
        );
    }
}
