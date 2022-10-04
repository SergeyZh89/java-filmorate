package ru.yandex.practicum.filmorate.controllers;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.ReviewService;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/reviews")
@NoArgsConstructor
public class ReviewController {

    private ReviewService reviewService;

    @Autowired
    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping
    public Review createReview(@RequestBody Review review) {
        log.info("Добавление нового отзыва.");
        validateReview(review);
        return reviewService.createReview(review);
    }

    @PutMapping
    public Review updateReview(@RequestBody Review review) {
        log.info("Редактирование уже имеющегося отзыва.");
        validateReview(review);
        return reviewService.updateReview(review);
    }

    @DeleteMapping("/{id}")
    public void deleteReview(@PathVariable long id) {
        log.info("Удаление уже имеющегося отзыва {}.", id);
        reviewService.deleteReview(id);
    }

    @GetMapping("/{id}")
    public Review getReview(@PathVariable long id) {
        log.info("Получение отзыва по идентификатору {}.", id);
        return reviewService.getReview(id);
    }

    @GetMapping
    public List<Review> getFilmReviews(
            @RequestParam(required = false, name = "filmId", defaultValue = "0") long filmId,
            @RequestParam(required = false, name = "count", defaultValue = "10") int count
    ) {
        log.info("Получение {} отзывов по идентификатору фильма {}.", count, filmId);
        return reviewService.getFilmReviews(filmId, count);
    }

    @PutMapping("/{id}/like/{userId}")
    public void like(@PathVariable long id, @PathVariable long userId) {
        log.info("Пользователь {} ставит лайк отзыву {}.", userId, id);
        reviewService.like(id, userId);
    }

    @PutMapping("/{id}/dislike/{userId}")
    public void dislike(@PathVariable long id, @PathVariable long userId) {
        log.info("Пользователь {} ставит дизлайк отзыву {}.", userId, id);
        reviewService.dislike(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void deleteLike(@PathVariable long id, @PathVariable long userId) {
        log.info("Пользователь {} удаляет лайк у отзыва {}.", userId, id);
        reviewService.deleteLike(id, userId);
    }

    @DeleteMapping("/{id}/dislike/{userId}")
    public void deleteDislike(@PathVariable long id, @PathVariable long userId) {
        log.info("Пользователь {} удаляет дизлайк у отзыва {}.", userId, id);
        reviewService.deleteDislike(id, userId);
    }

    private void validateReview(Review review) {
        if (review.getContent().isEmpty() || review.getContent().isBlank()) {
            throw new ValidationException("Содержание не должно быть пустым.");
        } else if (review.getUserId() == 0) {
            throw new ValidationException("Идентификатор пользователя не должен быть пустым или равен 0.");
        } else if (review.getUserId() < 0) {
            throw new UserNotFoundException("Идентификатор пользователя должен быть больше 0.");
        } else if (review.getFilmId() == 0) {
            throw new ValidationException("Идентификатор фильма не должен быть пустым или равен 0.");
        } else if (review.getFilmId() < 0) {
            throw new FilmNotFoundException("Идентификатор фильма должен быть больше 0.");
        } else if (review.getIsPositive() == null) {
            throw new ValidationException("Отзыв должен быть положительным или отрицательным.");
        }
    }
}
