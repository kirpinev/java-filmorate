package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ReviewValidationException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.filmReview.ReviewStorage;
import ru.yandex.practicum.filmorate.storage.filmReviewRating.ReviewRatingStorage;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ReviewService {

    ReviewStorage reviewStorage;
    UserService userService;
    FilmService filmService;
    ReviewRatingStorage reviewLikeStorage;

    @Autowired
    public ReviewService(
            @Qualifier("ReviewDbStorage") ReviewStorage reviewStorage,
            UserService userService,
            FilmService filmService,
            @Qualifier("ReviewRatingDbStorage") ReviewRatingStorage reviewLikeStorage
    ) {
        this.reviewStorage = reviewStorage;
        this.userService = userService;
        this.filmService = filmService;
        this.reviewLikeStorage = reviewLikeStorage;
    }

    public Review getById(Integer id) {
        log.debug("Запрошен отзыв с id = {}", id);
        checkIfReviewExists(id);
        Review review = reviewStorage.getById(id);
        log.trace("Получен отзыв: {}", review);
        return review;
    }

    public List<Review> getAll(Integer filmId, Integer count) {
        List<Review> reviews;
        if (filmId == -1) {
            log.debug("Запрошены все отзывы");
            reviews = reviewStorage.getAll();
        } else {
            log.debug("Запрошены все отзывы фильма с id = {}", filmId);
            reviews = reviewStorage.getByFilmId(filmId);
        }
        log.debug("Количество выгруженных отзывов: {}", reviews.size());
        log.trace("Перечень отзывов: {}", reviews.stream().map(Review::toString));
        if (reviews.size() > count) {
            reviews = reviews.stream().limit(count).collect(Collectors.toList());
            log.debug("Количество отзывов ограничено {}", count);
            log.trace("Итоговый перечень отзывов: {}", reviews.stream().map(Review::toString));
        }
        return reviews;
    }

    public Review add(Review filmReview) {
        log.debug("Добавление нового отзыва: {}", filmReview);
        performChecks(filmReview);
        Review review = reviewStorage.add(filmReview);
        log.debug("Добавлен отзыв с id = {}", review.getReviewId());
        log.trace("Итоговый отзыв: {}", review);
        return review;
    }

    public Review update(Review filmReview) {
        log.debug("Обновление отзыва: {}", filmReview);
        performChecks(filmReview);
        Review review = reviewStorage.update(filmReview);
        log.debug("Отзыв обновлён");
        log.trace("Итоговый отзыв: {}", review);
        return review;
    }

    public void delete(Integer id) {
        log.debug("Удаление отзыва с id = {}", id);
        checkIfReviewExists(id);
        reviewStorage.delete(id);
        log.debug("Удалён отзыв с id = {}", id);
    }

    public void addLikeToFilmReview(Integer reviewId, Integer userId) {
        log.debug("Добавление лайка отзыву с id = {} от пользователя с id = {}", reviewId, userId);
        changeUserReviewReaction(reviewId, userId, true);
        log.debug("Лайк добавлен");
    }

    public void addDislikeToFilmReview (Integer reviewId, Integer userId) {
        log.debug("Добавление дизлайка отзыву с id = {} от пользователя с id = {}", reviewId, userId);
        changeUserReviewReaction(reviewId, userId, false);
        log.debug("Дизлайк добавлен");
    }

    public void deleteLikeFromFilmReview (Integer reviewId, Integer userId) {
        log.debug("Удаление лайка к отзыву с id = {} от пользователя с id = {}", reviewId, userId);
        clearUserReviewReaction(reviewId, userId, true);
        log.debug("Выполнено");
    }

    public void deleteDislikeFromFilmReview (Integer reviewId, Integer userId) {
        log.debug("Удаление дизлайка к отзыву с id = {} от пользователя с id = {}", reviewId, userId);
        clearUserReviewReaction(reviewId, userId, false);
        log.debug("Выполнено");
    }

    private void changeUserReviewReaction(Integer reviewId, Integer userId, boolean isPositive) {
        User user = userService.getUserById(userId);
        checkIfReviewExists(reviewId);
        if (isPositive) {
            reviewLikeStorage.addLikeToFilmReview(reviewId, user.getId());
        } else {
            reviewLikeStorage.addDislikeToFilmReview(reviewId, user.getId());
        }
    }

    private void clearUserReviewReaction(Integer reviewId, Integer userId, boolean isLike) {
        User user = userService.getUserById(userId);
        checkIfReviewExists(reviewId);
        if (isLike) {
            reviewLikeStorage.deleteLikeFromFilmReview(reviewId, user.getId());
        } else {
            reviewLikeStorage.deleteDislikeFromFilmReview(reviewId, user.getId());
        }
    }

    private void performChecks(Review filmReview) {
        log.debug("Запуск проверок отзыва");
        if (filmReview.getContent() == null || filmReview.getContent().isBlank()) {
            log.warn("Ошибка проверки значений текста отзыва");
            throw new ReviewValidationException("Текст отзыва заполнен некорректно");
        }
        if (filmReview.getUserId() == null || userService.getUserById(filmReview.getUserId()) == null) {
            log.warn("Не обнаружен пользователь с id = {}", filmReview.getUserId());
            throw new ReviewValidationException("Данные о пользователе заполнены некорректно");
        }
        if (filmReview.getFilmId() == null || filmService.getFilmById(filmReview.getFilmId()) == null) {
            log.warn("Не обнаружен фильм с id = {}", filmReview.getFilmId());
            throw new ReviewValidationException("Данные о фильме заполнены некорректно");
        }
        if (filmReview.getIsPositive() == null) {
            log.warn("Ошибка проверки значения типа отзыва");
            throw new ReviewValidationException("Данные о типе отзыва заполнены некорректно");
        }
        filmReview.setUseful(0);
        log.debug("Проверки пройдены успешно");
    }

    private void checkIfReviewExists (Integer reviewId) {
        if (!reviewStorage.isExists(reviewId)) {
            log.warn("Отзыв с id = {} не обнаружен", reviewId);
            throw new NotFoundException("Отзыв не обнаружен");
        }
    }
}
