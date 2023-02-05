package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.storage.like.LikeStorage;

@Service
@RequiredArgsConstructor
public class LikeService {

    private final LikeStorage likeStorage;

    public void addLikeToFilm(Integer filmId, Integer userId) {
        likeStorage.addLikeToFilm(filmId, userId);
    }

    public void deleteLikeFromFilm(Integer filmId, Integer userId) {
        likeStorage.deleteLikeFromFilm(filmId, userId);
    }
}
