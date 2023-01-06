package ru.yandex.practicum.filmorate.storage.filmGenre;

import ru.yandex.practicum.filmorate.model.FilmGenre;

import java.util.Collection;

public interface FilmGenreStorage {
    void addFilmGenre(Integer filmId, Integer genreId);

    Collection<FilmGenre> getAllFilmGenresById(Integer filmId);

    void deleteAllFilmGenresById(Integer filmId);
}
