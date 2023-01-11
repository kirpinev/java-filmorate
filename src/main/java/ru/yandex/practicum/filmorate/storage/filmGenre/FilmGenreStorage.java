package ru.yandex.practicum.filmorate.storage.filmGenre;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Collection;

public interface FilmGenreStorage {
    void addFilmGenre(Integer filmId, Integer genreId);

    Collection<Genre> getAllFilmGenresById(Integer filmId);

    void deleteAllFilmGenresById(Integer filmId);
}
