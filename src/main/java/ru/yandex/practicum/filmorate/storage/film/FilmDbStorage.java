package ru.yandex.practicum.filmorate.storage.film;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.constants.DirectorErrorMessages;
import ru.yandex.practicum.filmorate.constants.SortBy;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.filmDirector.FilmDirectorStorage;
import ru.yandex.practicum.filmorate.storage.filmGenre.FilmGenreStorage;
import ru.yandex.practicum.filmorate.storage.filmMpa.FilmMpaStorage;
import ru.yandex.practicum.filmorate.storage.mpa.MpaStorage;

import java.sql.PreparedStatement;
import java.util.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;
    private final FilmMpaStorage filmMpaStorage;
    private final MpaStorage mpaStorage;
    private final FilmGenreStorage filmGenreStorage;
    private final FilmDirectorStorage filmDirectorStorage;
    private final String filmsSql =
        "select f.*, m.id as mpa_id, m.name as mpa_name from films f left join film_mpas fm on f.id = fm.film_id " +
            "left join mpas m on fm.mpa_id = m.id";


    @Override
    public Film createFilm(Film film) {
        final String sql = "insert into films (name, release_date, description, duration, rate) " +
            "values (?, ?, ?, ?, ?)";

        KeyHolder generatedKeyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement preparedStatement = connection.prepareStatement(
                sql,
                new String[] {"id"}
            );
            preparedStatement.setString(1, film.getName());
            preparedStatement.setObject(2, film.getReleaseDate());
            preparedStatement.setString(3, film.getDescription());
            preparedStatement.setInt(4, film.getDuration());
            preparedStatement.setInt(5, film.getRate());

            return preparedStatement;
        }, generatedKeyHolder);

        int filmId = Objects.requireNonNull(generatedKeyHolder.getKey()).intValue();

        film.setId(filmId);

        return addCredentials(film);
    }

    @Override
    public Film getFilmById(Integer filmId) {
        List<Film> films = jdbcTemplate.query(filmsSql.concat(" where f.id = ?"), new FilmMapper(), filmId);

        if (!films.isEmpty()) {
            Collection<Genre> filmGenres = filmGenreStorage.getAllFilmGenresById(filmId);
            Collection<Director> directors = filmDirectorStorage.getFilmDirectors(filmId);

            return films.get(0).toBuilder().genres(filmGenres).directors(directors).build();
        }

        return null;
    }

    @Override
    public Collection<Film> getAllFilms() {
        Collection<Film> films = jdbcTemplate.query(filmsSql, new FilmMapper());

        return setFilmGenresAndDirectors(films);
    }

    @Override
    public Film updateFilm(Film film) {
        final String sql = "update films set name = ?, release_date = ?, description = ?, duration = ?, " +
            "rate = ? where id = ?";

        jdbcTemplate.update(sql, film.getName(), film.getReleaseDate(), film.getDescription(),
            film.getDuration(), film.getRate(), film.getId()
        );

        filmMpaStorage.deleteFilmMpaById(film.getId());
        filmGenreStorage.deleteAllFilmGenresById(film.getId());
        filmDirectorStorage.deleteFilmDirectors(film.getId());

        return addCredentials(film);
    }

    @Override
    public Collection<Film> getPopularFilms(Integer count) {
        final String sql =
            "select f.*, m.id as mpa_id, m.name as mpa_name from films f left join likes l on f.id = l.film_id " +
                "left join film_mpas fm on f.id = fm.film_id " +
                "left join mpas m on fm.mpa_id = m.id group by f.name, f.id " +
                "order by count(l.film_id) desc limit ?";
        Collection<Film> films = jdbcTemplate.query(sql, new FilmMapper(), count);

        return setFilmGenresAndDirectors(films);
    }

    @Override
    public Collection<Film> getDirectorFilms(Integer directorId, SortBy sortBy) {
        String yearOrderSql = "SELECT F.*, " +
            "       M.ID MPA_ID, " +
            "       M.NAME MPA_NAME " +
            "FROM FILM_DIRECTORS FD " +
            "         JOIN FILMS F on F.ID = FD.FILM_ID " +
            "         JOIN FILM_MPAS FM on F.ID = FM.FILM_ID " +
            "         JOIN MPAS M on FM.MPA_ID = M.ID " +
            "WHERE DIRECTOR_ID = ? " +
            "ORDER BY year(F.RELEASE_DATE) asc";

        String likesOrderSql = "SELECT F.*,  " +
            "       M.ID MPA_ID,  " +
            "       M.NAME MPA_NAME,  " +
            "       (SELECT COUNT(*) FROM LIKES WHERE FD.FILM_ID = LIKES.FILM_ID) as LIKES " +
            "FROM FILM_DIRECTORS FD " +
            "JOIN FILMS F on F.ID = FD.FILM_ID " +
            "JOIN FILM_MPAS FM on F.ID = FM.FILM_ID " +
            "JOIN MPAS M on FM.MPA_ID = M.ID " +
            "WHERE DIRECTOR_ID = ? " +
            "ORDER BY LIKES DESC;";

        Collection<Film> films =
            jdbcTemplate.query(sortBy == SortBy.likes ? likesOrderSql : yearOrderSql, new FilmMapper(), directorId);

        if (films.isEmpty()) {
            throw new NotFoundException(String.format(DirectorErrorMessages.notFound, directorId));
        }

        return setFilmGenresAndDirectors(films);
    }

    private Collection<Film> setFilmGenresAndDirectors(Collection<Film> films) {
        Map<Integer, Collection<Genre>> filmGenresMap = filmGenreStorage.getAllFilmGenres(films);
        Map<Integer, Collection<Director>> filmDirectorsMap = filmDirectorStorage.getFilmDirectors(films);

        return films.stream().peek(film -> {
            Integer filmId = film.getId();

            film.setGenres(filmGenresMap.getOrDefault(filmId, new ArrayList<>()));
            film.setDirectors(filmDirectorsMap.getOrDefault(filmId, new ArrayList<>()));
        }).collect(Collectors.toList());
    }


    private Film addCredentials(Film film) {
        int filmId = film.getId();
        int mpaId = film.getMpa().getId();

        filmMpaStorage.addFilmMpa(filmId, mpaId);
        new LinkedHashSet<>(film.getGenres()).forEach(genre -> filmGenreStorage.addFilmGenre(filmId, genre.getId()));

        Mpa filmMpa = mpaStorage.getMpaById(mpaId);
        Collection<Genre> filmGenres = filmGenreStorage.getAllFilmGenresById(filmId);

        filmDirectorStorage.setFilmDirectors(film.getDirectors(), filmId);
        Collection<Director> directors = filmDirectorStorage.getFilmDirectors(filmId);

        return film.toBuilder().mpa(filmMpa).genres(filmGenres).directors(directors).build();
    }
}
