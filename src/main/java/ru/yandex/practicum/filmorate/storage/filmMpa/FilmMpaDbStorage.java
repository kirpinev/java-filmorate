package ru.yandex.practicum.filmorate.storage.filmMpa;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.FilmMpa;

import java.util.Collection;

@Component
@Qualifier("FilmMpaDbStorage")
public class FilmMpaDbStorage implements FilmMpaStorage {

    private final JdbcTemplate jdbcTemplate;

    public FilmMpaDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void addFilmMpa(Integer filmId, Integer mpaId) {
        jdbcTemplate.update(FilmMpaSqlQueries.ADD_FILM_MPA, filmId, mpaId);
    }

    @Override
    public FilmMpa getFilmMpaById(Integer filmId) {
        return jdbcTemplate.queryForObject(FilmMpaSqlQueries.GET_FILM_MPA, new FilmMapMapper(), filmId);
    }

    @Override
    public void deleteFilmMpaById(Integer filmId) {
        jdbcTemplate.update(FilmMpaSqlQueries.DELETE_FILM_MPA, filmId);
    }

    @Override
    public Collection<FilmMpa> getAllFilmMpa() {
        return jdbcTemplate.query(FilmMpaSqlQueries.GET_ALL_FILM_MPA, new FilmMapMapper());
    }
}
