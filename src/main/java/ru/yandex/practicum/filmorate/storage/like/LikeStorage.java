package ru.yandex.practicum.filmorate.storage.like;

import ru.yandex.practicum.filmorate.model.Like;

import java.util.Collection;

public interface LikeStorage {

    void addLikeToFilm(Integer filmId, Integer userId);

    void deleteLikeFromFilm(Integer filmId, Integer userId);

    Collection<Like> getAllLikes();

    Collection<Like> getTopLikes(Integer count);
}
