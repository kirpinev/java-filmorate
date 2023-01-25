package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ReviewValidationException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.filmReview.ReviewStorage;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ReviewService {

    ReviewStorage reviewStorage;
    UserService userService;
    FilmService filmService;

    @Autowired
    public ReviewService(
            @Qualifier("ReviewDbStorage") ReviewStorage reviewStorage,
            UserService userService,
            FilmService filmService
    ) {
        this.reviewStorage = reviewStorage;
        this.userService = userService;
        this.filmService = filmService;
    }

    public Review getById(Integer id) {
        log.debug("Запрошен отзыв с id = {}", id);
        if (!reviewStorage.isExists(id)) {
            log.warn("Отзыв не обнаружен");
            throw new NotFoundException("Отзыв не обнаружен");
        }
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
        log.debug("Добавление нового отзыва на фильм: {}", filmReview);
        performChecks(filmReview);
        return reviewStorage.add(filmReview);
    }

    public Review update(Review filmReview) {
        log.debug("Обновление фильма: {}", filmReview);
        if (filmService.getFilmById(filmReview.getFilmId()) == null) {
            log.warn("Не обнаружен фильм с id = {}", filmReview.getFilmId());
            throw new NotFoundException("Фильм указан некорректно");
        }
        performChecks(filmReview);
        return reviewStorage.update(filmReview);
    }

    public Review delete(Integer id) {
        log.debug("Удаление отзыва с id = {}", id);
        if (!reviewStorage.isExists(id)) {
            log.warn("Отзыв не обнаружен");
            throw new NotFoundException("ID отзыва указан некорректно");
        }
        Review review = reviewStorage.delete(id);
        log.trace("Удалён отзыв: {}", review);
        return review;
    }

    private void performChecks(Review filmReview) {
        log.debug("Запуск проверок отзыва");
        if (filmReview.getContent() == null || filmReview.getContent().isBlank()) {
            log.warn("Ошибка проверки значений текста отзыва");
            throw new ReviewValidationException("Текст отзыва заполнен некорректно");
        }
        if (userService.getUserById(filmReview.getUserId()) == null) {
            log.warn("Не обнаружен пользователь с id = {}", filmReview.getUserId());
            throw new NotFoundException("Данные о пользователе заполнены некорректно");
        }
        if (filmService.getFilmById(filmReview.getFilmId()) == null) {
            log.warn("Не обнаружен фильм с id = {}", filmReview.getFilmId());
            throw new NotFoundException("Данные о фильме заполнены некорректно");
        }
        log.debug("Проверки пройдены успешно");
    }

    public void addUserLike(Integer reviewId, Integer userId) {
        log.debug("Добавление лайка отзыву с id = {} от пользователя с id = {}", reviewId, userId);
        Review review = getById(reviewId);
        if (userService.getUserById(userId) == null) {
            log.warn("Пользователь не обнаружен");
            throw new NotFoundException("Данные о пользователе заполнены некорректно");
        }
    }
}
