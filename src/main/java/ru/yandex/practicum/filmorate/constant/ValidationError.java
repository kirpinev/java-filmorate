package ru.yandex.practicum.filmorate.constant;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ValidationError {
    public static final String INVALID_DATE = "Указана неверная дата.";
    public static final String FILM_NOT_FOUND = "Такого фильма нет.";
    public static final String USER_NOT_FOUND = "Такого пользователя нет.";
}
