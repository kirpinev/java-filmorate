package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.validation.FilmValidator;

import javax.validation.Valid;
import java.util.Collection;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/films")
public class FilmController {

    private static final String NOT_FOUND_FILM = "фильма с id %s нет";
    private static final String NOT_FOUND_USER = "пользователя с userId %s нет";
    private final FilmService filmService;

    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @PostMapping
    public Film createFilm(@Valid @RequestBody Film film) {
        log.debug("Создается фильм: {}", film);

        return filmService.createFilm(film);
    }

    @GetMapping
    public Collection<Film> getFilms() {
        return filmService.getFilms().values();
    }


    @GetMapping("/{id}")
    public Film getFilmById(@PathVariable("id") Integer id) {
        Film film = filmService.getFilmById(id);

        checkFilmIsNotNull(film, id);

        return filmService.getFilmById(id);
    }

    @GetMapping("/popular")
    public List<Film> getPopularFilms(@RequestParam(name = "count",
            defaultValue = "10", required = false) Integer count) {
        return filmService.getPopularFilms(count);
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLikeToFilm(@PathVariable("id") Integer id, @PathVariable("userId") Integer userId) {
        Film film = filmService.getFilmById(id);

        checkFilmIsNotNull(film, id);

        filmService.addLikeToFilm(film, userId);
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) {
        log.debug("Обновляется фильм: {}", film);

        checkFilmIsNotNull(film, film.getId());

        return filmService.updateFilm(film);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void deleteLikeFromFilm(@PathVariable("id") Integer id, @PathVariable("userId") Integer userId) {
        Film film = filmService.getFilmById(id);

        checkFilmIsNotNull(film, id);
        checkUserIdIsValid(userId);

        filmService.deleteLikeFromFilm(film, userId);
    }

    private void checkFilmIsNotNull(Film film, Integer id) {
        if (FilmValidator.isFilmNotFound(filmService.getFilms(), film)) {
            throw new NotFoundException(String.format(NOT_FOUND_FILM, id));
        }
    }

    private void checkUserIdIsValid(Integer userId) {
        if (userId <= 0) {
            throw new NotFoundException(String.format(NOT_FOUND_USER, userId));
        }
    }
}
