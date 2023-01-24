package ru.yandex.practicum.filmorate.constants;

import lombok.experimental.UtilityClass;

@UtilityClass
public class DirectorErrorMessages {
    public final String notFound = "Режиссер с id: '%d' не найден";
    public final String conflict = "Ошибка при создании режиссера";
}
