package ru.yandex.practicum.filmorate.storage.film;

public final class FilmSqlQueries {
    private FilmSqlQueries() {
    }

    public static String CREATE_FILM = "insert into films (name, release_date, description, duration, rate) " +
            "values (?, ?, ?, ?, ?)";
    public static String FIND_FILM_BY_ID = "select * from films where id = ?";
    public static String FIND_ALL_FILMS = "select * from films";
    public static String UPDATE_FILM = "update films set name = ?, release_date = ?, description = ?, duration = ?, rate = ? where id = ?";
    public static String DELETE_FILM_BY_ID = "delete from films where id = ?";
}
