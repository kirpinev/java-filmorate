package ru.yandex.practicum.filmorate.storage.film;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.storage.filmDirector.FilmDirectorStorage;
import ru.yandex.practicum.filmorate.storage.filmGenre.FilmGenreStorage;
import ru.yandex.practicum.filmorate.storage.filmMpa.FilmMpaStorage;
import ru.yandex.practicum.filmorate.storage.mpa.MpaStorage;

import java.sql.PreparedStatement;
import java.util.*;

@Component
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {

    private static final String FILMS_SQL =
            "select f.*, m.id as mpa_id, m.name as mpa_name from films f left join film_mpas fm on f.id = fm.film_id " +
                    "left join mpas m on fm.mpa_id = m.id";
    private final JdbcTemplate jdbcTemplate;
    private final FilmMpaStorage filmMpaStorage;
    private final MpaStorage mpaStorage;
    private final FilmGenreStorage filmGenreStorage;
    private final FilmDirectorStorage filmDirectorStorage;

    @Override
    public Film createFilm(Film film) {
        final String sql = "insert into films (name, release_date, description, duration, rate) " +
                "values (?, ?, ?, ?, ?)";

        KeyHolder generatedKeyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement preparedStatement = connection.prepareStatement(
                    sql,
                    new String[]{"id"}
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

        return addExtraFields(film);
    }

    @Override
    public Film getFilmById(Integer filmId) {
        List<Film> films = jdbcTemplate.query(FILMS_SQL.concat(" where f.id = ?"), new FilmMapper(), filmId);

        if (!films.isEmpty()) {
            Collection<Genre> filmGenres = filmGenreStorage.getAllFilmGenresById(filmId);
            Collection<Director> directors = filmDirectorStorage.getFilmDirectors(filmId);

            return films.get(0).toBuilder().genres(filmGenres).directors(directors).build();
        }

        return null;
    }

    @Override
    public Collection<Film> getAllFilms() {
        Collection<Film> films = jdbcTemplate.query(FILMS_SQL, new FilmMapper());

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

        return addExtraFields(film);
    }

    @Override
    public Collection<Film> getPopularFilms(Integer count, Integer genreId, Integer year) {
        final Collection<String> params = new ArrayList<>();
        String sql =
                "select f.*, m.id as mpa_id, m.name as mpa_name from films f left join likes l on f.id = l.film_id " +
                        "left join film_mpas fm on f.id = fm.film_id left join mpas m on fm.mpa_id = m.id " +
                        "left join film_genres fg on f.id = fg.film_id %s group by f.name, f.id order by count(l.film_id) desc limit ?";

        if (Objects.nonNull(genreId)) {
            params.add(String.format("genre_id = %s", genreId));
        }

        if (Objects.nonNull(year)) {
            params.add(String.format("YEAR(release_date) = %s", year));
        }

        final String genreAndYearParams = !params.isEmpty() ? "where ".concat(String.join(" and ", params)) : "";
        Collection<Film> films = jdbcTemplate.query(String.format(sql, genreAndYearParams), new FilmMapper(), count);

        return setFilmGenresAndDirectors(films);
    }

    @Override
    public Collection<Film> getCommonFilms(Integer userId, Integer friendId) {
        String sql = "select * from (select f.*, m.id as mpa_id, m.name as mpa_name from films f " +
                "left join likes l on f.id = l.film_id left join film_mpas fm on f.id = fm.film_id " +
                "left join mpas m on fm.mpa_id = m.id left join film_genres fg on f.id = fg.film_id " +
                "group by f.name, f.id order by count(l.film_id)) f, likes l1, likes l2 " +
                "where f.id = l1.film_id and f.id = l2.film_id and l1.user_id = ? and l2.user_id = ?";
        Collection<Film> films = jdbcTemplate.query(sql, new FilmMapper(), userId, friendId);

        return setFilmGenresAndDirectors(films);
    }

    @Override
    public void deleteFilmById(Integer id) {
        final String sql = "delete from films where id = ?";
        int status = jdbcTemplate.update(sql, id);
        if (status == 0) {
            throw new NotFoundException("фильма с id " + id + " нет");
        }
    }

    @Override
    public Collection<Film> getDirectorFilms(Integer directorId, SortBy sortBy) {
        String yearOrderSql = "select f.*, " +
                "       m.id as mpa_id, " +
                "       m.name as mpa_name " +
                "from film_directors fd " +
                "         join films f on f.id = fd.film_id " +
                "         join film_mpas fm on f.id = fm.film_id " +
                "         join mpas m on fm.mpa_id = m.id " +
                "where director_id = ? " +
                "order by year(f.release_date)";

        String likesOrderSql = "select f.*,  " +
                "       m.id as mpa_id,  " +
                "       m.name as mpa_name,  " +
                "       (select count(*) from likes where fd.film_id = likes.film_id) as likes " +
                "from film_directors fd " +
                "join films f on f.id = fd.film_id " +
                "join film_mpas fm on f.id = fm.film_id " +
                "join mpas m on fm.mpa_id = m.id " +
                "where director_id = ? " +
                "order by likes desc;";

        Collection<Film> films =
                jdbcTemplate.query(sortBy == SortBy.LIKES ? likesOrderSql : yearOrderSql, new FilmMapper(), directorId);

        if (films.isEmpty()) {
            return Collections.emptyList();
        }

        return setFilmGenresAndDirectors(films);
    }

    @Override
    public Collection<Film> getUserRecommendations(Integer userId) {
        String sql = "SELECT l.user_id " +
                "FROM likes AS l " +
                "WHERE l.film_id IN " +
                "(SELECT film_id " +
                "FROM likes l1 " +
                "WHERE user_id = ?) and l.user_id <> ?" +
                "GROUP BY l.user_id " +
                "ORDER BY COUNT(l.film_id) " +
                "limit 1";

        final List<Integer> userIds = jdbcTemplate.query(sql, (rs, rowNum) -> rs.getInt("user_id"), userId, userId);

        if (userIds.isEmpty()) {
            return Collections.emptyList();
        }

        int similarUserId = userIds.get(0);

        String filmsFromUser = "SELECT f.*, m.id AS mpa_id, m.name AS mpa_name " +
                "FROM films AS f LEFT JOIN film_mpas AS fm ON f.id = fm.film_id " +
                "LEFT JOIN mpas AS m ON fm.mpa_id = m.id " +
                "LEFT JOIN likes AS l ON f.id = l.film_id " +
                "WHERE l.user_id = ?";

        List<Film> userFilms = jdbcTemplate.query(filmsFromUser, new FilmMapper(), userId);
        List<Film> similarUserFilms = jdbcTemplate.query(filmsFromUser, new FilmMapper(), similarUserId);

        similarUserFilms.removeAll(userFilms);

        return setFilmGenresAndDirectors(similarUserFilms);
    }

    private Collection<Film> setFilmGenresAndDirectors(Collection<Film> films) {
        Map<Integer, Collection<Genre>> filmGenresMap = filmGenreStorage.getAllFilmGenres(films);
        Map<Integer, Collection<Director>> filmDirectorsMap = filmDirectorStorage.getFilmDirectors(films);

        films.forEach(film -> {
            Integer filmId = film.getId();

            film.setGenres(filmGenresMap.getOrDefault(filmId, new ArrayList<>()));
            film.setDirectors(filmDirectorsMap.getOrDefault(filmId, new ArrayList<>()));
        });

        return films;
    }


    private Film addExtraFields(Film film) {
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
