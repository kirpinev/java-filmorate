package ru.yandex.practicum.filmorate.storage.filmReviewLike;

public interface ReviewLikeStorage {

    void addLikeToFilmReview(Integer reviewId, Integer userId);

    void deleteLikeFromFilmReview(Integer reviewId, Integer userId);

}
