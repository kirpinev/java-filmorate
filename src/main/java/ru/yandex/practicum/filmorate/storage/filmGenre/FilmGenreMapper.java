package ru.yandex.practicum.filmorate.storage.filmGenre;

import org.springframework.jdbc.core.RowMapper;
import ru.yandex.practicum.filmorate.model.FilmGenre;

import java.sql.ResultSet;
import java.sql.SQLException;

public class FilmGenreMapper implements RowMapper<FilmGenre> {

    @Override
    public FilmGenre mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new FilmGenre()
                .toBuilder()
                .id(rs.getInt("id"))
                .filmId(rs.getInt("film_id"))
                .genreId(rs.getInt("genre_id"))
                .build();
    }
}