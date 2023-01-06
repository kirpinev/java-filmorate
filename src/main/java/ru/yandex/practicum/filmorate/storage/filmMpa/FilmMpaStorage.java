package ru.yandex.practicum.filmorate.storage.filmMpa;

import ru.yandex.practicum.filmorate.model.FilmMpa;

import java.util.Collection;

public interface FilmMpaStorage {

    void addFilmMpa(Integer filmId, Integer mpaId);

    FilmMpa getFilmMpaById(Integer filmId);

    void deleteFilmMpaById(Integer filmId);

    Collection<FilmMpa> getAllFilmMpa();
}
