package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.validation.ReleaseDate;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class Film {
    public static final int FILM_DESCRIPTION_MAX_LENGTH = 200;
    private int id;
    @NotBlank
    private String name;
    @Size(max = FILM_DESCRIPTION_MAX_LENGTH)
    private String description;
    @ReleaseDate
    private LocalDate releaseDate;
    @Positive
    private int duration;
    private Mpa mpa;
    private int rate;
    private Collection<Genre> genres = new ArrayList<>();
    private Collection<Director> directors = new ArrayList<>();
}