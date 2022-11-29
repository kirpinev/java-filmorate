package ru.yandex.practicum.filmorate.validation;

import ru.yandex.practicum.filmorate.annotation.ReleaseDate;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;
import java.util.Calendar;

public class ReleaseDateValidator implements ConstraintValidator<ReleaseDate, LocalDate> {

    @Override
    public boolean isValid(LocalDate date, ConstraintValidatorContext constraintValidatorContext) {
        LocalDate firstFilmDate = LocalDate.of(1895, Calendar.DECEMBER, 28);

        return firstFilmDate.isBefore(date);
    }
}
