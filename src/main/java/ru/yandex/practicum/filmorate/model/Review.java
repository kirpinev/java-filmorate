package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@Builder
public class Review {

    Integer reviewId;
    @NotNull String content;
    Boolean isPositive;
    Integer userId;
    Integer filmId;
    Integer useful;

}
