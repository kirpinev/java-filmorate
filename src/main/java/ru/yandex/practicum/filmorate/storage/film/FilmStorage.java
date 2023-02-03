package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.constants.SearchBy;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.SortBy;

import java.util.Collection;
import java.util.Set;

public interface FilmStorage {

    Film createFilm(Film film);

    Film getFilmById(Integer id);

    Collection<Film> getAllFilms();

    Film updateFilm(Film film);

    Collection<Film> getPopularFilms(Integer count, Integer genreId, Integer year);

    Collection<Film> getCommonFilms(Integer userId, Integer friendId);

    Collection<Film> getDirectorFilms(Integer directorId, SortBy sortBy);

    Collection<Film> getUserRecommendations(Integer userId);

    boolean deleteFilmById(Integer id);

    Set<Film> search(String query, Set<SearchBy> searchFields);

}
