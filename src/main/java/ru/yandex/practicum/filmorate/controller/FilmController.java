package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.constants.SearchBy;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.SortBy;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import java.util.Collection;
import java.util.Set;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/films")
public class FilmController {

    private final FilmService filmService;

    @PostMapping
    public Film createFilm(@Valid @RequestBody Film film) {
        log.debug("Создается фильм: {}", film);

        return filmService.createFilm(film);
    }

    @GetMapping
    public Collection<Film> getFilms() {
        return filmService.getFilms();
    }


    @GetMapping("/{id}")
    public Film getFilmById(@PathVariable("id") Integer id) {
        return filmService.getFilmById(id);
    }

    @GetMapping("/popular")
    public Collection<Film> getPopularFilms(
            @RequestParam(name = "count",
                    defaultValue = "10", required = false) Integer count,
            @RequestParam(name = "genreId", required = false) Integer genreId,
            @RequestParam(name = "year", required = false) Integer year
    ) {
        return filmService.getPopularFilms(count, genreId, year);
    }

    @GetMapping("/common")
    public Collection<Film> getCommonFilms(
            @RequestParam(name = "userId") Integer userId,
            @RequestParam(name = "friendId") Integer friendId
    ) {
        return filmService.getCommonFilms(userId, friendId);
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLikeToFilm(@PathVariable("id") Integer id, @PathVariable("userId") Integer userId) {
        filmService.addLikeToFilm(id, userId);
    }


    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) {
        log.debug("Обновляется фильм: {}", film);

        return filmService.updateFilm(film);
    }


    @DeleteMapping("/{id}/like/{userId}")
    public void deleteLikeFromFilm(@PathVariable("id") Integer id, @PathVariable("userId") Integer userId) {
        filmService.deleteLikeFromFilm(id, userId);
    }

    @GetMapping("/director/{directorId}")
    public Collection<Film> getSortedFilms(
            @PathVariable("directorId") Integer directorId, @RequestParam SortBy sortBy
    ) {
        return filmService.getDirectorFilms(directorId, sortBy);
    }

    @DeleteMapping("/{filmId}")
    public void deleteFilmById(@PathVariable("filmId") Integer id) {
        filmService.deleteFilmById(id);
    }

    @GetMapping("/search")
    public Set<Film> search(@RequestParam(name = "query") String query, @RequestParam(name = "by") SearchBy[] by) {
        return filmService.search(query, by);
    }
}
