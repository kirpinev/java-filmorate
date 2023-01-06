package ru.yandex.practicum.filmorate.storage.filmMpa;

import org.springframework.jdbc.core.RowMapper;
import ru.yandex.practicum.filmorate.model.FilmMpa;

import java.sql.ResultSet;
import java.sql.SQLException;

public class FilmMapMapper implements RowMapper<FilmMpa> {

    @Override
    public FilmMpa mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new FilmMpa()
                .toBuilder()
                .id(rs.getInt("id"))
                .mpaId(rs.getInt("mpa_id"))
                .filmId(rs.getInt("film_id"))
                .build();
    }
}