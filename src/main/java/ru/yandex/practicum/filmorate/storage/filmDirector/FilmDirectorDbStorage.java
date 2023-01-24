package ru.yandex.practicum.filmorate.storage.filmDirector;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.director.DirectorMapper;

import java.sql.PreparedStatement;
import java.util.*;

@Slf4j
@Component
@AllArgsConstructor
public class FilmDirectorDbStorage implements FilmDirectorStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public void setFilmDirectors(Collection<Director> directors, Integer filmId) {
        if (Objects.isNull(directors) || directors.isEmpty()) {
            return;
        }

        String sqlQuery = "insert into FILM_DIRECTORS(DIRECTOR_ID, FILM_ID) " +
            "values ( ?, ? )";

        try {
            jdbcTemplate.batchUpdate(
                sqlQuery, directors, directors.size(), (PreparedStatement ps, Director director) -> {
                    ps.setLong(1, director.getId());
                    ps.setLong(2, filmId);
                });
        }
        catch (DataIntegrityViolationException ignored) {
        }

    }

    @Override
    public Collection<Director> getFilmDirectors(Integer filmId) {
        String sqlQuery = "SELECT D.* " +
            "FROM FILM_DIRECTORS FD " +
            "JOIN DIRECTORS D ON D.DIRECTOR_ID = FD.DIRECTOR_ID " +
            "WHERE FILM_ID = ?";

        return jdbcTemplate.query(sqlQuery, new DirectorMapper(), filmId);
    }

    @Override
    public Map<Integer, Collection<Director>> getFilmDirectors(Collection<Film> films) {
        Map<Integer, Collection<Director>> directorsByFilmId = new HashMap<>();

        String inSql = String.join(",", Collections.nCopies(films.size(), "?"));
        String sqlQuery = "SELECT FD.FILM_ID, D.* " +
            "FROM FILM_DIRECTORS FD " +
            "JOIN DIRECTORS D on D.DIRECTOR_ID = FD.DIRECTOR_ID " +
            "WHERE FILM_ID IN (%s);";

        DirectorMapper directorMapper = new DirectorMapper();

        jdbcTemplate.query(
            String.format(sqlQuery, inSql),
            (rs, rowNum) -> {
                Integer filmId = rs.getInt("film_id");
                Director director = directorMapper.mapRow(rs, rowNum);
                Collection<Director> directors = directorsByFilmId.getOrDefault(filmId, new ArrayList<>());
                directors.add(director);

                return directorsByFilmId.put(
                    filmId,
                    directors
                );
            },
            films.stream().map(film -> Integer.toString(film.getId())).toArray()
        );

        return directorsByFilmId;
    }

    @Override
    public void deleteFilmDirectors(Integer filmId) {
        String sqlQuery = "DELETE FROM FILM_DIRECTORS " +
            "WHERE FILM_ID = ?";

        jdbcTemplate.update(sqlQuery, filmId);
    }
}
