package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Review {

    Integer id;
    String content;
    Boolean isPositive;
    Integer userId;
    Integer filmId;
    Integer useful;

}
