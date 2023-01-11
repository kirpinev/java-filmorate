package ru.yandex.practicum.filmorate.validation;

import lombok.experimental.UtilityClass;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;

@UtilityClass
public class FilmValidator {

    public static boolean isFilmNotFound(Collection<Film> films, Film film) {
        return Objects.isNull(film) || Objects.isNull(films.stream()
                .collect(Collectors.toMap(Film::getId, f -> f)).get(film.getId()));
    }
}
