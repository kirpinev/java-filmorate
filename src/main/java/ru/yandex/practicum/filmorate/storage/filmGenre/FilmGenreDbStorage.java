package ru.yandex.practicum.filmorate.storage.filmGenre;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.genre.GenreMapper;

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
        final String sql = "insert into film_genres (film_id, genre_id) values (?, ?)";

        jdbcTemplate.update(sql, filmId, genreId);
    }

    @Override
    public Collection<Genre> getAllFilmGenresById(Integer filmId) {
        final String sql = "select g.id as id, name from film_genres fg left join genres g on " +
                "fg.genre_id = g.id where film_id = ?";

        return jdbcTemplate.query(sql, new GenreMapper(), filmId);
    }

    @Override
    public void deleteAllFilmGenresById(Integer filmId) {
        final String sql = "delete from film_genres where film_id = ?";

        jdbcTemplate.update(sql, filmId);
    }
}
