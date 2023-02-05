package ru.yandex.practicum.filmorate.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum SearchBy {

    director("directors.name"),
    title("films.name");

    public final String sqlTableAndFieldName;

}
