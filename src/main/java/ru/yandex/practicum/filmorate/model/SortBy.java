package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum SortBy {
    YEAR("year"),
    LIKES("likes");

    public final String label;
}
