package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.storage.filmGenre.FilmGenreStorage;
import ru.yandex.practicum.filmorate.storage.filmMpa.FilmMpaStorage;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;
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

    private final GenreStorage genreStorage;


    public FilmDbStorage(JdbcTemplate jdbcTemplate, FilmMpaStorage filmMpaStorage, MpaStorage mpaStorage,
                         FilmGenreStorage filmGenreStorage, GenreStorage genreStorage) {
        this.jdbcTemplate = jdbcTemplate;
        this.filmMpaStorage = filmMpaStorage;
        this.mpaStorage = mpaStorage;
        this.filmGenreStorage = filmGenreStorage;
        this.genreStorage = genreStorage;
    }

    @Override
    public Film createFilm(Film film) {
        KeyHolder generatedKeyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement preparedStatement = connection.prepareStatement(FilmSqlQueries.CREATE_FILM,
                    new String[]{"id"});
            preparedStatement.setString(1, film.getName());
            preparedStatement.setObject(2, film.getReleaseDate());
            preparedStatement.setString(3, film.getDescription());
            preparedStatement.setInt(4, film.getDuration());
            preparedStatement.setInt(5, film.getRate());

            return preparedStatement;
        }, generatedKeyHolder);

        int filmId = Objects.requireNonNull(generatedKeyHolder.getKey()).intValue();

        return addMpaAndGenres(film.toBuilder().id(filmId).build());
    }

    private Film addMpaAndGenres(Film film) {
        int mpaId = film.getMpa().getId();
        Collection<Genre> genres = new LinkedHashSet<>(film.getGenres());

        filmMpaStorage.addFilmMpa(film.getId(), mpaId);
        Mpa mpa = mpaStorage.getMpaById(mpaId);

        genres.forEach(genre -> filmGenreStorage.addFilmGenre(film.getId(), genre.getId()));

        Collection<Genre> genresWithNames = genres.stream()
                .map(genre -> genreStorage.getGenreById(genre.getId())).collect(Collectors.toList());

        return film.toBuilder().id(film.getId()).mpa(mpa).genres(genresWithNames).build();
    }

    private Film updateMpaAndGenres(Film film) {
        FilmMpa filmMpa = filmMpaStorage.getFilmMpaById(film.getId());
        Mpa mpa = mpaStorage.getMpaById(filmMpa.getMpaId());
        Collection<FilmGenre> filmGenres = filmGenreStorage.getAllFilmGenresById(film.getId());
        Collection<Genre> genres = filmGenres.stream()
                .map(filmGenre -> genreStorage.getGenreById(filmGenre.getGenreId()))
                .collect(Collectors.toList());

        return film.toBuilder().mpa(mpa).genres(genres).build();
    }

    @Override
    public Film getFilmById(Integer filmId) {
        List<Film> films = jdbcTemplate.query(FilmSqlQueries.FIND_FILM_BY_ID, new FilmMapper(), filmId);

        return !films.isEmpty() ? updateMpaAndGenres(films.get(0)) : null;
    }

    @Override
    public Collection<Film> getAllFilms() {
        Collection<Film> films = jdbcTemplate.query(FilmSqlQueries.FIND_ALL_FILMS, new FilmMapper());

        return films.stream().map(this::updateMpaAndGenres).collect(Collectors.toList());
    }

    @Override
    public Film updateFilm(Film film) {
        jdbcTemplate.update(FilmSqlQueries.UPDATE_FILM, film.getName(), film.getReleaseDate(), film.getDescription(),
                film.getDuration(), film.getRate(), film.getId());

        filmMpaStorage.deleteFilmMpaById(film.getId());
        filmGenreStorage.deleteAllFilmGenresById(film.getId());

        return addMpaAndGenres(film);
    }

    @Override
    public void deleteFilmById(Integer filmId) {
        jdbcTemplate.update(FilmSqlQueries.DELETE_FILM_BY_ID, filmId);
    }
}
