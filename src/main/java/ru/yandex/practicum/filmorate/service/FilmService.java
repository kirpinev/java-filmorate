package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.constants.SortBy;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.validation.FilmValidator;

import java.util.Collection;

@Service
@RequiredArgsConstructor
public class FilmService {

    private static final String NOT_FOUND_FILM = "фильма с id %s нет";
    private final FilmStorage filmStorage;
    private final UserService userService;
    private final LikeService likeService;

    public Film createFilm(Film film) {
        return filmStorage.createFilm(film);
    }

    public Film updateFilm(Film film) {
        Film filmFromBD = filmStorage.getFilmById(film.getId());

        checkFilmIsNotFound(filmFromBD, film.getId());

        return filmStorage.updateFilm(film);
    }

    public Collection<Film> getFilms() {
        return filmStorage.getAllFilms();
    }

    public Collection<Film> getDirectorFilms(Integer directorId, SortBy sortBy) {
        return filmStorage.getDirectorFilms(directorId, sortBy);
    }

    public Collection<Film> getRecommendations(Integer userId) {
        return filmStorage.getUserRecommendations(userId);
    }

    public void addLikeToFilm(Integer filmId, Integer userId) {
        likeService.addLikeToFilm(filmId, userId);
    }

    public void deleteLikeFromFilm(Integer filmId, Integer userId) {
        User user = userService.getUserById(userId);

        likeService.deleteLikeFromFilm(filmId, user.getId());
    }

    public Collection<Film> getPopularFilms(Integer count, Integer genreId, Integer year) {
        return filmStorage.getPopularFilms(count, genreId, year);
    }

    public Film getFilmById(Integer id) {
        Film film = filmStorage.getFilmById(id);

        checkFilmIsNotFound(film, id);

        return film;
    }

    private void checkFilmIsNotFound(Film film, Integer id) {
        if (FilmValidator.isFilmNull(film)) {
            throw new NotFoundException(String.format(NOT_FOUND_FILM, id));
        }
    }
}
