package ru.yandex.practicum.filmorate.storage.director;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ResponseStatus;
import ru.yandex.practicum.filmorate.constants.DirectorErrorMessages;
import ru.yandex.practicum.filmorate.exception.ConflictException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Director;

import java.sql.PreparedStatement;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class DirectorDbStorage implements DirectorStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    @ResponseStatus(HttpStatus.CREATED)
    public Director createDirector(Director director) {
        String sqlQuery = "insert into directors(name) values (?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[] {"director_id"});
            stmt.setString(1, director.getName());
            return stmt;
        }, keyHolder);

        if (Objects.isNull(keyHolder.getKey())) {
            throw new ConflictException(DirectorErrorMessages.conflict);
        }

        director.setId(keyHolder.getKey().intValue());
        return director;
    }

    @Override
    public Director updateDirector(Director director) {
        String sqlQuery = "update DIRECTORS set " +
            "NAME = ? " +
            "where DIRECTOR_ID = ?";

        int status = jdbcTemplate.update(
            sqlQuery,
            director.getName(),
            director.getId()
        );

        if (status == 0) {
            throw new NotFoundException(String.format(DirectorErrorMessages.notFound, director.getId()));
        }

        return director;
    }

    @Override
    public Optional<Director> getDirectorById(Integer id) {
        String sqlQuery = "SELECT * " +
            "FROM DIRECTORS  " +
            "WHERE DIRECTOR_ID = ?;";

        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(sqlQuery, new DirectorMapper(), id));
        }
        catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public Collection<Director> getAllDirectors() {
        String sqlQuery = "SELECT * " +
            "FROM DIRECTORS";

        return jdbcTemplate.query(sqlQuery, new DirectorMapper());
    }

    @Override
    public void deleteDirector(Integer id) {
        String sqlQuery = "delete from DIRECTORS " +
            "where DIRECTOR_ID = ?";

        int status = jdbcTemplate.update(sqlQuery, id);

        if (status == 0) {
            throw new NotFoundException(String.format(DirectorErrorMessages.notFound, id));
        }
    }
}
