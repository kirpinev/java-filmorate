package ru.yandex.practicum.filmorate.storage.genre;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Collection;

@Component
@Qualifier("GenreDbStorage")
public class GenreDbStorage implements GenreStorage {

    private final JdbcTemplate jdbcTemplate;

    public GenreDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Genre getGenreById(Integer genreId) {
        final String sql = "select * from genres where id = ?";

        try {
            return jdbcTemplate.queryForObject(sql, new GenreMapper(), genreId);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public Collection<Genre> getAllGenres() {
        final String sql = "select * from genres";

        return jdbcTemplate.query(sql, new GenreMapper());
    }
}
