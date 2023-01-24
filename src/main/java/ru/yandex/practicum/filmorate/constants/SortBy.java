package ru.yandex.practicum.filmorate.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum SortBy {
    year("year"),
    likes("likes");

    public final String label;
}
