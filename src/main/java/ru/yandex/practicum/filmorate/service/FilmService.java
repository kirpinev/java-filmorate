package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.constants.SearchBy;
import ru.yandex.practicum.filmorate.constants.SortBy;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.validation.FilmValidator;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
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

    public void deleteFilmById(Integer id) {
        filmStorage.deleteFilmById(id);
    }

    public Collection<Film> getCommonFilms(Integer userId, Integer friendId) {
        return filmStorage.getCommonFilms(userId, friendId);
    }

    private void checkFilmIsNotFound(Film film, Integer id) {
        if (FilmValidator.isFilmNull(film)) {
            throw new NotFoundException(String.format(NOT_FOUND_FILM, id));
        }
    }

    public Set<Film> search(String query, SearchBy[] searchBy) {
        log.debug("Запрошен поиск сочетания \"{}\" в полях: {}", query, searchBy);
        Set<SearchBy> searchFields = Arrays.stream(searchBy)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        log.trace("Отфильтрованный список полей: {}", searchFields);
        Set<Film> foundFilms = filmStorage.search(query, searchFields);
        log.debug("Найдены фильмы, подходящие под условия: {}", foundFilms);
        return foundFilms;
    }

}
