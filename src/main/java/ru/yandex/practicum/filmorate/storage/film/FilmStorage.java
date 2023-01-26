package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.constants.SortBy;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;

public interface FilmStorage {

    Film createFilm(Film film);

    Film getFilmById(Integer id);

    Collection<Film> getAllFilms();

    Film updateFilm(Film film);

    Collection<Film> getPopularFilms(Integer count, Integer genreId, Integer year);

    Collection<Film> getCommonFilms(Integer userId, Integer friendId);

    Collection<Film> getDirectorFilms(Integer directorId, SortBy sortBy);

    Collection<Film> getUserRecommendations(Integer userId);

    void deleteFilmById(Integer id);
}
