package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import javax.validation.ValidationException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

@RestController
@Slf4j
@RequestMapping("/films")
public class FilmController {

    private int id = 1;
    private final HashMap<Integer, Film> films = new HashMap<>();

    @GetMapping
    public List<Film> getFilms() {
        return new ArrayList<>(films.values());
    }

    @PostMapping
    public Film createFilm(@Valid @RequestBody Film film) {
        LocalDate firstFilmDate = LocalDate.of(1895, 11, 28);

        if (firstFilmDate.isAfter(film.getReleaseDate())) {
            throw new ValidationException("Указана неверная дата!");
        }

        film.setId(id);

        id += 1;

        log.debug("Создается фильм: {}", film);

        films.put(film.getId(), film);

        return film;
    }

    @PutMapping
    public Film updateFilm(@RequestBody Film film) {
        log.debug("Обновляется фильм: {}", film);

        if (Objects.isNull(films.get(film.getId()))) {
            throw new ValidationException("Такого фильма нет");
        }

        films.put(film.getId(), film);

        return film;
    }
}
