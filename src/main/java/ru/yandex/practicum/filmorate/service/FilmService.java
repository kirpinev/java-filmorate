package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class FilmService {

    private final FilmStorage filmStorage;

    public FilmService(FilmStorage inMemoryFilmStorage) {
        this.filmStorage = inMemoryFilmStorage;
    }

    public Film createFilm(Film film) {
        return filmStorage.create(film);
    }

    public Film updateFilm(Film film) {
        return filmStorage.update(film);
    }

    public Map<Integer, Film> getFilms() {
        return filmStorage.getAll();
    }

    public void addLikeToFilm(Film film, Integer userId) {
        film.getLikes().add(userId);
        filmStorage.update(film);
    }

    public void deleteLikeFromFilm(Film film, Integer userId) {
        film.getLikes().remove(userId);
    }

    public List<Film> getPopularFilms(Integer count) {
        return filmStorage
                .getAll()
                .values()
                .stream()
                .sorted((Film film1, Film film2) -> film2.getLikes().size() - film1.getLikes().size())
                .limit(count)
                .collect(Collectors.toList());
    }

    public Film getFilmById(Integer id) {
        return filmStorage.getById(id);
    }
}
