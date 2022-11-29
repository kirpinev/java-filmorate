package ru.yandex.practicum.filmorate.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Set;

class FilmTest {

    private final Film film = Film.builder()
            .name("nisi eiusmod")
            .description("adipisicing")
            .releaseDate(LocalDate.of(1967, Calendar.MARCH, 25))
            .duration(100)
            .build();
    private final ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
    private final Validator validator = validatorFactory.getValidator();

    @Test
    void shouldCreateFilm() {
        Set<ConstraintViolation<Film>> violations = validator.validate(film);

        Assertions.assertTrue(violations.isEmpty());
    }

    @Test
    void shouldNotCreateFilmIfNameIsEmpty() {
        String[] names = {"", " ", "  ", null};

        Arrays.stream(names).forEach(name -> {
            Film filmWithIncorrectName = film
                    .toBuilder()
                    .name(name)
                    .build();

            Set<ConstraintViolation<Film>> violations = validator.validate(filmWithIncorrectName);

            Assertions.assertFalse(violations.isEmpty());
        });
    }

    @Test
    void shouldNotCreateFilmIfDescriptionTooLong() {
        Film filmWithIncorrectDescription = film
                .toBuilder()
                .description("Пятеро друзей ( комик-группа «Шарло»), приезжают в город Бризуль. Здесь они хотят" +
                        " разыскать господина Огюста Куглова, который задолжал им деньги, а именно 20 миллионов." +
                        " о Куглов, который за время «своего отсутствия», стал кандидатом Коломбани.")
                .build();

        Set<ConstraintViolation<Film>> violations = validator.validate(filmWithIncorrectDescription);

        Assertions.assertFalse(violations.isEmpty());
        Assertions.assertEquals(1, violations.size());
    }

    @Test
    void shouldNotCreateFilmIfReleaseDateIsWrong() {
        Film filmWithIncorrectReleaseDate = film
                .toBuilder()
                .releaseDate(LocalDate.of(1890, Calendar.MARCH, 25))
                .build();

        Set<ConstraintViolation<Film>> violations = validator.validate(filmWithIncorrectReleaseDate);

        Assertions.assertFalse(violations.isEmpty());
        Assertions.assertEquals(1, violations.size());
    }

    @Test
    void shouldNotCreateFilmIfDurationIsWrong() {
        Film filmWithIncorrectDuration = film
                .toBuilder()
                .duration(-100)
                .build();

        Set<ConstraintViolation<Film>> violations = validator.validate(filmWithIncorrectDuration);

        Assertions.assertFalse(violations.isEmpty());
        Assertions.assertEquals(1, violations.size());
    }
}