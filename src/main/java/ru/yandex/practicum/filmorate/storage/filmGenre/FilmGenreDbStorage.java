package ru.yandex.practicum.filmorate.storage.filmGenre;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.FilmGenre;

import java.util.Collection;

@Component
@Qualifier("FilmGenreDbStorage")
public class FilmGenreDbStorage implements FilmGenreStorage {

    private final JdbcTemplate jdbcTemplate;

    public FilmGenreDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void addFilmGenre(Integer filmId, Integer genreId) {
        jdbcTemplate.update(FilmGenreSqlQueries.ADD_FILM_GENRE, filmId, genreId);
    }

    @Override
    public Collection<FilmGenre> getAllFilmGenresById(Integer filmId) {
        return jdbcTemplate.query(FilmGenreSqlQueries.GET_ALL_FILM_GENRES, new FilmGenreMapper(), filmId);
    }

    @Override
    public void deleteAllFilmGenresById(Integer filmId) {
        jdbcTemplate.update(FilmGenreSqlQueries.DELETE_ALL_FILM_GENRES, filmId);
    }
}
