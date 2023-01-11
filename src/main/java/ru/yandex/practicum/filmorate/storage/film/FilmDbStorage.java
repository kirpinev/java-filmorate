package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.storage.filmGenre.FilmGenreStorage;
import ru.yandex.practicum.filmorate.storage.filmMpa.FilmMpaStorage;
import ru.yandex.practicum.filmorate.storage.mpa.MpaStorage;

import java.sql.PreparedStatement;
import java.util.*;
import java.util.stream.Collectors;

@Component
@Qualifier("FilmDbStorage")
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;

    private final FilmMpaStorage filmMpaStorage;

    private final MpaStorage mpaStorage;

    private final FilmGenreStorage filmGenreStorage;

    public FilmDbStorage(JdbcTemplate jdbcTemplate, FilmMpaStorage filmMpaStorage, MpaStorage mpaStorage,
                         FilmGenreStorage filmGenreStorage) {
        this.jdbcTemplate = jdbcTemplate;
        this.filmMpaStorage = filmMpaStorage;
        this.mpaStorage = mpaStorage;
        this.filmGenreStorage = filmGenreStorage;
    }

    @Override
    public Film createFilm(Film film) {
        final String sql = "insert into films (name, release_date, description, duration, rate) " +
                "values (?, ?, ?, ?, ?)";

        KeyHolder generatedKeyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement preparedStatement = connection.prepareStatement(sql,
                    new String[]{"id"});
            preparedStatement.setString(1, film.getName());
            preparedStatement.setObject(2, film.getReleaseDate());
            preparedStatement.setString(3, film.getDescription());
            preparedStatement.setInt(4, film.getDuration());
            preparedStatement.setInt(5, film.getRate());

            return preparedStatement;
        }, generatedKeyHolder);

        int filmId = Objects.requireNonNull(generatedKeyHolder.getKey()).intValue();

        film.setId(filmId);

        return addMpaAndGenres(film);
    }

    private Film addMpaAndGenres(Film film) {
        int filmId = film.getId();
        int mpaId = film.getMpa().getId();
        Collection<Genre> genres = new LinkedHashSet<>(film.getGenres());

        filmMpaStorage.addFilmMpa(filmId, mpaId);
        genres.forEach(genre -> filmGenreStorage.addFilmGenre(filmId, genre.getId()));

        Mpa filmMpa = mpaStorage.getMpaById(mpaId);
        Collection<Genre> filmGenres = filmGenreStorage.getAllFilmGenresById(filmId);

        return film.toBuilder().id(filmId).mpa(filmMpa).genres(filmGenres).build();
    }

    private Film updateMpaAndGenres(Film film) {
        int filmId = film.getId();
        Mpa filmMpa = filmMpaStorage.getFilmMpaById(filmId);
        Collection<Genre> filmGenres = filmGenreStorage.getAllFilmGenresById(filmId);

        return film.toBuilder().mpa(filmMpa).genres(filmGenres).build();
    }

    @Override
    public Film getFilmById(Integer filmId) {
        final String sql = "select * from films where id = ?";
        List<Film> films = jdbcTemplate.query(sql, new FilmMapper(), filmId);

        return !films.isEmpty() ? updateMpaAndGenres(films.get(0)) : null;
    }

    @Override
    public Collection<Film> getAllFilms() {
        final String sql = "select * from films";
        Collection<Film> films = jdbcTemplate.query(sql, new FilmMapper());

        return films.stream().map(this::updateMpaAndGenres).collect(Collectors.toList());
    }

    @Override
    public Film updateFilm(Film film) {
        final String sql = "update films set name = ?, release_date = ?, description = ?, duration = ?, " +
                "rate = ? where id = ?";

        jdbcTemplate.update(sql, film.getName(), film.getReleaseDate(), film.getDescription(),
                film.getDuration(), film.getRate(), film.getId());

        filmMpaStorage.deleteFilmMpaById(film.getId());
        filmGenreStorage.deleteAllFilmGenresById(film.getId());

        return addMpaAndGenres(film);
    }

    @Override
    public Collection<Film> getPopularFilms(Integer count) {
        final String sql = "SELECT f.* FROM films f LEFT JOIN likes l ON f.ID = l.film_id GROUP BY f.name " +
                "ORDER BY count(l.film_id) DESC limit ?";

        Collection<Film> films = jdbcTemplate.query(sql, new FilmMapper(), count);

        return films.stream().map(this::updateMpaAndGenres).collect(Collectors.toList());
    }
}
